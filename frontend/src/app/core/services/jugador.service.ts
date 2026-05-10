import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Jugador } from '../models/jugador.model';
import { GrafoData } from '../models/grafo.model';

@Injectable({ providedIn: 'root' })
export class JugadorService {
  private readonly API = 'http://localhost:8080/api/jugadores';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Jugador[]> { return this.http.get<Jugador[]>(this.API); }
  getById(id: number): Observable<Jugador> { return this.http.get<Jugador>(`${this.API}/${id}`); }
  getOnline(): Observable<Jugador[]> { return this.http.get<Jugador[]>(`${this.API}/online`); }
  getTopPvP(limit = 10): Observable<Jugador[]> { return this.http.get<Jugador[]>(`${this.API}/top/pvp?limit=${limit}`); }
  getTopComerciantes(limit = 10): Observable<Jugador[]> { return this.http.get<Jugador[]>(`${this.API}/top/comerciantes?limit=${limit}`); }
  getMasConectados(limit = 10): Observable<Jugador[]> { return this.http.get<Jugador[]>(`${this.API}/top/conectados?limit=${limit}`); }
  getMasInfluyentes(limit = 10): Observable<Jugador[]> { return this.http.get<Jugador[]>(`${this.API}/top/influyentes?limit=${limit}`); }
  getAmigosDeAmigos(nickname: string): Observable<Jugador[]> { return this.http.get<Jugador[]>(`${this.API}/${nickname}/amigos-de-amigos`); }
  search(q: string): Observable<Jugador[]> { return this.http.get<Jugador[]>(`${this.API}/search?q=${q}`); }
  getGrafo(): Observable<GrafoData> { return this.http.get<GrafoData>(`${this.API}/grafo`); }
}
