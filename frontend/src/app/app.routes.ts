import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';

export const routes: Routes = [
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/auth/login/login').then((m) => m.LoginComponent)
  },
  {
    path: 'register',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/auth/register/register').then((m) => m.RegisterComponent)
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./layout/main-layout/main-layout').then((m) => m.MainLayoutComponent),
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./shared/components/placeholder/placeholder').then((m) => m.PlaceholderComponent),
        data: { title: 'Discover' }
      },
      {
        path: 'diary',
        loadComponent: () =>
          import('./shared/components/placeholder/placeholder').then((m) => m.PlaceholderComponent),
        data: { title: 'Diary' }
      },
      {
        path: 'watchlist',
        loadComponent: () =>
          import('./shared/components/placeholder/placeholder').then((m) => m.PlaceholderComponent),
        data: { title: 'Watchlist' }
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./shared/components/placeholder/placeholder').then((m) => m.PlaceholderComponent),
        data: { title: 'Profile' }
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
