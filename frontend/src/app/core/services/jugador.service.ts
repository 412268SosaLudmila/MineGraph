import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Jugador } from '../models/jugador.model';
import { GrafoData } from '../models/grafo.model';

@Injectable({ providedIn: 'root' })
export class JugadorService {
  private readonly API = 'http://localhost:8080/api/jugadores';

  constructor(private http: HttpClient) {}

  // ─── GET ─────────────────────────────────────────────────────────────────
  getAll(): Observable<Jugador[]>              { return this.http.get<Jugador[]>(this.API); }
  getById(id: number): Observable<Jugador>     { return this.http.get<Jugador>(`${this.API}/${id}`); }
  getOnline(): Observable<Jugador[]>           { return this.http.get<Jugador[]>(`${this.API}/online`); }
  getTopPvP(limit = 10): Observable<Jugador[]>          { return this.http.get<Jugador[]>(`${this.API}/top/pvp?limit=${limit}`); }
  getTopComerciantes(limit = 10): Observable<Jugador[]> { return this.http.get<Jugador[]>(`${this.API}/top/comerciantes?limit=${limit}`); }
  getMasConectados(limit = 10): Observable<Jugador[]>   { return this.http.get<Jugador[]>(`${this.API}/top/conectados?limit=${limit}`); }
  getMasInfluyentes(limit = 10): Observable<Jugador[]>  { return this.http.get<Jugador[]>(`${this.API}/top/influyentes?limit=${limit}`); }
  getAmigosDeAmigos(nickname: string): Observable<Jugador[]> { return this.http.get<Jugador[]>(`${this.API}/${nickname}/amigos-de-amigos`); }
  search(q: string): Observable<Jugador[]>     { return this.http.get<Jugador[]>(`${this.API}/search?q=${q}`); }
  getGrafo(): Observable<GrafoData>            { return this.http.get<GrafoData>(`${this.API}/grafo`); }

  // ─── CRUD ─────────────────────────────────────────────────────────────────
  create(data: Partial<Jugador>): Observable<Jugador>             { return this.http.post<Jugador>(this.API, data); }
  update(id: number, data: Partial<Jugador>): Observable<Jugador> { return this.http.put<Jugador>(`${this.API}/${id}`, data); }
  delete(id: number): Observable<void>                            { return this.http.delete<void>(`${this.API}/${id}`); }

  // ─── Relaciones ──────────────────────────────────────────────────────────
  addAmigo(id: number, amigoId: number): Observable<any>       { return this.http.post(`${this.API}/${id}/amigos/${amigoId}`, {}); }
  removeAmigo(id: number, amigoId: number): Observable<any>    { return this.http.delete(`${this.API}/${id}/amigos/${amigoId}`); }
  addEnemigo(id: number, enemigoId: number): Observable<any>   { return this.http.post(`${this.API}/${id}/enemigos/${enemigoId}`, {}); }
  removeEnemigo(id: number, enemigoId: number): Observable<any>{ return this.http.delete(`${this.API}/${id}/enemigos/${enemigoId}`); }
  asignarClan(id: number, clanId: number): Observable<any>     { return this.http.post(`${this.API}/${id}/clan/${clanId}`, {}); }
  quitarClan(id: number): Observable<any>                      { return this.http.delete(`${this.API}/${id}/clan`); }
}
