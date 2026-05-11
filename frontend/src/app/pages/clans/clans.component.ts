import { Component, OnInit } from '@angular/core';
import { Clan } from '../../core/models/clan.model';
import { ClanService } from '../../core/services/clan.service';
import { ChartConfiguration, ChartData } from 'chart.js';

@Component({
  selector: 'mg-clans',
  templateUrl: './clans.component.html',
  styleUrls: ['./clans.component.scss']
})
export class ClansComponent implements OnInit {
  clans: Clan[] = [];
  dominantes: Clan[] = [];
  enGuerra: Clan[] = [];
  loading = true;
  activeTab = 'all';
  selectedClan: Clan | null = null;

  // Modal
  showModal = false;
  showRelModal = false;
  editMode = false;
  saving = false;
  form: Partial<Clan> = {};
  relForm = { targetId: null as number | null, tipo: 'alianza' as 'alianza' | 'guerra' };

  COLORS = ['#6c63ff','#10b981','#f59e0b','#ef4444','#3b82f6','#a855f7','#ec4899','#14b8a6'];

  riquezaChartData: ChartData<'bar'> = { labels: [], datasets: [] };
  riquezaOptions: ChartConfiguration['options'] = {
    indexAxis: 'y',
    responsive: true, maintainAspectRatio: false,
    plugins: { legend: { display: false }, tooltip: { backgroundColor: 'rgba(14,16,28,0.95)', borderColor: 'rgba(108,99,255,0.4)', borderWidth: 1, padding: 10 } },
    scales: {
      x: { grid: { color: 'rgba(255,255,255,0.04)' }, ticks: { color: '#718096', font: { size: 10 } }, border: { color: 'transparent' } },
      y: { grid: { display: false }, ticks: { color: '#e2e8f0', font: { size: 11 } }, border: { color: 'transparent' } }
    }
  };

  constructor(private clanService: ClanService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.clanService.getAll().subscribe(d => { this.clans = d; this.loading = false; this.buildChart(d); });
    this.clanService.getDominantes(10).subscribe(d => this.dominantes = d);
    this.clanService.getEnGuerra().subscribe(d => this.enGuerra = d);
  }

  private buildChart(data: Clan[]): void {
    const top8 = [...data].sort((a, b) => b.riqueza - a.riqueza).slice(0, 8);
    this.riquezaChartData = {
      labels: top8.map(c => c.tag),
      datasets: [{ data: top8.map(c => c.riqueza), backgroundColor: top8.map(c => c.color + '90'), borderColor: top8.map(c => c.color), borderWidth: 1, borderRadius: 4 }]
    };
  }

  setTab(tab: string): void { this.activeTab = tab; }
  get displayedClans(): Clan[] {
    if (this.activeTab === 'dominantes') return this.dominantes;
    if (this.activeTab === 'guerra') return this.enGuerra;
    return this.clans;
  }
  selectClan(c: Clan): void { this.selectedClan = this.selectedClan?.id === c.id ? null : c; }
  getWinRate(c: Clan): number { const t = (c.victorias||0)+(c.derrotas||0); return t>0?Math.round((c.victorias/t)*100):0; }
  formatNum(n: number): string { if(!n)return'0'; if(n>=1000000)return(n/1000000).toFixed(2)+'M'; if(n>=1000)return(n/1000).toFixed(1)+'K'; return n.toFixed(0); }

  // ─── CRUD ────────────────────────────────────────────────────────────────

  openCreate(): void {
    this.editMode = false;
    this.form = { nivel: 1, victorias: 0, derrotas: 0, territoriosControlados: 0, riqueza: 0, cantidadMiembros: 0, color: '#6c63ff' };
    this.showModal = true;
  }

  openEdit(c: Clan, event: Event): void {
    event.stopPropagation();
    this.editMode = true;
    this.form = { nombre: c.nombre, tag: c.tag, descripcion: c.descripcion, nivel: c.nivel,
                  riqueza: c.riqueza, cantidadMiembros: c.cantidadMiembros, victorias: c.victorias,
                  derrotas: c.derrotas, territoriosControlados: c.territoriosControlados, color: c.color };
    this.selectedClan = c;
    this.showModal = true;
  }

  saveClan(): void {
    if (!this.form.nombre?.trim()) return;
    this.saving = true;
    const obs = this.editMode && this.selectedClan
      ? this.clanService.update(this.selectedClan.id!, this.form)
      : this.clanService.create(this.form);
    obs.subscribe({ next: () => { this.showModal = false; this.saving = false; this.load(); }, error: () => { this.saving = false; } });
  }

  deleteClan(c: Clan, event: Event): void {
    event.stopPropagation();
    if (!confirm(`¿Eliminar el clan ${c.nombre}? Se quitará a todos sus miembros.`)) return;
    this.clanService.delete(c.id!).subscribe(() => this.load());
  }

  openRelModal(c: Clan, event: Event): void {
    event.stopPropagation();
    this.selectedClan = c;
    this.relForm = { targetId: null, tipo: 'alianza' };
    this.showRelModal = true;
  }

  saveRelClan(): void {
    if (!this.selectedClan || !this.relForm.targetId) return;
    const obs = this.relForm.tipo === 'alianza'
      ? this.clanService.declararAlianza(this.selectedClan.id!, this.relForm.targetId)
      : this.clanService.declararGuerra(this.selectedClan.id!, this.relForm.targetId);
    obs.subscribe({ next: () => { this.showRelModal = false; this.load(); }, error: () => {} });
  }
}
