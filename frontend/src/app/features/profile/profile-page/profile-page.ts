import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';

import { AuthService } from '../../../core/services/auth.service';
import { ErrorMessageComponent } from '../../../shared/components/error-message/error-message';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner';
import { UserProfileService } from '../user-profile.service';

@Component({
  selector: 'app-profile-page',
  imports: [ReactiveFormsModule, ErrorMessageComponent, LoadingSpinnerComponent, DatePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './profile-page.html',
  styleUrl: './profile-page.css'
})
export class ProfilePageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly userProfileService = inject(UserProfileService);
  protected readonly authService = inject(AuthService);

  protected readonly saving = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly success = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    bio: [this.authService.currentUser()?.bio ?? ''],
    avatarUrl: [this.authService.currentUser()?.avatarUrl ?? '']
  });

  protected submit(): void {
    this.saving.set(true);
    this.error.set(null);
    this.success.set(false);

    const { bio, avatarUrl } = this.form.getRawValue();

    this.userProfileService.updateProfile({ bio: bio || null, avatarUrl: avatarUrl || null }).subscribe({
      next: () => {
        this.saving.set(false);
        this.success.set(true);
      },
      error: (err: HttpErrorResponse) => {
        this.saving.set(false);
        this.error.set(err.error?.message ?? 'Failed to update profile.');
      }
    });
  }
}
