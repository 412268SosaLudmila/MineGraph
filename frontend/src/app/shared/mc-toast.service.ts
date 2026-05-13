import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface McToastData {
  title: string;
  desc:  string;
  icon:  string;
  color: string;
}

@Injectable({ providedIn: 'root' })
export class McToastService {
  private _toast$ = new Subject<McToastData>();
  toast$ = this._toast$.asObservable();

  achievement(title: string, desc: string): void {
    this._toast$.next({ title, desc, icon: '🏆', color: '#ffd700' });
  }

  success(title: string, desc: string): void {
    this._toast$.next({ title, desc, icon: '⚔️', color: '#00ff88' });
  }

  info(title: string, desc: string): void {
    this._toast$.next({ title, desc, icon: '📦', color: '#00d4ff' });
  }

  error(title: string, desc: string): void {
    this._toast$.next({ title, desc, icon: '💀', color: '#ff4757' });
  }
}
