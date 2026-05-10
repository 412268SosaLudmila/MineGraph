import { Jugador } from './jugador.model';
import { Clan } from './clan.model';
import { Evento } from './evento.model';

export interface DashboardStats {
  totalJugadores: number;
  jugadoresOnline: number;
  totalClanes: number;
  totalEventos: number;
  totalTransacciones: number;
  volumenEconomico: number;
  topPvP: Jugador[];
  jugadoresMasConectados: Jugador[];
  clanesDominantes: Clan[];
  topComerciantes: Jugador[];
  eventosRecientes: Evento[];
  topItems: any[];
  volumenPorMercado: any[];
  jugadoresNuevos: Jugador[];
  eventosFrecuentes: any[];
  actividadPorHora: any[];
  distribucionNiveles: any[];
}
