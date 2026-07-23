import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Movie, PageResponse } from '../../core/models/movie.model';

@Injectable({ providedIn: 'root' })
export class MovieService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/movies`;

  discoverMovies(page: number, query?: string): Observable<PageResponse<Movie>> {
    if (query && query.trim().length > 0) {
      const params = new HttpParams().set('query', query).set('page', page);
      return this.http.get<PageResponse<Movie>>(`${this.baseUrl}/search`, { params });
    }

    const params = new HttpParams().set('page', page);
    return this.http.get<PageResponse<Movie>>(`${this.baseUrl}/discover`, { params });
  }

  getMovieDetails(tmdbId: number): Observable<Movie> {
    return this.http.get<Movie>(`${this.baseUrl}/${tmdbId}`);
  }
}
