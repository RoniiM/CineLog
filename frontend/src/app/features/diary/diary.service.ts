import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { DiaryEntry, DiaryEntryRequest } from '../../core/models/diary.model';

@Injectable({ providedIn: 'root' })
export class DiaryService {
  private readonly http = inject(HttpClient);

  private readonly _entries = signal<DiaryEntry[]>([]);
  readonly entries = this._entries.asReadonly();

  private readonly _loading = signal(false);
  readonly loading = this._loading.asReadonly();

  loadDiary(userId: number): void {
    this._loading.set(true);

    this.http.get<DiaryEntry[]>(`${environment.apiBaseUrl}/users/${userId}/diary`).subscribe({
      next: (entries) => {
        this._entries.set(entries);
        this._loading.set(false);
      },
      error: () => this._loading.set(false)
    });
  }

  addEntry(request: DiaryEntryRequest): Observable<DiaryEntry> {
    return this.http.post<DiaryEntry>(`${environment.apiBaseUrl}/diary`, request).pipe(
      tap((entry) => this._entries.update((entries) => [entry, ...entries]))
    );
  }

  deleteEntry(entryId: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiBaseUrl}/diary/${entryId}`).pipe(
      tap(() => this._entries.update((entries) => entries.filter((entry) => entry.id !== entryId)))
    );
  }
}
