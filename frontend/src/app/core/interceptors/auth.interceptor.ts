import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

import { TokenService } from '../services/token.service';

const SKIP_AUTH_HEADER_PATHS = ['/auth/login', '/auth/register', '/auth/refresh'];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (SKIP_AUTH_HEADER_PATHS.some((path) => req.url.includes(path))) {
    return next(req);
  }

  const accessToken = inject(TokenService).getAccessToken();
  if (!accessToken) {
    return next(req);
  }

  return next(
    req.clone({
      setHeaders: { Authorization: `Bearer ${accessToken}` }
    })
  );
};
