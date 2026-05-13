import { CanActivate, Router } from '@angular/router';
import { AuthService } from './auth-service';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class VeterinarianGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (this.authService.canManageMedicalRecords()) {
      return true;
    } else {
      this.router.navigate(['/app/animals']);
      return false;
    }
  }
}
