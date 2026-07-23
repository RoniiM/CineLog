import { HttpErrorResponse, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, catchError, filter, switchMap, take, throwError } from 'rxjs';

import { AuthService } from '../services/auth.service';
import { TokenService } from '../services/token.service';

let isRefreshing = false;
const refreshedAccessToken$ = new BehaviorSubject<string | null>(null);

function withToken(req: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
  return req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
}

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const tokenService = inject(TokenService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: unknown) => {
      const isAuthEndpoint = req.url.includes('/auth/');
      if (!(error instanceof HttpErrorResponse) || error.status !== 401 || isAuthEndpoint) {
        return throwError(() => error);
      }

      if (!isRefreshing) {
        isRefreshing = true;
        refreshedAccessToken$.next(null);

        return authService.refreshAccessToken().pipe(
          switchMap((response) => {
            isRefreshing = false;
            refreshedAccessToken$.next(response.accessToken);
            return next(withToken(req, response.accessToken));
          }),
          catchError((refreshError: unknown) => {
            isRefreshing = false;
            tokenService.clearTokens();
            router.navigate(['/login']);
            return throwError(() => refreshError);
          })
        );
      }

      return refreshedAccessToken$.pipe(
        filter((token): token is string => token !== null),
        take(1),
        switchMap((token) => next(withToken(req, token)))
      );
    })
  );
};
