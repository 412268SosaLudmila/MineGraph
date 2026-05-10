export interface GrafoNodo {
  id: string;
  label: string;
  group: string;
  nivel: number;
  online: boolean;
  clan: string | null;
  size: number;
  color?: string;
  titulo?: string;
}

export interface GrafoArista {
  id: string;
  from: string;
  to: string;
  tipo: string;
  color: string;
  width: number;
  label?: string;
}

export interface GrafoData {
  nodes: GrafoNodo[];
  edges: GrafoArista[];
}
