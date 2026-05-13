import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { McToastComponent } from './mc-toast.component';

@NgModule({
  declarations: [McToastComponent],
  imports: [CommonModule],
  exports: [McToastComponent]
})
export class SharedModule {}
