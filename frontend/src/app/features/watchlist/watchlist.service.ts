import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { WatchlistItem } from '../../core/models/watchlist.model';

@Injectable({ providedIn: 'root' })
export class WatchlistService {
  private readonly http = inject(HttpClient);

  private readonly _items = signal<WatchlistItem[]>([]);
  readonly items = this._items.asReadonly();

  private readonly _loading = signal(false);
  readonly loading = this._loading.asReadonly();

  loadWatchlist(userId: number): void {
    this._loading.set(true);

    this.http.get<WatchlistItem[]>(`${environment.apiBaseUrl}/users/${userId}/watchlist`).subscribe({
      next: (items) => {
        this._items.set(items);
        this._loading.set(false);
      },
      error: () => this._loading.set(false)
    });
  }

  addToWatchlist(tmdbId: number): Observable<WatchlistItem> {
    return this.http.post<WatchlistItem>(`${environment.apiBaseUrl}/watchlist`, { tmdbId }).pipe(
      tap((item) => this._items.update((items) => [item, ...items]))
    );
  }

  removeFromWatchlist(movieId: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiBaseUrl}/watchlist/${movieId}`).pipe(
      tap(() => this._items.update((items) => items.filter((item) => item.movie.id !== movieId)))
    );
  }

  isInWatchlist(tmdbId: number): boolean {
    return this._items().some((item) => item.movie.tmdbId === tmdbId);
  }
}
