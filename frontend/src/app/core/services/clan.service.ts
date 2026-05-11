import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Clan } from '../models/clan.model';

@Injectable({ providedIn: 'root' })
export class ClanService {
  private readonly API = 'http://localhost:8080/api/clanes';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Clan[]>                        { return this.http.get<Clan[]>(this.API); }
  getById(id: number): Observable<Clan>               { return this.http.get<Clan>(`${this.API}/${id}`); }
  getDominantes(limit = 10): Observable<Clan[]>       { return this.http.get<Clan[]>(`${this.API}/dominantes?limit=${limit}`); }
  getEnGuerra(): Observable<Clan[]>                   { return this.http.get<Clan[]>(`${this.API}/en-guerra`); }
  getTopTerritorio(limit = 10): Observable<Clan[]>    { return this.http.get<Clan[]>(`${this.API}/top/territorio?limit=${limit}`); }
  getNucleoComunidad(): Observable<Clan[]>            { return this.http.get<Clan[]>(`${this.API}/comunidad`); }

  create(data: Partial<Clan>): Observable<Clan>             { return this.http.post<Clan>(this.API, data); }
  update(id: number, data: Partial<Clan>): Observable<Clan> { return this.http.put<Clan>(`${this.API}/${id}`, data); }
  delete(id: number): Observable<void>                      { return this.http.delete<void>(`${this.API}/${id}`); }

  declararAlianza(id1: number, id2: number): Observable<any> { return this.http.post(`${this.API}/${id1}/alianza/${id2}`, {}); }
  declararGuerra(id1: number, id2: number): Observable<any>  { return this.http.post(`${this.API}/${id1}/guerra/${id2}`, {}); }
}
