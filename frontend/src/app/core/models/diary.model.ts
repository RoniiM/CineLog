import { Movie } from './movie.model';

export interface DiaryEntry {
  id: number;
  movie: Movie;
  watchedDate: string;
  rating: number;
  review: string | null;
  createdAt: string;
}

export interface DiaryEntryRequest {
  tmdbId: number;
  watchedDate: string;
  rating: number;
  review: string | null;
}
