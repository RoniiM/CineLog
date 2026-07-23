import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, effect, inject, input, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { AuthService } from '../../../core/services/auth.service';
import { Movie } from '../../../core/models/movie.model';
import { ErrorMessageComponent } from '../../../shared/components/error-message/error-message';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner';
import { StarRatingComponent } from '../../../shared/components/star-rating/star-rating';
import { DiaryService } from '../../diary/diary.service';
import { WatchlistService } from '../../watchlist/watchlist.service';
import { MovieService } from '../movie.service';

@Component({
  selector: 'app-movie-detail',
  imports: [ReactiveFormsModule, StarRatingComponent, LoadingSpinnerComponent, ErrorMessageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './movie-detail.html',
  styleUrl: './movie-detail.css'
})
export class MovieDetailComponent {
  tmdbId = input.required<string>();

  private readonly fb = inject(FormBuilder);
  private readonly movieService = inject(MovieService);
  private readonly authService = inject(AuthService);
  protected readonly diaryService = inject(DiaryService);
  protected readonly watchlistService = inject(WatchlistService);

  protected readonly movie = signal<Movie | null>(null);
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);

  protected readonly logSubmitting = signal(false);
  protected readonly logSuccess = signal(false);
  protected readonly logError = signal<string | null>(null);

  protected readonly watchlistBusy = signal(false);

  protected readonly logForm = this.fb.nonNullable.group({
    watchedDate: ['', Validators.required],
    rating: [0, [Validators.required, Validators.min(1)]],
    review: ['']
  });

  private readonly numericTmdbId = computed(() => Number(this.tmdbId()));
  protected readonly inWatchlist = computed(() => this.watchlistService.isInWatchlist(this.numericTmdbId()));

  constructor() {
    effect(() => {
      this.loadMovie(this.numericTmdbId());
    });

    effect(() => {
      const userId = this.authService.currentUser()?.id;
      if (userId) {
        this.watchlistService.loadWatchlist(userId);
      }
    });
  }

  protected setRating(value: number): void {
    this.logForm.controls.rating.setValue(value);
  }

  protected submitLog(): void {
    if (this.logForm.invalid) {
      this.logForm.markAllAsTouched();
      return;
    }

    this.logSubmitting.set(true);
    this.logError.set(null);
    this.logSuccess.set(false);

    const { watchedDate, rating, review } = this.logForm.getRawValue();

    this.diaryService
      .addEntry({ tmdbId: this.numericTmdbId(), watchedDate, rating, review: review || null })
      .subscribe({
        next: () => {
          this.logSubmitting.set(false);
          this.logSuccess.set(true);
          this.logForm.reset({ watchedDate: '', rating: 0, review: '' });
        },
        error: (err: HttpErrorResponse) => {
          this.logSubmitting.set(false);
          this.logError.set(err.error?.message ?? 'Failed to log movie.');
        }
      });
  }

  protected toggleWatchlist(): void {
    const movie = this.movie();
    if (!movie) {
      return;
    }

    this.watchlistBusy.set(true);

    if (this.inWatchlist()) {
      const item = this.watchlistService.items().find((i) => i.movie.tmdbId === movie.tmdbId);
      const movieId = item?.movie.id;
      if (movieId == null) {
        this.watchlistBusy.set(false);
        return;
      }

      this.watchlistService.removeFromWatchlist(movieId).subscribe({
        next: () => this.watchlistBusy.set(false),
        error: () => this.watchlistBusy.set(false)
      });
    } else {
      this.watchlistService.addToWatchlist(movie.tmdbId).subscribe({
        next: () => this.watchlistBusy.set(false),
        error: () => this.watchlistBusy.set(false)
      });
    }
  }

  private loadMovie(tmdbId: number): void {
    this.loading.set(true);
    this.error.set(null);

    this.movieService.getMovieDetails(tmdbId).subscribe({
      next: (movie) => {
        this.movie.set(movie);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load movie details.');
        this.loading.set(false);
      }
    });
  }
}
