import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';

@Component({
  selector: 'app-star-rating',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './star-rating.html',
  styleUrl: './star-rating.css'
})
export class StarRatingComponent {
  rating = input(0);
  isReadonly = input(false, { alias: 'readonly' });
  ratingChange = output<number>();

  protected readonly stars = [1, 2, 3, 4, 5];

  protected selectRating(value: number): void {
    if (this.isReadonly()) {
      return;
    }
    this.ratingChange.emit(value);
  }
}
