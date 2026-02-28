import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { LoginRequest } from '../../dto/auth/LoginRequest';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TokensDto } from '../../dto/auth/TokensDTO';
import { RegisterRequestDto } from '../../dto/auth/RegisterRequestDTO';
import { RegisterResponseDto } from '../../dto/auth/RegisterResponseDTO';
import { RefreshTokenDto } from '../../dto/auth/RefreshTokenDTO';
import { User } from '../../model/auth/user';
import { UserRole } from '../../dto/auth/UserRole';

export interface StringBody {
  message: string;
}

export interface DecodedToken {
  sub: string;
  exp: number;
  iat: number;
  username?: string;
  role?: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly TOKEN_KEY = 'animal_shelter_tokens';

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.initializeAuth();
  }

  private initializeAuth(): void {
    const tokens = this.getStoredTokens();

    if (tokens && this.isTokenValid(tokens.access_token)) {
      this.isAuthenticatedSubject.next(true);
      if (tokens.user) {
        this.currentUserSubject.next(tokens.user);
      }
    } else {
      this.clearAuthData();
    }
  }

  login(credentials: LoginRequest): Observable<TokensDto> {
    return this.http
      .post<TokensDto>(`${environment.apiGatewayUrl}/auth/login`, credentials)
      .pipe(
        tap((response) => {
          this.handleLoginSuccess(response);
        }),
        catchError((error) => {
          console.error('Login failed:', error);
          return throwError(() => error);
        })
      );
  }

  register(userData: RegisterRequestDto): Observable<RegisterResponseDto> {
    return this.http
      .post<RegisterResponseDto>(`${environment.apiGatewayUrl}/auth/register`, userData)
      .pipe(
        catchError((error) => {
          console.error('Registration failed:', error);
          return throwError(() => error);
        })
      );
  }

  private handleLoginSuccess(response: TokensDto): void {
    localStorage.setItem(this.TOKEN_KEY, JSON.stringify(response));

    this.isAuthenticatedSubject.next(true);
    if (response.user) {
      this.currentUserSubject.next(response.user);
    }

    this.router.navigate(['/app']);
  }

  logout(): void {
    this.http.post(`${environment.apiGatewayUrl}/auth/logout`, {}).subscribe({
      complete: () => {
        this.clearAuthData();
        this.router.navigate(['']);
      },
      error: () => {
        this.clearAuthData();
        this.router.navigate(['']);
      }
    });
  }

  private clearAuthData(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
  }

  getAccessToken(): string | null {
    const tokens = this.getStoredTokens();
    return tokens?.access_token || null;
  }

  getRefreshToken(): string | null {
    const tokens = this.getStoredTokens();
    return tokens?.refresh_token || null;
  }

  private getStoredTokens(): TokensDto | null {
    try {
      const tokens = localStorage.getItem(this.TOKEN_KEY);
      return tokens ? JSON.parse(tokens) : null;
    } catch (error) {
      console.error('Error parsing stored tokens:', error);
      return null;
    }
  }

  isTokenValid(token: string): boolean {
    try {
      const decoded = this.decodeToken(token);
      const currentTime = Math.floor(Date.now() / 1000);
      return decoded.exp > currentTime;
    } catch (error) {
      return false;
    }
  }

  private decodeToken(token: string): DecodedToken {
    try {
      const payload = token.split('.')[1];
      const decodedPayload = atob(payload);
      return JSON.parse(decodedPayload);
    } catch (error) {
      throw new Error('Invalid token format');
    }
  }

  isAuthenticated(): boolean {
    const tokens = this.getStoredTokens();
    return !!(tokens && this.isTokenValid(tokens.access_token));
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  getStoredUser(): User | null {
    const tokens = this.getStoredTokens();
    return tokens?.user || null;
  }

  refreshTokens(): Observable<RefreshTokenDto> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    return this.http
      .post<RefreshTokenDto>(
        `${environment.apiGatewayUrl}/auth/refresh-token`,
        { refresh_token: refreshToken }
      )
      .pipe(
        tap((tokens) => {
          const currentTokens = this.getStoredTokens();
          const updatedTokens = {
            ...currentTokens,
            access_token: tokens.access_token,
            refresh_token: tokens.refresh_token,
            expires_in: tokens.expires_in
          };
          localStorage.setItem(this.TOKEN_KEY, JSON.stringify(updatedTokens));
        }),
        catchError((error) => {
          console.error('Token refresh failed:', error);
          this.clearAuthData();
          this.router.navigate(['']);
          return throwError(() => error);
        })
      );
  }

  getTokenExpiration(): Date | null {
    const accessToken = this.getAccessToken();
    if (!accessToken) return null;

    try {
      const decoded = this.decodeToken(accessToken);
      return new Date(decoded.exp * 1000);
    } catch (error) {
      return null;
    }
  }

  shouldRefreshToken(): boolean {
    const expiration = this.getTokenExpiration();
    if (!expiration) return false;

    const fiveMinutesFromNow = new Date(Date.now() + 5 * 60 * 1000);
    return expiration <= fiveMinutesFromNow;
  }

  isVolunteer(): boolean {
    return this.getRoleFromToken() === 'Volunteer';
  }

  isCaretaker(): boolean {
    return this.getRoleFromToken() === 'Caretaker';
  }

  isVeterinarian(): boolean {
    return this.getRoleFromToken() === 'Veterinarian';
  }

  isAdmin(): boolean {
    return this.getRoleFromToken() === 'Admin';
  }

  canManageAnimals(): boolean {
    const role = this.getRoleFromToken();
    return role === 'Admin' || role === 'Caretaker';
  }

  canManageMedicalRecords(): boolean {
    const role = this.getRoleFromToken();
    return role === 'Admin' || role === 'Veterinarian';
  }

  canTrackActivities(): boolean {
    const role = this.getRoleFromToken();
    return role === 'Admin' || role === 'Caretaker' || role === 'Veterinarian';
  }

  canView(): boolean {
    return this.isAuthenticated();
  }

  getRoleFromToken(): UserRole | undefined {
    const tokens = this.getStoredTokens();
    if (tokens?.access_token) {
      try {
        const tokenInfo = this.decodeToken(tokens.access_token);
        if (tokenInfo.role) {
          return tokenInfo.role.charAt(0).toUpperCase() + tokenInfo.role.slice(1) as UserRole;
        }
      } catch {
        return undefined;
      }
    }
    if (tokens?.user?.role) {
      return tokens.user.role.charAt(0).toUpperCase() + tokens.user.role.slice(1) as UserRole;
    }
    return undefined;
  }

  getUsernameFromToken(): string | undefined {
    const tokens = this.getStoredTokens();
    if (tokens?.access_token) {
      try {
        const tokenInfo = this.decodeToken(tokens.access_token);
        return tokenInfo.username;
      } catch {
        return undefined;
      }
    }
    return tokens?.user?.username;
  }
}
