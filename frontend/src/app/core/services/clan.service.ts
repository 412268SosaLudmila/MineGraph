import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Clan } from '../models/clan.model';

@Injectable({ providedIn: 'root' })
export class ClanService {
  private readonly API = 'http://localhost:8080/api/clanes';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Clan[]> { return this.http.get<Clan[]>(this.API); }
  getById(id: number): Observable<Clan> { return this.http.get<Clan>(`${this.API}/${id}`); }
  getDominantes(limit = 10): Observable<Clan[]> { return this.http.get<Clan[]>(`${this.API}/dominantes?limit=${limit}`); }
  getEnGuerra(): Observable<Clan[]> { return this.http.get<Clan[]>(`${this.API}/en-guerra`); }
  getTopTerritorio(limit = 10): Observable<Clan[]> { return this.http.get<Clan[]>(`${this.API}/top/territorio?limit=${limit}`); }
  getComunidad(): Observable<Clan[]> { return this.http.get<Clan[]>(`${this.API}/comunidad`); }
}
