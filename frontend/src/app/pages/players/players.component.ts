import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Jugador } from '../../core/models/jugador.model';
import { JugadorService } from '../../core/services/jugador.service';
import { ClanService } from '../../core/services/clan.service';
import { Clan } from '../../core/models/clan.model';
import { McToastService } from '../../shared/mc-toast.service';
import { McXpService } from '../../shared/mc-xp.service';

@Component({
  selector: 'mg-players',
  templateUrl: './players.component.html',
  styleUrls: ['./players.component.scss']
})
export class PlayersComponent implements OnInit {
  displayedColumns = ['rank','nickname','nivel','clan','kd','monedas','reputacion','estado','acciones'];
  dataSource = new MatTableDataSource<Jugador>([]);
  loading = true;
  searchQuery = '';
  activeTab = 'all';

  topPvP: Jugador[] = [];
  masConectados: Jugador[] = [];
  masInfluyentes: Jugador[] = [];
  clanes: Clan[] = [];

  // Modal
  showModal = false;
  showRelModal = false;
  editMode = false;
  saving = false;
  selectedJugador: Jugador | null = null;

  form: Partial<Jugador> = {};
  relForm = { targetId: null as number | null, tipo: 'amigo' as 'amigo' | 'enemigo' | 'clan' };

  // Avatar cache
  avatarErrors = new Set<string>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private jugadorService: JugadorService,
    private clanService: ClanService,
    private toast: McToastService,
    private xp: McXpService
  ) {}

  ngOnInit(): void {
    this.loadAll();
    this.jugadorService.getTopPvP(5).subscribe(d => this.topPvP = d);
    this.jugadorService.getMasConectados(5).subscribe(d => this.masConectados = d);
    this.jugadorService.getMasInfluyentes(5).subscribe(d => this.masInfluyentes = d);
    this.clanService.getAll().subscribe(d => this.clanes = d);
  }

  loadAll(): void {
    this.loading = true;
    this.jugadorService.getAll().subscribe({
      next: (data) => {
        this.dataSource.data = data;
        setTimeout(() => {
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
        });
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  applyFilter(query: string): void {
    this.dataSource.filter = query.trim().toLowerCase();
  }

  setTab(tab: string): void {
    this.activeTab = tab;
    this.loading = true;
    if (tab === 'all') this.loadAll();
    else if (tab === 'online') {
      this.jugadorService.getOnline().subscribe(d => { this.dataSource.data = d; this.loading = false; });
    } else if (tab === 'pvp') {
      this.jugadorService.getTopPvP(100).subscribe(d => { this.dataSource.data = d; this.loading = false; });
    }
  }

  // ─── Avatar Minecraft ────────────────────────────────────────────────────

  private readonly MC_AVATARS = [
    'assets/avatars/steve.png',
    'assets/avatars/alex.png',
    'assets/avatars/villager.png',
    'assets/avatars/enderman.png',
    'assets/avatars/creeper.png',
    'assets/avatars/skeleton.png',
    'assets/avatars/zombie.png',
    'assets/avatars/blaze.png',
  ];

  getAvatarUrl(nick: string): string {
    let hash = 0;
    for (const c of nick) hash = c.charCodeAt(0) + ((hash << 5) - hash);
    return this.MC_AVATARS[Math.abs(hash) % this.MC_AVATARS.length];
  }

  onAvatarError(nick: string): void { this.avatarErrors.add(nick); }
  hasAvatarError(nick: string): boolean { return this.avatarErrors.has(nick); }

  getAvatarColor(nick: string): string {
    const colors = ['#6c63ff','#00d4ff','#ff6b6b','#ffd700','#00ff88','#a855f7','#ff9f43'];
    let h = 0;
    for (const c of nick) h = c.charCodeAt(0) + ((h << 5) - h);
    return colors[Math.abs(h) % colors.length];
  }

  getInitials(nick: string): string { return nick.substring(0, 2).toUpperCase(); }

  // ─── Modal Crear/Editar ──────────────────────────────────────────────────

  openCreate(): void {
    this.editMode = false;
    this.selectedJugador = null;
    this.form = { nivel: 1, estadoOnline: false, kills: 0, muertes: 0, monedas: 0, reputacion: 0, horasJugadas: 0, titulo: 'Novato' };
    this.showModal = true;
  }

  openEdit(j: Jugador): void {
    this.editMode = true;
    this.selectedJugador = j;
    this.form = { nickname: j.nickname, nivel: j.nivel, estadoOnline: j.estadoOnline,
                  kills: j.kills, muertes: j.muertes, monedas: j.monedas,
                  reputacion: j.reputacion, horasJugadas: j.horasJugadas, titulo: j.titulo };
    this.showModal = true;
  }

  saveJugador(event?: MouseEvent): void {
    if (!this.form.nickname?.trim()) return;
    this.saving = true;
    const isNew = !(this.editMode && this.selectedJugador);
    const obs = !isNew
      ? this.jugadorService.update(this.selectedJugador!.id!, this.form)
      : this.jugadorService.create(this.form);

    obs.subscribe({
      next: () => {
        this.showModal = false;
        this.saving = false;
        this.loadAll();
        if (event) this.xp.burst(event);
        if (isNew) {
          this.toast.achievement('¡Nuevo Jugador!', `${this.form.nickname} se unió al servidor`);
        } else {
          this.toast.success('Jugador Actualizado', `${this.form.nickname} fue modificado`);
        }
      },
      error: () => {
        this.saving = false;
        this.toast.error('Error', 'No se pudo guardar el jugador');
      }
    });
  }

  deleteJugador(j: Jugador, event: Event): void {
    event.stopPropagation();
    if (!confirm(`¿Eliminar a ${j.nickname}? Esta acción no se puede deshacer.`)) return;
    this.jugadorService.delete(j.id!).subscribe(() => {
      this.loadAll();
      this.toast.info('Jugador Eliminado', `${j.nickname} abandonó el servidor`);
    });
  }

  // ─── Modal Relaciones ────────────────────────────────────────────────────

  openRelModal(j: Jugador, event: Event): void {
    event.stopPropagation();
    this.selectedJugador = j;
    this.relForm = { targetId: null, tipo: 'amigo' };
    this.showRelModal = true;
  }

  saveRelacion(): void {
    if (!this.selectedJugador || !this.relForm.targetId) return;
    const id = this.selectedJugador.id!;
    const tid = this.relForm.targetId;
    let obs;
    if (this.relForm.tipo === 'amigo')   obs = this.jugadorService.addAmigo(id, tid);
    else if (this.relForm.tipo === 'enemigo') obs = this.jugadorService.addEnemigo(id, tid);
    else obs = this.jugadorService.asignarClan(id, tid);

    obs.subscribe({
      next: () => { this.showRelModal = false; this.loadAll(); },
      error: () => {}
    });
  }

  quitarClan(j: Jugador, event: Event): void {
    event.stopPropagation();
    if (!j.clanNombre) return;
    this.jugadorService.quitarClan(j.id!).subscribe(() => this.loadAll());
  }

  // ─── Helpers ─────────────────────────────────────────────────────────────

  getKDClass(kd: number): string {
    if (kd >= 3) return 'elite'; if (kd >= 2) return 'high';
    if (kd >= 1) return 'mid'; return 'low';
  }

  formatNum(n: number): string {
    if (!n) return '0';
    if (n >= 1000) return (n / 1000).toFixed(1) + 'K';
    return n.toFixed(0);
  }

  getReputacionClass(r: number): string {
    if (r > 500) return 'rep-legendary'; if (r > 200) return 'rep-high';
    if (r > 0) return 'rep-mid'; if (r < -100) return 'rep-negative';
    return 'rep-neutral';
  }

  // ─── Barras de vida estilo Minecraft ──────────────────────────────────────
  // Devuelve array de 5 estados: 'full' | 'half' | 'empty'
  getHearts(rep: number): ('full' | 'half' | 'empty')[] {
    const MAX = 5;
    // Mapeo: rep -200..1000 → 0..10 medios corazones
    const halves = Math.max(0, Math.min(MAX * 2, Math.round(((rep + 200) / 1200) * MAX * 2)));
    return Array.from({ length: MAX }, (_, i) => {
      const pos = i * 2;
      if (halves >= pos + 2) return 'full';
      if (halves >= pos + 1) return 'half';
      return 'empty';
    });
  }
}
