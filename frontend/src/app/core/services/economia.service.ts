import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EconomiaStats, Transaccion } from '../models/economia.model';

@Injectable({ providedIn: 'root' })
export class EconomiaService {
  private readonly API = 'http://localhost:8080/api/economia';

  constructor(private http: HttpClient) {}

  getStats(): Observable<EconomiaStats> { return this.http.get<EconomiaStats>(`${this.API}/stats`); }
  getTransacciones(limit = 50): Observable<Transaccion[]> {
    return this.http.get<Transaccion[]>(`${this.API}/transacciones?limit=${limit}`);
  }
}
