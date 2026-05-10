import { Component, OnInit, OnDestroy, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { JugadorService } from '../../core/services/jugador.service';
import { GrafoData, GrafoNodo, GrafoArista } from '../../core/models/grafo.model';

declare const vis: any;

@Component({
  selector: 'mg-graph',
  templateUrl: './graph.component.html',
  styleUrls: ['./graph.component.scss']
})
export class GraphComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('networkContainer') networkContainer!: ElementRef;

  private network: any = null;
  private visData: any = null;
  grafoData: GrafoData | null = null;
  loading = true;
  selectedNode: GrafoNodo | null = null;

  // Filters
  showFriends = true;
  showEnemies = true;
  showTrade = true;
  searchQuery = '';
  filterType = 'all';

  // Stats
  totalNodes = 0;
  totalEdges = 0;
  onlineNodes = 0;

  constructor(private jugadorService: JugadorService) {}

  ngOnInit(): void {
    this.jugadorService.getGrafo().subscribe({
      next: (data) => {
        this.grafoData = data;
        this.totalNodes = data.nodes?.length || 0;
        this.totalEdges = data.edges?.length || 0;
        this.onlineNodes = data.nodes?.filter(n => n.online).length || 0;
        this.loading = false;
        setTimeout(() => this.initNetwork(data), 100);
      },
      error: () => { this.loading = false; }
    });
  }

  ngAfterViewInit(): void {}

  private initNetwork(data: GrafoData): void {
    if (!this.networkContainer?.nativeElement) return;

    const nodes = new (window as any).vis.DataSet(
      data.nodes.map(n => ({
        id: n.id,
        label: n.label,
        title: this.buildTooltip(n),
        size: n.size || 18,
        color: {
          background: n.online ? this.getNodeColor(n) : '#22243a',
          border: this.getNodeColor(n),
          highlight: { background: this.getNodeColor(n), border: '#ffffff' },
          hover: { background: this.getNodeColor(n), border: '#ffffff' }
        },
        font: {
          color: n.online ? '#e2e8f0' : '#4a5568',
          size: 11,
          face: 'Rajdhani',
          strokeWidth: 2,
          strokeColor: '#08090f'
        },
        borderWidth: n.online ? 2.5 : 1.5,
        borderWidthSelected: 4,
        shadow: n.online ? { enabled: true, color: this.getNodeColor(n) + '55', size: 14, x: 0, y: 0 } : false,
        group: n.clan || 'Sin Clan'
      }))
    );

    const edges = new (window as any).vis.DataSet(
      data.edges.map(e => ({
        id: e.id,
        from: e.from,
        to: e.to,
        color: { color: e.color + 'bb', highlight: e.color, hover: e.color, opacity: 0.8 },
        width: e.tipo === 'COMERCIA_CON' ? 1.5 : (e.width || 1.2),
        smooth: { enabled: true, type: 'dynamic' },
        arrows: e.tipo !== 'AMIGO_DE' ? { to: { enabled: true, scaleFactor: 0.6, type: 'arrow' } } : undefined,
        title: this.buildEdgeTooltip(e.tipo),
        dashes: e.tipo === 'ENEMIGO_DE' ? [4, 4] : false
      }))
    );

    this.visData = { nodes, edges };

    const options = {
      physics: {
        enabled: true,
        solver: 'forceAtlas2Based',
        forceAtlas2Based: {
          gravitationalConstant: -80,
          centralGravity: 0.005,
          springLength: 160,
          springConstant: 0.05,
          damping: 0.5,
          avoidOverlap: 0.8
        },
        stabilization: { enabled: true, iterations: 200, updateInterval: 10, fit: true },
        maxVelocity: 40,
        minVelocity: 0.1
      },
      interaction: {
        hover: true,
        tooltipDelay: 100,
        zoomView: true,
        dragView: true,
        navigationButtons: false,
        keyboard: { enabled: true, bindToWindow: false },
        hideEdgesOnDrag: true
      },
      nodes: {
        shape: 'dot',
        scaling: { min: 10, max: 38, label: { enabled: true, min: 10, max: 14 } }
      },
      edges: {
        scaling: { min: 0.5, max: 5 },
        selectionWidth: 3
      }
    };

    this.network = new (window as any).vis.Network(
      this.networkContainer.nativeElement,
      this.visData,
      options
    );

    this.network.on('click', (params: any) => {
      if (params.nodes.length > 0) {
        const nodeId = params.nodes[0];
        const node = data.nodes.find(n => n.id === nodeId.toString());
        this.selectedNode = node || null;
      } else {
        this.selectedNode = null;
      }
    });
  }

  private buildTooltip(n: GrafoNodo): HTMLElement {
    const el = document.createElement('div');
    el.style.cssText = 'background:#0e1018;border:1px solid rgba(108,99,255,0.35);border-radius:8px;padding:10px 14px;font-family:Rajdhani,sans-serif;color:#e2e8f0;min-width:150px;max-width:210px;box-shadow:0 4px 20px rgba(0,0,0,0.6);pointer-events:none';
    const statusColor = n.online ? '#00ff88' : '#718096';
    const nodeColor = this.getNodeColor(n);
    el.innerHTML = `
      <div style="display:flex;align-items:center;gap:8px;margin-bottom:8px;padding-bottom:7px;border-bottom:1px solid rgba(255,255,255,0.07)">
        <div style="width:28px;height:28px;border-radius:6px;background:${nodeColor}22;border:1px solid ${nodeColor}66;display:flex;align-items:center;justify-content:center;font-size:10px;font-weight:700;color:${nodeColor}">${n.label.substring(0,2).toUpperCase()}</div>
        <div style="font-weight:700;font-size:13px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">${n.label}</div>
      </div>
      <div style="font-size:11px;color:#718096;margin-bottom:4px">${n.titulo || 'Jugador'}</div>
      <div style="font-size:11px;color:#a855f7;margin-bottom:3px">⬡ ${n.clan || 'Sin clan'}</div>
      <div style="display:flex;justify-content:space-between;margin-top:6px">
        <span style="font-size:11px;color:${statusColor}">● ${n.online ? 'Online' : 'Offline'}</span>
        <span style="font-size:11px;color:#00d4ff;font-family:Orbitron,monospace">Nv.${n.nivel}</span>
      </div>`;
    return el;
  }

  private buildEdgeTooltip(tipo: string): HTMLElement {
    const el = document.createElement('div');
    const colors: Record<string, string> = { AMIGO_DE: '#00ff88', ENEMIGO_DE: '#ff4444', COMERCIA_CON: '#ffd700' };
    const labels: Record<string, string> = { AMIGO_DE: 'Amistad', ENEMIGO_DE: 'Enemistad', COMERCIA_CON: 'Comercio' };
    const c = colors[tipo] || '#718096';
    el.style.cssText = `background:#0e1018;border:1px solid ${c}44;border-radius:6px;padding:6px 10px;font-family:Rajdhani,sans-serif;font-size:12px;color:${c};pointer-events:none`;
    el.textContent = labels[tipo] || tipo;
    return el;
  }

  private getNodeColor(n: GrafoNodo): string {
    const colors = ['#6c63ff','#00d4ff','#ff6b6b','#ffd700','#00ff88','#a855f7','#ff9f43'];
    if (!n.clan) return '#4a5568';
    let h = 0;
    for (const c of n.clan) h = c.charCodeAt(0) + ((h << 5) - h);
    return colors[Math.abs(h) % colors.length];
  }

  applyFilters(): void {
    if (!this.visData || !this.grafoData) return;
    const filteredEdges = this.grafoData.edges.filter(e => {
      if (e.tipo === 'AMIGO_DE' && !this.showFriends) return false;
      if (e.tipo === 'ENEMIGO_DE' && !this.showEnemies) return false;
      if (e.tipo === 'COMERCIA_CON' && !this.showTrade) return false;
      return true;
    });
    this.visData.edges.clear();
    this.visData.edges.add(filteredEdges.map(e => ({
      id: e.id, from: e.from, to: e.to,
      color: { color: e.color + 'bb', highlight: e.color, hover: e.color },
      width: e.tipo === 'COMERCIA_CON' ? 1.5 : (e.width || 1.2),
      title: this.buildEdgeTooltip(e.tipo),
      dashes: e.tipo === 'ENEMIGO_DE' ? [4, 4] : false,
      arrows: e.tipo !== 'AMIGO_DE' ? { to: { enabled: true, scaleFactor: 0.6 } } : undefined,
      smooth: { enabled: true, type: 'dynamic' }
    })));
  }

  searchNode(): void {
    if (!this.network || !this.visData || !this.searchQuery) return;
    const node = this.grafoData?.nodes.find(n =>
      n.label.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
    if (node) {
      this.network.focus(node.id, { scale: 1.5, animation: { duration: 600, easingFunction: 'easeInOutQuad' } });
      this.network.selectNodes([node.id]);
      this.selectedNode = node;
    }
  }

  fitNetwork(): void {
    this.network?.fit({ animation: { duration: 800, easingFunction: 'easeInOutQuad' } });
  }

  stabilize(): void {
    this.network?.stabilize(150);
  }

  ngOnDestroy(): void {
    this.network?.destroy();
  }
}
