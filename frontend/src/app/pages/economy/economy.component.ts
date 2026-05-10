import { Component, OnInit } from '@angular/core';
import { EconomiaStats } from '../../core/models/economia.model';
import { EconomiaService } from '../../core/services/economia.service';
import { ChartConfiguration, ChartData } from 'chart.js';

@Component({
  selector: 'mg-economy',
  templateUrl: './economy.component.html',
  styleUrls: ['./economy.component.scss']
})
export class EconomyComponent implements OnInit {
  stats: EconomiaStats | null = null;
  loading = true;

  itemsChartData: ChartData<'bar'> = { labels: [], datasets: [] };
  mercadoChartData: ChartData<'doughnut'> = { labels: [], datasets: [] };
  tipoChartData: ChartData<'polarArea'> = { labels: [], datasets: [] };

  barOptions: ChartConfiguration['options'] = {
    responsive: true, maintainAspectRatio: false,
    plugins: { legend: { display: false }, tooltip: { backgroundColor: 'rgba(14,16,28,0.95)', borderColor: 'rgba(255,215,0,0.4)', borderWidth: 1, padding: 12 } },
    scales: {
      x: { grid: { display: false }, ticks: { color: '#718096', font: { size: 11 } }, border: { color: 'transparent' } },
      y: { grid: { color: 'rgba(255,255,255,0.04)' }, ticks: { color: '#718096', font: { size: 11 } }, border: { color: 'transparent' } }
    }
  };

  doughnutOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true, maintainAspectRatio: false,
    cutout: '65%',
    plugins: { legend: { position: 'bottom', labels: { color: '#718096', font: { size: 11 }, boxWidth: 12, padding: 12 } }, tooltip: { backgroundColor: 'rgba(14,16,28,0.95)', borderColor: 'rgba(255,215,0,0.4)', borderWidth: 1, padding: 12 } }
  };

  polarOptions: ChartConfiguration['options'] = {
    responsive: true, maintainAspectRatio: false,
    plugins: { legend: { position: 'right', labels: { color: '#718096', font: { size: 11 }, boxWidth: 12 } }, tooltip: { backgroundColor: 'rgba(14,16,28,0.95)', padding: 12 } },
    scales: { r: { grid: { color: 'rgba(255,255,255,0.06)' }, ticks: { display: false }, pointLabels: { display: false } } }
  };

  constructor(private economiaService: EconomiaService) {}

  ngOnInit(): void {
    this.economiaService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.buildCharts(data);
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  private buildCharts(data: EconomiaStats): void {
    if (data.topItems?.length) {
      const top8 = data.topItems.slice(0, 8);
      this.itemsChartData = {
        labels: top8.map(i => i['item']),
        datasets: [{
          data: top8.map(i => i['volumen']),
          backgroundColor: 'rgba(255,215,0,0.2)',
          borderColor: '#ffd700',
          borderWidth: 1,
          borderRadius: 6,
          hoverBackgroundColor: 'rgba(255,215,0,0.4)'
        }]
      };
    }
    if (data.volumenPorMercado?.length) {
      const colors = ['#6c63ff','#00d4ff','#ffd700','#00ff88','#ff6b6b','#a855f7'];
      this.mercadoChartData = {
        labels: data.volumenPorMercado.map(m => m['mercado']),
        datasets: [{
          data: data.volumenPorMercado.map(m => m['volumen']),
          backgroundColor: colors.map(c => c + '88'),
          borderColor: colors,
          borderWidth: 2,
          hoverBorderColor: 'transparent'
        }]
      };
    }
    if (data.actividadPorTipo?.length) {
      const colors2 = ['#6c63ff','#00d4ff','#ffd700','#00ff88'];
      this.tipoChartData = {
        labels: data.actividadPorTipo.map(t => t['tipo']),
        datasets: [{
          data: data.actividadPorTipo.map(t => t['cantidad']),
          backgroundColor: colors2.map(c => c + '66'),
          borderColor: colors2,
          borderWidth: 1
        }]
      };
    }
  }

  formatNum(n: number): string {
    if (!n) return '0';
    if (n >= 1000000) return (n / 1000000).toFixed(2) + 'M';
    if (n >= 1000) return (n / 1000).toFixed(1) + 'K';
    return n.toFixed(0);
  }

  formatCurrency(n: number): string { return this.formatNum(n) + ' ⬡'; }

  getTipoClass(tipo: string): string {
    const m: Record<string, string> = { 'VENTA': 'tipo-venta', 'COMPRA': 'tipo-compra', 'SUBASTA': 'tipo-subasta', 'INTERCAMBIO': 'tipo-intercambio' };
    return m[tipo] || '';
  }
}
