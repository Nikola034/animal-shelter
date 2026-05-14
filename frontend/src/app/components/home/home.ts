import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { DividerModule } from 'primeng/divider';

interface RoleCard {
  label: string;
  icon: string;
  iconBg: string;
  iconColor: string;
  description: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    CardModule,
    ButtonModule,
    DividerModule,
  ],
  templateUrl: 'home.html',
})
export class Home {

  // Short, non-technical role descriptions for prospective registrants —
  // matches the documented copy in thesis chapter IV.Б.1.
  readonly roles: RoleCard[] = [
    {
      label: 'Volunteer',
      icon: 'pi pi-users',
      iconBg: 'bg-blue-100',
      iconColor: 'text-blue-600',
      description:
        'Browse all animals in the shelter and use AI-powered semantic search ' +
        'to help potential adopters find the right match. View-only access.',
    },
    {
      label: 'Caretaker',
      icon: 'pi pi-clipboard',
      iconBg: 'bg-pink-100',
      iconColor: 'text-pink-600',
      description:
        'Run the shelter day-to-day: register new animals, log daily ' +
        'measurements, activities and feedings, and follow operational ' +
        'analytics.',
    },
    {
      label: 'Veterinarian',
      icon: 'pi pi-briefcase',
      iconBg: 'bg-green-100',
      iconColor: 'text-green-600',
      description:
        'Maintain medical records — vaccinations, diagnoses, treatments — ' +
        'and monitor health analytics for every animal in the shelter.',
    },
  ];

  constructor(private router: Router) {}

  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  goToRegister(): void {
    this.router.navigate(['/register']);
  }
}
