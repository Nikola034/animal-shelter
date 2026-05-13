import { CanActivate, Router } from '@angular/router';
import { AuthService } from './auth-service';
import { Injectable } from '@angular/core';

/**
 * Volunteers do not have a dashboard view — the documented workflow sends them
 * straight to the animal list on login. This guard intercepts /app (the
 * dashboard path) and redirects volunteers to /app/animals.
 */
@Injectable({
  providedIn: 'root'
})
export class DashboardRedirectGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (this.authService.isVolunteer()) {
      this.router.navigate(['/app/animals']);
      return false;
    }
    return true;
  }
}
