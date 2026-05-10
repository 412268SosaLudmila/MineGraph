import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ClansComponent } from './clans.component';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NgChartsModule } from 'ng2-charts';

const routes: Routes = [{ path: '', component: ClansComponent }];

@NgModule({
  declarations: [ClansComponent],
  imports: [
    CommonModule, RouterModule.forChild(routes),
    MatIconModule, MatTableModule, MatPaginatorModule,
    MatSortModule, MatProgressBarModule, MatTooltipModule, NgChartsModule
  ]
})
export class ClansModule {}
