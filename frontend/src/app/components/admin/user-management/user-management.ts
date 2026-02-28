import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';

import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { CardModule } from 'primeng/card';
import { MessageService, ConfirmationService } from 'primeng/api';

import { UserService } from '../../../services/user/user-service';
import { UserResponse } from '../../../dto/auth/UserResponse';
import { UserRole, USER_ROLE_OPTIONS, getRoleSeverity } from '../../../dto/auth/UserRole';
import { UserStatus, USER_STATUS_OPTIONS, getStatusSeverity } from '../../../dto/auth/UserStatus';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    TagModule,
    ButtonModule,
    DropdownModule,
    ToastModule,
    ConfirmDialogModule,
    CardModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: 'user-management.html'
})
export class UserManagement implements OnInit, OnDestroy {

  users: UserResponse[] = [];
  loading = true;

  // Filter
  statusFilter: UserStatus | null = null;
  statusFilterOptions = [
    { label: 'All Statuses', value: null },
    ...USER_STATUS_OPTIONS
  ];

  // Dropdown options for inline editing
  roleOptions = USER_ROLE_OPTIONS;
  statusOptions = USER_STATUS_OPTIONS;

  private destroy$ = new Subject<void>();

  constructor(
    private userService: UserService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadUsers(): void {
    this.loading = true;

    const request = this.statusFilter
      ? this.userService.getUsersByStatus(this.statusFilter)
      : this.userService.getAllUsers();

    request.pipe(takeUntil(this.destroy$)).subscribe({
      next: (response) => {
        this.users = response.users;
        this.loading = false;
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load users',
          life: 5000
        });
        this.loading = false;
      }
    });
  }

  onStatusFilterChange(): void {
    this.loadUsers();
  }

  onStatusChange(user: UserResponse, newStatus: UserStatus): void {
    this.userService.updateUserStatus(user.id, newStatus)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updated) => {
          this.messageService.add({
            severity: 'success',
            summary: 'Status Updated',
            detail: `${user.username} is now ${newStatus}`,
            life: 3000
          });
          this.loadUsers();
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: err.error?.message || 'Failed to update status',
            life: 5000
          });
        }
      });
  }

  onRoleChange(user: UserResponse, newRole: UserRole): void {
    this.userService.updateUserRole(user.id, newRole)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updated) => {
          this.messageService.add({
            severity: 'success',
            summary: 'Role Updated',
            detail: `${user.username} is now ${newRole}`,
            life: 3000
          });
          this.loadUsers();
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: err.error?.message || 'Failed to update role',
            life: 5000
          });
        }
      });
  }

  confirmDelete(user: UserResponse): void {
    this.confirmationService.confirm({
      message: `Are you sure you want to delete user "${user.username}"? This action cannot be undone.`,
      header: 'Confirm Deletion',
      icon: 'pi pi-exclamation-triangle',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.deleteUser(user);
      }
    });
  }

  private deleteUser(user: UserResponse): void {
    this.userService.deleteUser(user.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'User Deleted',
            detail: `${user.username} has been deleted`,
            life: 3000
          });
          this.loadUsers();
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: err.error?.message || 'Failed to delete user',
            life: 5000
          });
        }
      });
  }

  getStatusSeverity(status: UserStatus): string {
    return getStatusSeverity(status);
  }

  getRoleSeverity(role: UserRole): string {
    return getRoleSeverity(role);
  }
}
