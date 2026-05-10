import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { EventsComponent } from './events.component';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NgChartsModule } from 'ng2-charts';

const routes: Routes = [{ path: '', component: EventsComponent }];

@NgModule({
  declarations: [EventsComponent],
  imports: [CommonModule, RouterModule.forChild(routes), MatIconModule, MatChipsModule, MatTooltipModule, NgChartsModule]
})
export class EventsModule {}
