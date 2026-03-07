import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarModule } from 'primeng/sidebar';
import { MenuModule } from 'primeng/menu';
import { MenuItem } from 'primeng/api';
import { AuthService } from '../../../services/auth/auth-service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    SidebarModule,
    MenuModule
  ],
  templateUrl: 'sidebar.html'
})
export class Sidebar {

  @Input() visible = true;

  menuItems: MenuItem[] = [];

  constructor(private authService: AuthService) {
    this.buildMenu();
  }

  private buildMenu(): void {
    this.menuItems = [
      {
        label: 'Main',
        items: [
          {
            label: 'Dashboard',
            icon: 'pi pi-home',
            routerLink: '/app'
          },
          {
            label: 'Animals',
            icon: 'pi pi-heart',
            routerLink: '/app/animals'
          }
        ]
      }
    ];

    if (this.authService.isAdmin()) {
      this.menuItems.push({
        label: 'Administration',
        items: [
          {
            label: 'User Management',
            icon: 'pi pi-users',
            routerLink: '/app/users'
          }
        ]
      });
    }
  }
}
