import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MainLayoutComponent } from './main-layout.component';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { NavbarComponent } from '../navbar/navbar.component';
import { HotbarComponent } from '../hotbar/hotbar.component';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatBadgeModule } from '@angular/material/badge';
import { MatMenuModule } from '@angular/material/menu';
import { MatRippleModule } from '@angular/material/core';
import { MatDividerModule } from '@angular/material/divider';

const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: 'dashboard', loadChildren: () => import('../../pages/dashboard/dashboard.module').then(m => m.DashboardModule) },
      { path: 'players', loadChildren: () => import('../../pages/players/players.module').then(m => m.PlayersModule) },
      { path: 'clans', loadChildren: () => import('../../pages/clans/clans.module').then(m => m.ClansModule) },
      { path: 'economy', loadChildren: () => import('../../pages/economy/economy.module').then(m => m.EconomyModule) },
      { path: 'events', loadChildren: () => import('../../pages/events/events.module').then(m => m.EventsModule) },
      { path: 'graph', loadChildren: () => import('../../pages/graph/graph.module').then(m => m.GraphModule) },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];

@NgModule({
  declarations: [MainLayoutComponent, SidebarComponent, NavbarComponent, HotbarComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FormsModule,
    MatIconModule, MatTooltipModule, MatBadgeModule, MatMenuModule, MatRippleModule, MatDividerModule
  ]
})
export class MainLayoutModule {}
