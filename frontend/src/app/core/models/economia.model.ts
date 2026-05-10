export interface Transaccion {
  id: number;
  vendedorNick: string;
  compradorNick: string;
  itemNombre: string;
  cantidad: number;
  precioUnitario: number;
  total: number;
  fecha: string;
  mercado: string;
  tipo: string;
}

export interface EconomiaStats {
  volumenTotal: number;
  totalTransacciones: number;
  transaccionPromedio: number;
  topItems: any[];
  topVendedores: any[];
  volumenPorMercado: any[];
  actividadPorTipo: any[];
  ultimasTransacciones: Transaccion[];
}
