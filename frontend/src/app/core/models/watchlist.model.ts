import { Movie } from './movie.model';

export interface WatchlistItem {
  id: number;
  movie: Movie;
  addedAt: string;
}
