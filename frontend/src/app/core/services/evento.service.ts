import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Evento } from '../models/evento.model';

@Injectable({ providedIn: 'root' })
export class EventoService {
  private readonly API = 'http://localhost:8080/api/eventos';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Evento[]> { return this.http.get<Evento[]>(this.API); }
  getById(id: number): Observable<Evento> { return this.http.get<Evento>(`${this.API}/${id}`); }
  getActivos(): Observable<Evento[]> { return this.http.get<Evento[]>(`${this.API}/activos`); }
  getByTipo(tipo: string): Observable<Evento[]> { return this.http.get<Evento[]>(`${this.API}/tipo/${tipo}`); }
  getRecientes(limit = 20): Observable<Evento[]> { return this.http.get<Evento[]>(`${this.API}/recientes?limit=${limit}`); }
  getFrecuentes(): Observable<any[]> { return this.http.get<any[]>(`${this.API}/frecuentes`); }
}
