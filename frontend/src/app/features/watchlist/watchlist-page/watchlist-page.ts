import { ChangeDetectionStrategy, Component, effect, inject } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner';
import { MovieCardComponent } from '../../../shared/components/movie-card/movie-card';
import { WatchlistService } from '../watchlist.service';

@Component({
  selector: 'app-watchlist-page',
  imports: [MovieCardComponent, LoadingSpinnerComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './watchlist-page.html',
  styleUrl: './watchlist-page.css'
})
export class WatchlistPageComponent {
  protected readonly watchlistService = inject(WatchlistService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  constructor() {
    effect(() => {
      const userId = this.authService.currentUser()?.id;
      if (userId) {
        this.watchlistService.loadWatchlist(userId);
      }
    });
  }

  protected openMovie(tmdbId: number): void {
    this.router.navigate(['/movies', tmdbId]);
  }

  protected remove(movieId: number | null, event: Event): void {
    event.stopPropagation();
    if (movieId == null) {
      return;
    }
    this.watchlistService.removeFromWatchlist(movieId).subscribe();
  }
}
