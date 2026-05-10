import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PlayersComponent } from './players.component';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';

const routes: Routes = [{ path: '', component: PlayersComponent }];

@NgModule({
  declarations: [PlayersComponent],
  imports: [
    CommonModule, FormsModule,
    RouterModule.forChild(routes),
    MatIconModule, MatTableModule, MatPaginatorModule,
    MatSortModule, MatTooltipModule, MatButtonModule, MatChipsModule
  ]
})
export class PlayersModule {}
