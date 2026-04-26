import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { DividerModule } from 'primeng/divider';
import { TagModule } from 'primeng/tag';
import { PasswordModule } from 'primeng/password';
import { MessageService } from 'primeng/api';

import { UserService } from '../../services/user/user-service';
import { AuthService } from '../../services/auth/auth-service';
import { UserResponse } from '../../dto/auth/UserResponse';
import { getRoleSeverity } from '../../dto/auth/UserRole';
import { getStatusSeverity } from '../../dto/auth/UserStatus';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CardModule,
    InputTextModule,
    ButtonModule,
    ToastModule,
    DividerModule,
    TagModule,
    PasswordModule,
  ],
  providers: [MessageService],
  templateUrl: 'profile.html',
})
export class Profile implements OnInit {
  user: UserResponse | null = null;
  loading = true;

  // Profile form
  name = '';
  email = '';
  savingProfile = false;

  // Password form
  currentPassword = '';
  newPassword = '';
  confirmPassword = '';
  savingPassword = false;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private messageService: MessageService,
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.loading = true;
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.user = user;
        this.name = user.name || '';
        this.email = user.email;
        this.loading = false;
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to load profile' });
        this.loading = false;
      },
    });
  }

  saveProfile(): void {
    if (!this.email.trim()) {
      this.messageService.add({ severity: 'warn', summary: 'Validation', detail: 'Email is required' });
      return;
    }

    this.savingProfile = true;
    this.userService.updateMyProfile({ name: this.name, email: this.email }).subscribe({
      next: (user) => {
        this.user = user;
        this.name = user.name || '';
        this.email = user.email;
        this.savingProfile = false;
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Profile updated' });
      },
      error: (err) => {
        this.savingProfile = false;
        const msg = err.error?.message || 'Failed to update profile';
        this.messageService.add({ severity: 'error', summary: 'Error', detail: msg });
      },
    });
  }

  changePassword(): void {
    if (!this.currentPassword || !this.newPassword) {
      this.messageService.add({ severity: 'warn', summary: 'Validation', detail: 'All password fields are required' });
      return;
    }
    if (this.newPassword.length < 6) {
      this.messageService.add({ severity: 'warn', summary: 'Validation', detail: 'New password must be at least 6 characters' });
      return;
    }
    if (this.newPassword !== this.confirmPassword) {
      this.messageService.add({ severity: 'warn', summary: 'Validation', detail: 'Passwords do not match' });
      return;
    }

    this.savingPassword = true;
    this.userService
      .changeMyPassword({
        current_password: this.currentPassword,
        new_password: this.newPassword,
      })
      .subscribe({
        next: () => {
          this.savingPassword = false;
          this.currentPassword = '';
          this.newPassword = '';
          this.confirmPassword = '';
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Password changed successfully' });
        },
        error: (err) => {
          this.savingPassword = false;
          const msg = err.error?.message || 'Failed to change password';
          this.messageService.add({ severity: 'error', summary: 'Error', detail: msg });
        },
      });
  }

  getRoleSeverity(role: string): string {
    return getRoleSeverity(role as any);
  }

  getStatusSeverity(status: string): string {
    return getStatusSeverity(status as any);
  }
}
