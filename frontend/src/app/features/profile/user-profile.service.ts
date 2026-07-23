import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { UpdateProfileRequest, User } from '../../core/models/user.model';
import { AuthService } from '../../core/services/auth.service';

@Injectable({ providedIn: 'root' })
export class UserProfileService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  getProfile(userId: number): Observable<User> {
    return this.http.get<User>(`${environment.apiBaseUrl}/users/${userId}`);
  }

  updateProfile(request: UpdateProfileRequest): Observable<User> {
    return this.http.put<User>(`${environment.apiBaseUrl}/users/profile`, request).pipe(
      tap((user) => this.authService.setCurrentUser(user))
    );
  }
}
