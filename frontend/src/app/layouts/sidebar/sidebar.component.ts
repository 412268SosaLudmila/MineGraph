import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';

interface NavItem {
  label: string;
  icon: string;
  route: string;
  badge?: string;
  badgeColor?: string;
}

@Component({
  selector: 'mg-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  @Input() collapsed = false;

  navItems: NavItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/dashboard' },
    { label: 'Jugadores', icon: 'people', route: '/players', badge: '100' },
    { label: 'Clanes', icon: 'shield', route: '/clans', badge: '15' },
    { label: 'Economía', icon: 'account_balance', route: '/economy' },
    { label: 'Eventos', icon: 'bolt', route: '/events', badge: 'LIVE', badgeColor: '#ff4757' },
    { label: 'Mapa de Grafos', icon: 'hub', route: '/graph' }
  ];

  constructor(public router: Router) {}

  isActive(route: string): boolean {
    return this.router.url.startsWith(route);
  }
}
