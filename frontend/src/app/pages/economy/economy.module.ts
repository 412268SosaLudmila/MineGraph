import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { EconomyComponent } from './economy.component';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NgChartsModule } from 'ng2-charts';

const routes: Routes = [{ path: '', component: EconomyComponent }];

@NgModule({
  declarations: [EconomyComponent],
  imports: [
    CommonModule, RouterModule.forChild(routes),
    MatIconModule, MatTableModule, MatPaginatorModule, MatTooltipModule, NgChartsModule
  ]
})
export class EconomyModule {}
