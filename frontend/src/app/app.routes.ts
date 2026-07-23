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
          import('./features/discover/discover-page/discover-page').then((m) => m.DiscoverPageComponent)
      },
      {
        path: 'movies/:tmdbId',
        loadComponent: () =>
          import('./features/movies/movie-detail/movie-detail').then((m) => m.MovieDetailComponent)
      },
      {
        path: 'diary',
        loadComponent: () => import('./features/diary/diary-page/diary-page').then((m) => m.DiaryPageComponent)
      },
      {
        path: 'watchlist',
        loadComponent: () =>
          import('./features/watchlist/watchlist-page/watchlist-page').then((m) => m.WatchlistPageComponent)
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/profile/profile-page/profile-page').then((m) => m.ProfilePageComponent)
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
