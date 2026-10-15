import { isPlatformBrowser } from '@angular/common';
import { inject, Injectable, PLATFORM_ID } from '@angular/core';
import { Client } from '@stomp/stompjs';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class WebsocketService {
  private readonly auth = inject(AuthService);
  private readonly platformId = inject(PLATFORM_ID);
  private client?: Client;
  private connectedUserId?: number;

  connect(userId: number, onNotification: () => void, onError: () => void): void {
    if (!isPlatformBrowser(this.platformId) || this.connectedUserId === userId) return;

    this.disconnect();
    this.connectedUserId = userId;
    if (!('global' in globalThis)) Object.defineProperty(globalThis, 'global', { value: globalThis });
    void import('sockjs-client').then(({ default: SockJS }) => {
      if (this.connectedUserId !== userId) return;

      const client = new Client({
        webSocketFactory: () => new SockJS('http://localhost:8080/ws') as WebSocket,
        connectHeaders: { Authorization: `Bearer ${this.auth.token()}` },
        reconnectDelay: 5000,
        debug: () => undefined
      });
      client.onConnect = () => {
        client.subscribe(`/topic/notifications/${userId}`, onNotification);
      };
      client.onStompError = onError;
      client.onWebSocketError = onError;
      this.client = client;
      void client.activate();
    }).catch(() => {
      if (this.connectedUserId === userId) {
        this.connectedUserId = undefined;
        onError();
      }
    });
  }

  disconnect(): void {
    if (this.client) void this.client.deactivate();
    this.client = undefined;
    this.connectedUserId = undefined;
  }
}
