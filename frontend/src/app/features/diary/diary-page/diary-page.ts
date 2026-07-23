import { ChangeDetectionStrategy, Component, effect, inject } from '@angular/core';

import { AuthService } from '../../../core/services/auth.service';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner';
import { StarRatingComponent } from '../../../shared/components/star-rating/star-rating';
import { DiaryService } from '../diary.service';

@Component({
  selector: 'app-diary-page',
  imports: [LoadingSpinnerComponent, StarRatingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './diary-page.html',
  styleUrl: './diary-page.css'
})
export class DiaryPageComponent {
  protected readonly diaryService = inject(DiaryService);
  private readonly authService = inject(AuthService);

  constructor() {
    effect(() => {
      const userId = this.authService.currentUser()?.id;
      if (userId) {
        this.diaryService.loadDiary(userId);
      }
    });
  }

  protected delete(entryId: number): void {
    this.diaryService.deleteEntry(entryId).subscribe();
  }
}
