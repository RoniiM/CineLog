import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';

import { Movie } from '../../../core/models/movie.model';

@Component({
  selector: 'app-movie-card',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './movie-card.html',
  styleUrl: './movie-card.css'
})
export class MovieCardComponent {
  movie = input.required<Movie>();
  clicked = output<number>();

  protected readonly releaseYear = computed(() => {
    const date = this.movie().releaseDate;
    return date ? new Date(date).getFullYear().toString() : '—';
  });

  protected onClick(): void {
    this.clicked.emit(this.movie().tmdbId);
  }
}
