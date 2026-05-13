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

  // ─── Mapa de palabras clave → textura Minecraft ───────────────────────────
  private readonly ITEM_ICONS: { keywords: string[]; icon: string }[] = [
    { keywords: ['libro','encantado'],       icon: 'enchanted_book' },
    { keywords: ['libro'],                   icon: 'book' },
    { keywords: ['poción','pocion','velocidad','fuerza','salud','veneno','invisibilidad'], icon: 'potion' },
    { keywords: ['arco encantado'],          icon: 'bow' },
    { keywords: ['arco'],                    icon: 'bow' },
    { keywords: ['ballesta'],                icon: 'crossbow' },
    { keywords: ['tridente'],                icon: 'trident' },
    { keywords: ['espada diamante','espada de diamante'], icon: 'diamond_sword' },
    { keywords: ['espada'],                  icon: 'iron_sword' },
    { keywords: ['pico diamante','pico de diamante'],    icon: 'diamond_pickaxe' },
    { keywords: ['pico'],                    icon: 'iron_pickaxe' },
    { keywords: ['hacha diamante','hacha de diamante'],  icon: 'diamond_axe' },
    { keywords: ['hacha'],                   icon: 'iron_axe' },
    { keywords: ['manzana dorada'],          icon: 'golden_apple' },
    { keywords: ['manzana'],                 icon: 'apple' },
    { keywords: ['pan'],                     icon: 'bread' },
    { keywords: ['carne','bistec','filete'], icon: 'cooked_beef' },
    { keywords: ['tocón','tronco','madera'], icon: 'oak_log' },
    { keywords: ['tabla','plancha'],         icon: 'oak_planks' },
    { keywords: ['lingote','hierro'],        icon: 'iron_ingot' },
    { keywords: ['lingote oro','oro'],       icon: 'gold_ingot' },
    { keywords: ['diamante'],                icon: 'diamond' },
    { keywords: ['esmeralda'],               icon: 'emerald' },
    { keywords: ['flecha'],                  icon: 'arrow' },
    { keywords: ['hueso'],                   icon: 'bone' },
    { keywords: ['pluma'],                   icon: 'feather' },
    { keywords: ['perla','ender'],           icon: 'ender_pearl' },
    { keywords: ['estrella'],                icon: 'nether_star' },
    { keywords: ['tótem','totem'],           icon: 'totem_of_undying' },
    { keywords: ['palo','blaze'],            icon: 'blaze_rod' },
    { keywords: ['caña','pesca'],            icon: 'fishing_rod' },
    { keywords: ['cuero','cuero'],           icon: 'leather' },
    { keywords: ['eslabón','pedernal'],      icon: 'flint_and_steel' },
    { keywords: ['cofre'],                   icon: 'chest' },
    { keywords: ['cubo','balde'],            icon: 'bucket' },
    { keywords: ['mapa'],                    icon: 'map' },
    { keywords: ['brújula'],                 icon: 'compass' },
    { keywords: ['reloj'],                   icon: 'clock' },
  ];

  getItemIcon(nombre: string): string {
    const lower = (nombre || '').toLowerCase();
    for (const entry of this.ITEM_ICONS) {
      if (entry.keywords.some(kw => lower.includes(kw))) {
        return `assets/items/${entry.icon}.png`;
      }
    }
    return 'assets/items/book.png'; // fallback
  }
}
