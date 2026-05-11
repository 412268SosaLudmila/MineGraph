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

  // Modal
  showModal = false;
  editMode = false;
  saving = false;
  selectedEvento: Evento | null = null;
  form: Partial<Evento> = {};

  constructor(private eventoService: EventoService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.eventoService.getAll().subscribe({ next: (data) => { this.eventos = data; this.loading = false; }, error: () => { this.loading = false; } });
    this.eventoService.getActivos().subscribe(d => this.activos = d);
  }

  get filtered(): Evento[] {
    if (this.activeFilter === 'all') return this.eventos;
    return this.eventos.filter(e => e.tipo === this.activeFilter);
  }

  // ─── CRUD ────────────────────────────────────────────────────────────────

  openCreate(): void {
    this.editMode = false;
    this.selectedEvento = null;
    this.form = { tipo: 'PVP', duracionMinutos: 60, participantes: 0, recompensa: 500, region: 'Spawn', activo: true };
    this.showModal = true;
  }

  openEdit(e: Evento, event: Event): void {
    event.stopPropagation();
    this.editMode = true;
    this.selectedEvento = e;
    this.form = { nombre: e.nombre, tipo: e.tipo, descripcion: e.descripcion,
                  duracionMinutos: e.duracionMinutos, participantes: e.participantes,
                  recompensa: e.recompensa, region: e.region, ganador: e.ganador, activo: e.activo };
    this.showModal = true;
  }

  saveEvento(): void {
    if (!this.form.nombre?.trim()) return;
    this.saving = true;
    const obs = this.editMode && this.selectedEvento
      ? this.eventoService.update(this.selectedEvento.id!, this.form)
      : this.eventoService.create(this.form);
    obs.subscribe({ next: () => { this.showModal = false; this.saving = false; this.load(); }, error: () => { this.saving = false; } });
  }

  deleteEvento(e: Evento, event: Event): void {
    event.stopPropagation();
    if (!confirm(`¿Eliminar el evento "${e.nombre}"?`)) return;
    this.eventoService.delete(e.id!).subscribe(() => this.load());
  }

  // ─── Helpers ─────────────────────────────────────────────────────────────

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
