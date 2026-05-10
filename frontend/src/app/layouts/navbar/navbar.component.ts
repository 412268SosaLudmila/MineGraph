import { Component, EventEmitter, Output } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'mg-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
  @Output() toggleSidebar = new EventEmitter<void>();

  currentTime = new Date();
  searchQuery = '';

  constructor(public auth: AuthService, private router: Router) {
    setInterval(() => this.currentTime = new Date(), 1000);
  }

  getPageTitle(): string {
    const url = this.router.url;
    if (url.includes('dashboard')) return 'Dashboard';
    if (url.includes('players')) return 'Gestión de Jugadores';
    if (url.includes('clans')) return 'Gestión de Clanes';
    if (url.includes('economy')) return 'Panel Económico';
    if (url.includes('events')) return 'Eventos del Servidor';
    if (url.includes('graph')) return 'Mapa de Relaciones';
    return 'MineGraph';
  }

  logout(): void {
    this.auth.logout();
  }
}
