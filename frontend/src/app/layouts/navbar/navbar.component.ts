import { Component, EventEmitter, Output, ViewChild, ElementRef, HostListener } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';
import { CursorPetService, CursorPetMode } from '../../shared/cursor-pet.service';

// Mapa de avatares por username — agregar entradas para más usuarios si se quiere
const USER_AVATARS: Record<string, string> = {
  admin:    'assets/avatars/admin.jpg',
};

@Component({
  selector: 'mg-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
  @Output() toggleSidebar = new EventEmitter<void>();

  currentTime = new Date();
  searchQuery = '';
  avatarError = false;

  @ViewChild('petSelector') petSelector!: ElementRef;
  showPetMenu = false;

  @HostListener('document:click', ['$event'])
  onDocClick(e: MouseEvent): void {
    if (this.petSelector && !this.petSelector.nativeElement.contains(e.target)) {
      this.showPetMenu = false;
    }
  }

  readonly petOptions: { mode: CursorPetMode; label: string; icon: string }[] = [
    { mode: 'wolf',  label: 'Perrito',       icon: '🐺' },
    { mode: 'sword', label: 'Espada',         icon: '⚔️' },
    { mode: 'off',   label: 'Desactivado',    icon: '🚫' },
  ];

  constructor(public auth: AuthService, private router: Router, public petSvc: CursorPetService) {
    setInterval(() => this.currentTime = new Date(), 1000);
  }

  setPetMode(mode: CursorPetMode): void {
    this.petSvc.set(mode);
    this.showPetMenu = false;
  }

  get currentPetIcon(): string {
    return this.petOptions.find(o => o.mode === this.petSvc.mode)?.icon ?? '🐺';
  }

  /** Devuelve la ruta del avatar personalizado si existe, null si no */
  getUserAvatarUrl(): string | null {
    const username = this.auth.getCurrentUser()?.username?.toLowerCase();
    return username && USER_AVATARS[username] ? USER_AVATARS[username] : null;
  }

  onAvatarError(): void { this.avatarError = true; }

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
