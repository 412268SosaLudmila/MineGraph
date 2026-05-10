import { Component, OnInit } from '@angular/core';
import { Evento } from '../../core/models/evento.model';
import { EventoService } from '../../core/services/evento.service';

@Component({
  selector: 'mg-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.scss']
})
export class EventsComponent implements OnInit {
  eventos: Evento[] = [];
  activos: Evento[] = [];
  loading = true;
  activeFilter = 'all';
  tipos = ['all', 'PVP', 'BOSS', 'RAID', 'TORNEO', 'EXPLORACIÓN'];

  constructor(private eventoService: EventoService) {}

  ngOnInit(): void {
    this.eventoService.getAll().subscribe({
      next: (data) => { this.eventos = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
    this.eventoService.getActivos().subscribe(d => this.activos = d);
  }

  get filtered(): Evento[] {
    if (this.activeFilter === 'all') return this.eventos;
    return this.eventos.filter(e => e.tipo === this.activeFilter);
  }

  getBadgeClass(tipo: string): string {
    const m: Record<string, string> = { 'PVP': 'badge-pvp', 'BOSS': 'badge-boss', 'RAID': 'badge-raid', 'TORNEO': 'badge-torneo', 'EXPLORACIÓN': 'badge-exp' };
    return m[tipo] || 'badge-default';
  }

  getTipoIcon(tipo: string): string {
    const m: Record<string, string> = { 'PVP': 'gps_fixed', 'BOSS': 'pest_control', 'RAID': 'castle', 'TORNEO': 'emoji_events', 'EXPLORACIÓN': 'explore' };
    return m[tipo] || 'bolt';
  }

  getTipoColor(tipo: string): string {
    const m: Record<string, string> = { 'PVP': '#ff4757', 'BOSS': '#a855f7', 'RAID': '#ff9f43', 'TORNEO': '#ffd700', 'EXPLORACIÓN': '#00d4ff' };
    return m[tipo] || '#718096';
  }

  formatReward(n: number): string {
    if (!n) return '0 ⬡';
    if (n >= 1000) return (n / 1000).toFixed(1) + 'K ⬡';
    return n.toFixed(0) + ' ⬡';
  }

  formatDate(d: string): string {
    return new Date(d).toLocaleDateString('es', { day: '2-digit', month: 'short', year: 'numeric' });
  }
}
