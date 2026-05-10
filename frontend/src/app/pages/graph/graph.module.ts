import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { GraphComponent } from './graph.component';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSelectModule } from '@angular/material/select';

const routes: Routes = [{ path: '', component: GraphComponent }];

@NgModule({
  declarations: [GraphComponent],
  imports: [
    CommonModule, FormsModule,
    RouterModule.forChild(routes),
    MatIconModule, MatTooltipModule, MatSlideToggleModule, MatSelectModule
  ]
})
export class GraphModule {}
