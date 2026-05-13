import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type CursorPetMode = 'off' | 'wolf' | 'sword';

@Injectable({ providedIn: 'root' })
export class CursorPetService {
  private readonly KEY = 'mg_cursor_pet';

  private _mode = new BehaviorSubject<CursorPetMode>(
    (localStorage.getItem(this.KEY) as CursorPetMode) || 'wolf'
  );

  mode$ = this._mode.asObservable();
  get mode(): CursorPetMode { return this._mode.value; }

  set(mode: CursorPetMode): void {
    localStorage.setItem(this.KEY, mode);
    this._mode.next(mode);
  }
}
