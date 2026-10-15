import { HttpClient } from '@angular/common/http';
import { afterNextRender, inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Observable, tap } from 'rxjs';
import { AuthResponse } from '../../shared/models/workspace.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly apiUrl = 'http://localhost:8080/api/auth';
  readonly token = signal('');
  readonly userEmail = signal('');

  constructor() {
    afterNextRender(() => {
      if (isPlatformBrowser(this.platformId)) {
        this.token.set(localStorage.getItem('devhive-token') ?? '');
        this.userEmail.set(localStorage.getItem('devhive-email') ?? '');
      }
    });
  }

  login(credentials: { email: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        this.token.set(response.token);
        this.userEmail.set(credentials.email);
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('devhive-token', response.token);
          localStorage.setItem('devhive-email', credentials.email);
        }
      })
    );
  }

  register(details: { name: string; email: string; password: string }): Observable<string> {
    return this.http.post(`${this.apiUrl}/register`, details, { responseType: 'text' });
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('devhive-token');
      localStorage.removeItem('devhive-email');
    }
    this.token.set('');
    this.userEmail.set('');
  }
}
