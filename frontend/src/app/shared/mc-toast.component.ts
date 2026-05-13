import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { Subscription } from 'rxjs';
import { McToastService, McToastData } from './mc-toast.service';

interface ActiveToast extends McToastData { id: number; visible: boolean; }

@Component({
  selector: 'mg-mc-toast',
  template: `
    <div class="mc-toast-container">
      <div *ngFor="let t of toasts" class="mc-toast" [class.visible]="t.visible"
           [style.--tc]="t.color">
        <div class="mc-toast-glow" [style.background]="t.color + '30'"></div>
        <div class="mc-toast-icon">{{ t.icon }}</div>
        <div class="mc-toast-body">
          <div class="mc-toast-label">✦ LOGRO DESBLOQUEADO ✦</div>
          <div class="mc-toast-title">{{ t.title }}</div>
          <div class="mc-toast-desc">{{ t.desc }}</div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./mc-toast.component.scss']
})
export class McToastComponent implements OnInit, OnDestroy {
  toasts: ActiveToast[] = [];
  private counter = 0;
  private sub!: Subscription;

  constructor(private svc: McToastService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.sub = this.svc.toast$.subscribe(t => {
      const id = this.counter++;
      const toast: ActiveToast = { ...t, id, visible: false };
      this.toasts.push(toast);
      this.cdr.detectChanges();

      setTimeout(() => { toast.visible = true; this.cdr.detectChanges(); }, 30);
      setTimeout(() => { toast.visible = false; this.cdr.detectChanges(); }, 3600);
      setTimeout(() => {
        this.toasts = this.toasts.filter(x => x.id !== id);
        this.cdr.detectChanges();
      }, 4100);
    });
  }

  ngOnDestroy(): void { this.sub?.unsubscribe(); }
}
