import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../core/services/dashboard.service';

@Component({
  selector: 'mg-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss']
})
export class MainLayoutComponent implements OnInit {
  sidebarCollapsed = false;

  // Bossbar — muestra actividad del servidor como % de salud del jefe
  bossbarPct = 72;
  bossNotches = Array(20);   // 20 muescas = igual al bossbar de Minecraft

  constructor(private dashService: DashboardService) {}

  ngOnInit(): void {
    // Carga los stats del servidor para animar la bossbar
    this.dashService.getStats().subscribe({
      next: (stats: any) => {
        const online  = stats?.jugadoresOnline  ?? 0;
        const total   = stats?.totalJugadores   ?? 1;
        this.bossbarPct = Math.min(100, Math.round((online / Math.max(total, 1)) * 100));
      },
      error: () => { this.bossbarPct = 72; }
    });
  }

  toggleSidebar(): void {
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }
}
