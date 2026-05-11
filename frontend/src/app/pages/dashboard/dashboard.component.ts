import { Component, OnInit } from '@angular/core';
import { DashboardStats } from '../../core/models/dashboard.model';
import { DashboardService } from '../../core/services/dashboard.service';
import { ChartConfiguration, ChartData } from 'chart.js';

@Component({
  selector: 'mg-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats | null = null;
  loading = true;

  // Activity chart
  activityChartData: ChartData<'line'> = { labels: [], datasets: [] };
  activityChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      tooltip: {
        backgroundColor: 'rgba(14,16,28,0.95)',
        borderColor: 'rgba(108,99,255,0.4)',
        borderWidth: 1,
        titleColor: '#e2e8f0',
        bodyColor: '#00d4ff',
        padding: 12,
        displayColors: false
      }
    },
    scales: {
      x: {
        grid: { color: 'rgba(255,255,255,0.04)' },
        ticks: { color: '#718096', font: { size: 11 } },
        border: { color: 'transparent' }
      },
      y: {
        grid: { color: 'rgba(255,255,255,0.04)' },
        ticks: { color: '#718096', font: { size: 11 } },
        border: { color: 'transparent' }
      }
    },
    elements: {
      line: { tension: 0.4, borderWidth: 2.5 },
      point: { radius: 3, hoverRadius: 6, borderWidth: 2, backgroundColor: '#00d4ff' }
    }
  };

  // Economy chart (doughnut)
  econChartData: ChartData<'doughnut'> = { labels: [], datasets: [] };
  econChartOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'right',
        labels: { color: '#718096', font: { size: 11 }, boxWidth: 12, padding: 16 }
      },
      tooltip: {
        backgroundColor: 'rgba(14,16,28,0.95)',
        borderColor: 'rgba(108,99,255,0.4)',
        borderWidth: 1,
        titleColor: '#e2e8f0',
        bodyColor: '#e2e8f0',
        padding: 12
      }
    },
    cutout: '70%'
  };

  // Event distribution (bar)
  eventChartData: ChartData<'bar'> = { labels: [], datasets: [] };
  eventChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      tooltip: {
        backgroundColor: 'rgba(14,16,28,0.95)',
        borderColor: 'rgba(108,99,255,0.4)',
        borderWidth: 1,
        padding: 12
      }
    },
    scales: {
      x: {
        grid: { display: false },
        ticks: { color: '#718096', font: { size: 11 } },
        border: { color: 'transparent' }
      },
      y: {
        grid: { color: 'rgba(255,255,255,0.04)' },
        ticks: { color: '#718096', font: { size: 11 } },
        border: { color: 'transparent' }
      }
    }
  };

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.dashboardService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.buildCharts(data);
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  private buildCharts(data: DashboardStats): void {
    // Activity line chart
    if (data.actividadPorHora?.length) {
      this.activityChartData = {
        labels: data.actividadPorHora.map(h => h['hora']),
        datasets: [{
          data: data.actividadPorHora.map(h => h['jugadores']),
          borderColor: '#00d4ff',
          backgroundColor: 'rgba(0,212,255,0.06)',
          fill: true,
          pointBackgroundColor: '#00d4ff',
          pointBorderColor: '#0a0e1a',
          pointBorderWidth: 2
        }]
      };
    }

    // Economy doughnut
    if (data.topItems?.length) {
      const top5 = data.topItems.slice(0, 5);
      this.econChartData = {
        labels: top5.map(i => i['item']),
        datasets: [{
          data: top5.map(i => i['volumen']),
          backgroundColor: ['#6c63ff','#00d4ff','#ffd700','#00ff88','#ff6b6b'],
          borderColor: '#0a0e1a',
          borderWidth: 3,
          hoverBorderColor: 'transparent'
        }]
      };
    }

    // Events bar chart
    if (data.eventosFrecuentes?.length) {
      this.eventChartData = {
        labels: data.eventosFrecuentes.map(e => e['tipo']),
        datasets: [{
          data: data.eventosFrecuentes.map(e => e['cantidad']),
          backgroundColor: ['rgba(255,71,87,0.7)','rgba(168,85,247,0.7)',
                             'rgba(255,159,67,0.7)','rgba(255,215,0,0.7)','rgba(0,212,255,0.7)'],
          borderColor: ['#ff4757','#a855f7','#ff9f43','#ffd700','#00d4ff'],
          borderWidth: 1,
          borderRadius: 6
        }]
      };
    }
  }

  formatNumber(n: number): string {
    if (!n) return '0';
    if (n >= 1000000) return (n / 1000000).toFixed(1) + 'M';
    if (n >= 1000) return (n / 1000).toFixed(1) + 'K';
    return n.toFixed(0);
  }

  formatCurrency(n: number): string {
    if (!n) return '0';
    if (n >= 1000000) return (n / 1000000).toFixed(2) + 'M ⬡';
    if (n >= 1000) return (n / 1000).toFixed(1) + 'K ⬡';
    return n.toFixed(0) + ' ⬡';
  }

  getKDClass(kd: number): string {
    if (kd >= 3) return 'kd-elite';
    if (kd >= 2) return 'kd-high';
    if (kd >= 1) return 'kd-mid';
    return 'kd-low';
  }

  getEventBadgeClass(tipo: string): string {
    const map: Record<string, string> = {
      'PVP': 'badge-pvp', 'BOSS': 'badge-boss',
      'RAID': 'badge-raid', 'TORNEO': 'badge-torneo', 'EXPLORACIÓN': 'badge-exp'
    };
    return map[tipo] || 'badge-default';
  }

  getAvatarColor(nickname: string): string {
    const colors = ['#6c63ff','#00d4ff','#ff6b6b','#ffd700','#00ff88','#a855f7','#ff9f43'];
    let hash = 0;
    for (const c of nickname) hash = c.charCodeAt(0) + ((hash << 5) - hash);
    return colors[Math.abs(hash) % colors.length];
  }

  getInitials(nickname: string): string {
    return nickname.substring(0, 2).toUpperCase();
  }

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

  getMcAvatar(nickname: string): string {
    let hash = 0;
    for (const c of nickname) hash = c.charCodeAt(0) + ((hash << 5) - hash);
    return this.MC_AVATARS[Math.abs(hash) % this.MC_AVATARS.length];
  }
}
