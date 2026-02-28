import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { AuthService } from '../../services/auth/auth-service';
import { UserService } from '../../services/user/user-service';
import { UserResponse } from '../../dto/auth/UserResponse';
import { getRoleSeverity } from '../../dto/auth/UserRole';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    CardModule,
    TagModule
  ],
  templateUrl: 'dashboard.html'
})
export class Dashboard implements OnInit, OnDestroy {

  username = '';
  role = '';
  roleSeverity = '';
  isAdmin = false;

  totalUsers = 0;
  pendingUsers = 0;
  activeUsers = 0;
  inactiveUsers = 0;

  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.username = this.authService.getUsernameFromToken() || 'User';
    this.role = this.authService.getRoleFromToken() || '';
    this.roleSeverity = getRoleSeverity(this.authService.getRoleFromToken()!);
    this.isAdmin = this.authService.isAdmin();

    if (this.isAdmin) {
      this.loadUserStats();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadUserStats(): void {
    this.userService.getAllUsers()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          const users: UserResponse[] = response.users;
          this.totalUsers = response.total;
          this.pendingUsers = users.filter(u => u.status === 'Pending').length;
          this.activeUsers = users.filter(u => u.status === 'Active').length;
          this.inactiveUsers = users.filter(u => u.status === 'Inactive').length;
        },
        error: (err) => {
          console.error('Failed to load user stats:', err);
        }
      });
  }
}
