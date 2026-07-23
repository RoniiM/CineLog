import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, of, switchMap, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RefreshTokenRequest, RegisterRequest } from '../models/auth.model';
import { User } from '../models/user.model';
import { TokenService } from './token.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly tokenService = inject(TokenService);

  private readonly _currentUser = signal<User | null>(null);
  readonly currentUser = this._currentUser.asReadonly();
  readonly isAuthenticated = computed(() => this._currentUser() !== null);

  initializeAuth(): Observable<User | null> {
    if (!this.tokenService.getAccessToken()) {
      return of(null);
    }

    return this.fetchCurrentUser().pipe(
      catchError(() => {
        this.tokenService.clearTokens();
        this._currentUser.set(null);
        return of(null);
      })
    );
  }

  login(request: LoginRequest): Observable<User> {
    return this.http.post<AuthResponse>(`${environment.apiBaseUrl}/auth/login`, request).pipe(
      tap((response) => this.tokenService.setTokens(response.accessToken, response.refreshToken)),
      switchMap(() => this.fetchCurrentUser())
    );
  }

  register(request: RegisterRequest): Observable<User> {
    return this.http.post<User>(`${environment.apiBaseUrl}/auth/register`, request);
  }

  refreshAccessToken(): Observable<AuthResponse> {
    const request: RefreshTokenRequest = { refreshToken: this.tokenService.getRefreshToken() ?? '' };
    return this.http.post<AuthResponse>(`${environment.apiBaseUrl}/auth/refresh`, request).pipe(
      tap((response) => this.tokenService.setTokens(response.accessToken, response.refreshToken))
    );
  }

  logout(): void {
    const request: RefreshTokenRequest = { refreshToken: this.tokenService.getRefreshToken() ?? '' };

    this.http
      .post(`${environment.apiBaseUrl}/auth/logout`, request)
      .pipe(catchError(() => of(null)))
      .subscribe(() => this.finishLogout());
  }

  private finishLogout(): void {
    this.tokenService.clearTokens();
    this._currentUser.set(null);
    this.router.navigate(['/login']);
  }

  private fetchCurrentUser(): Observable<User> {
    return this.http.get<User>(`${environment.apiBaseUrl}/users/me`).pipe(
      tap((user) => this._currentUser.set(user))
    );
  }
}
