import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  BehaviorSubject,
  catchError,
  filter,
  finalize,
  Observable,
  switchMap,
  take,
  throwError,
} from 'rxjs';
import { AuthService } from './auth-service';
import { RefreshTokenDto } from '../../dto/auth/RefreshTokenDTO';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  private readonly excludedUrls = [
    '/auth/login',
    '/auth/register',
    '/auth/refresh-token'
  ];

  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (this.shouldExcludeUrl(request.url)) {
      return next.handle(request);
    }

    const authRequest = this.addAuthHeader(request);

    return next.handle(authRequest).pipe(
      catchError(error => {
        if (error instanceof HttpErrorResponse) {
          if (error.status === 401) {
            return this.handle401Error(authRequest, next);
          }

          if (error.status === 403) {
            console.warn('Access forbidden - insufficient permissions');
            return throwError(() => error);
          }
        }

        return throwError(() => error);
      })
    );
  }

  private addAuthHeader(request: HttpRequest<any>): HttpRequest<any> {
    const accessToken = this.authService.getAccessToken();

    const isFormData = request.body instanceof FormData;

    if (accessToken && this.authService.isAuthenticated()) {
      const headers: { [key: string]: string } = {
        Authorization: `Bearer ${accessToken}`
      };
      if (!isFormData) {
        headers['Content-Type'] = 'application/json';
      }
      return request.clone({ setHeaders: headers });
    }

    if (!isFormData) {
      return request.clone({
        setHeaders: {
          'Content-Type': 'application/json'
        }
      });
    }

    return request;
  }

  private shouldExcludeUrl(url: string): boolean {
    return this.excludedUrls.some(excludedUrl => url.includes(excludedUrl));
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      if (this.authService.getRefreshToken()) {
        return this.authService.refreshTokens().pipe(
          switchMap((tokens: RefreshTokenDto) => {
            this.isRefreshing = false;
            this.refreshTokenSubject.next(tokens.access_token);

            const newRequest = request.clone({
              setHeaders: {
                Authorization: `Bearer ${tokens.access_token}`
              }
            });

            return next.handle(newRequest);
          }),
          catchError((error) => {
            console.error('Token refresh failed:', error);
            this.isRefreshing = false;
            this.refreshTokenSubject.next(null);
            this.authService.logout();
            return throwError(() => error);
          }),
          finalize(() => {
            this.isRefreshing = false;
          })
        );
      } else {
        this.isRefreshing = false;
        this.authService.logout();
        return throwError(() => new Error('Authentication required'));
      }
    } else {
      return this.refreshTokenSubject.pipe(
        filter(token => token != null),
        take(1),
        switchMap(() => {
          const newRequest = this.addAuthHeader(request);
          return next.handle(newRequest);
        })
      );
    }
  }
}
