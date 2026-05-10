import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Jugador } from '../../core/models/jugador.model';
import { JugadorService } from '../../core/services/jugador.service';

@Component({
  selector: 'mg-players',
  templateUrl: './players.component.html',
  styleUrls: ['./players.component.scss']
})
export class PlayersComponent implements OnInit {
  displayedColumns = ['rank','nickname','nivel','clan','kd','monedas','reputacion','estado'];
  dataSource = new MatTableDataSource<Jugador>([]);
  loading = true;
  searchQuery = '';
  filterOnline = false;
  activeTab = 'all';

  topPvP: Jugador[] = [];
  masConectados: Jugador[] = [];
  masInfluyentes: Jugador[] = [];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private jugadorService: JugadorService) {}

  ngOnInit(): void {
    this.loadAll();
    this.jugadorService.getTopPvP(5).subscribe(d => this.topPvP = d);
    this.jugadorService.getMasConectados(5).subscribe(d => this.masConectados = d);
    this.jugadorService.getMasInfluyentes(5).subscribe(d => this.masInfluyentes = d);
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
      this.jugadorService.getOnline().subscribe(d => {
        this.dataSource.data = d;
        this.loading = false;
      });
    } else if (tab === 'pvp') {
      this.jugadorService.getTopPvP(100).subscribe(d => {
        this.dataSource.data = d;
        this.loading = false;
      });
    }
  }

  getAvatarColor(nick: string): string {
    const colors = ['#6c63ff','#00d4ff','#ff6b6b','#ffd700','#00ff88','#a855f7','#ff9f43'];
    let h = 0;
    for (const c of nick) h = c.charCodeAt(0) + ((h << 5) - h);
    return colors[Math.abs(h) % colors.length];
  }

  getInitials(nick: string): string { return nick.substring(0, 2).toUpperCase(); }

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
}
