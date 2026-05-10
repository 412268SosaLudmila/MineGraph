export interface Jugador {
  id: number;
  nickname: string;
  nivel: number;
  experiencia: number;
  fechaRegistro: string;
  estadoOnline: boolean;
  monedas: number;
  reputacion: number;
  kills: number;
  muertes: number;
  horasJugadas: number;
  titulo: string;
  clanNombre: string | null;
  totalAmigos: number;
  totalEnemigos: number;
  kdRatio: number;
}
