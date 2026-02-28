import { Routes } from '@angular/router';
import { AuthGuard } from './services/auth/auth-guard';
import { AdminGuard } from './services/auth/admin-guard';

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
      }
    ]
  },

  // Redirect unknown routes to login
  {
    path: '**',
    redirectTo: ''
  }
];
