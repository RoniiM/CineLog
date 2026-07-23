import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { debounceTime, distinctUntilChanged, skip } from 'rxjs';

import { Movie } from '../../../core/models/movie.model';
import { ErrorMessageComponent } from '../../../shared/components/error-message/error-message';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner';
import { MovieCardComponent } from '../../../shared/components/movie-card/movie-card';
import { InfiniteScrollDirective } from '../../../shared/directives/infinite-scroll.directive';
import { MovieService } from '../../movies/movie.service';

@Component({
  selector: 'app-discover-page',
  imports: [MovieCardComponent, LoadingSpinnerComponent, ErrorMessageComponent, InfiniteScrollDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './discover-page.html',
  styleUrl: './discover-page.css'
})
export class DiscoverPageComponent {
  private readonly movieService = inject(MovieService);
  private readonly router = inject(Router);

  protected readonly query = signal('');
  protected readonly movies = signal<Movie[]>([]);
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);

  private page = 1;
  private totalPages = 1;

  constructor() {
    this.loadPage(true);

    toObservable(this.query)
      .pipe(skip(1), debounceTime(300), distinctUntilChanged())
      .subscribe(() => this.loadPage(true));
  }

  protected onSearchInput(value: string): void {
    this.query.set(value);
  }

  protected onScrolled(): void {
    if (this.loading() || this.page >= this.totalPages) {
      return;
    }
    this.page += 1;
    this.loadPage(false);
  }

  protected openMovie(tmdbId: number): void {
    this.router.navigate(['/movies', tmdbId]);
  }

  private loadPage(reset: boolean): void {
    if (reset) {
      this.page = 1;
    }

    this.loading.set(true);
    this.error.set(null);

    this.movieService.discoverMovies(this.page, this.query() || undefined).subscribe({
      next: (response) => {
        this.totalPages = response.totalPages;
        this.movies.update((current) => (reset ? response.content : [...current, ...response.content]));
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.error.set('Failed to load movies. Please try again.');
        this.loading.set(false);
      }
    });
  }
}
