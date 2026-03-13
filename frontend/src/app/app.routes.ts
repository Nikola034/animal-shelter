import { Routes } from '@angular/router';
import { AuthGuard } from './services/auth/auth-guard';
import { AdminGuard } from './services/auth/admin-guard';
import { CaretakerGuard } from './services/auth/caretaker-guard';

export const routes: Routes = [
  // Public routes
  {
    path: '',
    loadComponent: () => import('./components/auth/login/login').then(m => m.Login)
  },
  {
    path: 'register',
    loadComponent: () => import('./components/auth/register/register').then(m => m.Register)
  },

  // Protected routes with layout
  {
    path: 'app',
    loadComponent: () => import('./components/layout/layout').then(m => m.Layout),
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./components/dashboard/dashboard').then(m => m.Dashboard)
      },
      {
        path: 'users',
        loadComponent: () => import('./components/admin/user-management/user-management').then(m => m.UserManagement),
        canActivate: [AdminGuard]
      },
      {
        path: 'animals',
        loadComponent: () => import('./components/animals/animal-list/animal-list').then(m => m.AnimalList)
      },
      {
        path: 'animals/new',
        loadComponent: () => import('./components/animals/animal-form/animal-form').then(m => m.AnimalForm),
        canActivate: [CaretakerGuard]
      },
      {
        path: 'animals/:id',
        loadComponent: () => import('./components/animals/animal-detail/animal-detail').then(m => m.AnimalDetail)
      },
      {
        path: 'animals/:id/edit',
        loadComponent: () => import('./components/animals/animal-form/animal-form').then(m => m.AnimalForm),
        canActivate: [CaretakerGuard]
      },
      {
        path: 'activities',
        loadComponent: () => import('./components/activities/daily-tracking/daily-tracking').then(m => m.DailyTracking),
        canActivate: [CaretakerGuard]
      }
    ]
  },

  // Redirect unknown routes to login
  {
    path: '**',
    redirectTo: ''
  }
];
