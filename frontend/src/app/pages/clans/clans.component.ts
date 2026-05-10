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

  ngOnInit(): void {
    this.clanService.getAll().subscribe(d => { this.clans = d; this.loading = false; this.buildChart(d); });
    this.clanService.getDominantes(10).subscribe(d => this.dominantes = d);
    this.clanService.getEnGuerra().subscribe(d => this.enGuerra = d);
  }

  private buildChart(data: Clan[]): void {
    const top8 = [...data].sort((a, b) => b.riqueza - a.riqueza).slice(0, 8);
    this.riquezaChartData = {
      labels: top8.map(c => c.tag),
      datasets: [{
        data: top8.map(c => c.riqueza),
        backgroundColor: top8.map(c => c.color + '90'),
        borderColor: top8.map(c => c.color),
        borderWidth: 1,
        borderRadius: 4
      }]
    };
  }

  setTab(tab: string): void { this.activeTab = tab; }

  get displayedClans(): Clan[] {
    if (this.activeTab === 'dominantes') return this.dominantes;
    if (this.activeTab === 'guerra') return this.enGuerra;
    return this.clans;
  }

  selectClan(c: Clan): void {
    this.selectedClan = this.selectedClan?.id === c.id ? null : c;
  }

  getWinRate(c: Clan): number {
    const total = (c.victorias || 0) + (c.derrotas || 0);
    return total > 0 ? Math.round((c.victorias / total) * 100) : 0;
  }

  formatNum(n: number): string {
    if (!n) return '0';
    if (n >= 1000000) return (n / 1000000).toFixed(2) + 'M';
    if (n >= 1000) return (n / 1000).toFixed(1) + 'K';
    return n.toFixed(0);
  }
}
