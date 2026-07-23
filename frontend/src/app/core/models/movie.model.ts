export interface Movie {
  id: number | null;
  tmdbId: number;
  title: string;
  overview: string | null;
  posterUrl: string | null;
  backdropUrl: string | null;
  releaseDate: string | null;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  totalPages: number;
  totalResults: number;
}
