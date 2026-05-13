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

  /**
   * Builds the side navigation per role exactly as described in chapter IV
   * of the thesis (Приказ имплементираног решења). Each role sees only the
   * sections that match its responsibilities — volunteers do not get a
   * dashboard, veterinarians do not see activity tracking, etc.
   */
  private buildMenu(): void {
    if (this.authService.isVolunteer()) {
      this.menuItems = this.buildVolunteerMenu();
    } else if (this.authService.isVeterinarian()) {
      this.menuItems = this.buildVeterinarianMenu();
    } else if (this.authService.isCaretaker()) {
      this.menuItems = this.buildCaretakerMenu();
    } else if (this.authService.isAdmin()) {
      this.menuItems = this.buildAdminMenu();
    } else {
      this.menuItems = [];
    }
  }

  // Volunteer: just browsing animals + the semantic search assistant.
  // Thesis ch. IV.В: "Бочни мени волонтера је минималан — садржи само
  // две главне ставке: „Животиње" и „Семантичка претрага"".
  private buildVolunteerMenu(): MenuItem[] {
    return [
      {
        label: 'Browse',
        items: [
          { label: 'Animals', icon: 'pi pi-heart', routerLink: '/app/animals' },
          { label: 'Semantic Search', icon: 'pi pi-sparkles', routerLink: '/app/search' },
        ]
      }
    ];
  }

  // Caretaker: operational role — dashboard, animal CRUD, daily tracking,
  // feeding, semantic search and analytics. Thesis ch. IV.Г.
  private buildCaretakerMenu(): MenuItem[] {
    return [
      {
        label: 'Main',
        items: [
          { label: 'Dashboard', icon: 'pi pi-home', routerLink: '/app' },
          { label: 'Animals', icon: 'pi pi-heart', routerLink: '/app/animals' },
        ]
      },
      {
        label: 'Daily Operations',
        items: [
          { label: 'Daily Tracking', icon: 'pi pi-chart-line', routerLink: '/app/activities' },
          { label: 'Feeding', icon: 'pi pi-bolt', routerLink: '/app/feeding' },
        ]
      },
      {
        label: 'Tools',
        items: [
          { label: 'Semantic Search', icon: 'pi pi-sparkles', routerLink: '/app/search' },
          { label: 'Analytics', icon: 'pi pi-chart-bar', routerLink: '/app/analytics' },
        ]
      }
    ];
  }

  // Veterinarian: medical-centric — dashboard, read-only animals, medical
  // records and health analytics. Thesis ch. IV.Д.
  private buildVeterinarianMenu(): MenuItem[] {
    return [
      {
        label: 'Main',
        items: [
          { label: 'Dashboard', icon: 'pi pi-home', routerLink: '/app' },
          { label: 'Animals', icon: 'pi pi-heart', routerLink: '/app/animals' },
        ]
      },
      {
        label: 'Medical',
        items: [
          { label: 'Medical Records', icon: 'pi pi-briefcase', routerLink: '/app/medical-records' },
          { label: 'Health Analytics', icon: 'pi pi-chart-bar', routerLink: '/app/analytics' },
        ]
      }
    ];
  }

  // Admin: everything caretakers see, plus medical records, user management
  // and pending registrations. Thesis ch. IV.Ђ.
  private buildAdminMenu(): MenuItem[] {
    return [
      {
        label: 'Main',
        items: [
          { label: 'Dashboard', icon: 'pi pi-home', routerLink: '/app' },
          { label: 'Animals', icon: 'pi pi-heart', routerLink: '/app/animals' },
        ]
      },
      {
        label: 'Daily Operations',
        items: [
          { label: 'Daily Tracking', icon: 'pi pi-chart-line', routerLink: '/app/activities' },
          { label: 'Feeding', icon: 'pi pi-bolt', routerLink: '/app/feeding' },
        ]
      },
      {
        label: 'Medical',
        items: [
          { label: 'Medical Records', icon: 'pi pi-briefcase', routerLink: '/app/medical-records' },
        ]
      },
      {
        label: 'Tools',
        items: [
          { label: 'Semantic Search', icon: 'pi pi-sparkles', routerLink: '/app/search' },
          { label: 'Analytics', icon: 'pi pi-chart-bar', routerLink: '/app/analytics' },
        ]
      },
      {
        label: 'Administration',
        items: [
          { label: 'Users', icon: 'pi pi-users', routerLink: '/app/users' },
          { label: 'Pending Registrations', icon: 'pi pi-user-plus', routerLink: '/app/admin/pending-users' },
        ]
      }
    ];
  }
}
