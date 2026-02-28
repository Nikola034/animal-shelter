import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToolbarModule } from 'primeng/toolbar';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { AuthService } from '../../../services/auth/auth-service';
import { getRoleSeverity } from '../../../dto/auth/UserRole';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [
    CommonModule,
    ToolbarModule,
    ButtonModule,
    TagModule
  ],
  templateUrl: 'topbar.html'
})
export class Topbar {

  @Output() toggleSidebar = new EventEmitter<void>();

  constructor(public authService: AuthService) {}

  get username(): string {
    return this.authService.getUsernameFromToken() || 'User';
  }

  get role(): string {
    return this.authService.getRoleFromToken() || '';
  }

  get roleSeverity(): string {
    return getRoleSeverity(this.authService.getRoleFromToken()!);
  }

  onToggleSidebar(): void {
    this.toggleSidebar.emit();
  }

  onLogout(): void {
    this.authService.logout();
  }
}
