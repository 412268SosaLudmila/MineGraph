import { Component } from '@angular/core';
import { Router } from '@angular/router';

interface HotbarSlot {
  route: string;
  label: string;
  icon:  string;   // emoji bloque Minecraft
  key:   number;
}

@Component({
  selector: 'mg-hotbar',
  templateUrl: './hotbar.component.html',
  styleUrls: ['./hotbar.component.scss']
})
export class HotbarComponent {
  slots: HotbarSlot[] = [
    { route: '/dashboard',  label: 'Dashboard',  icon: '🌿', key: 1 },
    { route: '/players',    label: 'Jugadores',  icon: '🧍', key: 2 },
    { route: '/clans',      label: 'Clanes',     icon: '🏰', key: 3 },
    { route: '/economy',    label: 'Economía',   icon: '💰', key: 4 },
    { route: '/events',     label: 'Eventos',    icon: '⚡', key: 5 },
    { route: '/graph',      label: 'Grafo',      icon: '🕸️', key: 6 },
  ];

  constructor(private router: Router) {}

  isActive(route: string): boolean {
    return this.router.url.startsWith(route);
  }

  navigate(route: string): void {
    this.router.navigate([route]);
  }
}
