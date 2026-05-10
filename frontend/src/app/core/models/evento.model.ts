export interface Evento {
  id: number;
  nombre: string;
  tipo: string;
  descripcion: string;
  fecha: string;
  duracionMinutos: number;
  participantes: number;
  recompensa: number;
  region: string;
  ganador: string | null;
  activo: boolean;
}
