import { Injectable } from '@angular/core';

// Servicio de partículas de XP — igual a las bolitas verdes de Minecraft
// Uso: inject McXpService → llamar .burst(event) en el click de un botón guardar

@Injectable({ providedIn: 'root' })
export class McXpService {
  /** Genera una ráfaga de partículas XP en la posición del evento del mouse */
  burst(event: MouseEvent | { clientX: number; clientY: number }, count = 12): void {
    const x = event.clientX;
    const y = event.clientY;

    for (let i = 0; i < count; i++) {
      const particle = document.createElement('div');
      particle.className = 'mc-xp-particle';

      // Dirección aleatoria
      const angle  = Math.random() * Math.PI * 2;
      const speed  = 40 + Math.random() * 80;
      const dx     = Math.cos(angle) * speed;
      const dy     = Math.sin(angle) * speed - 60; // tira hacia arriba
      const size   = 5 + Math.random() * 6;
      const delay  = Math.random() * 150;

      Object.assign(particle.style, {
        position:   'fixed',
        left:       x + 'px',
        top:        y + 'px',
        width:      size + 'px',
        height:     size + 'px',
        borderRadius: '2px',          // cuadrado pixelado
        background: `radial-gradient(circle, #a8ff78 0%, #56ab2f 100%)`,
        boxShadow:  '0 0 6px #78ff44, 0 0 12px #56ab2f',
        pointerEvents: 'none',
        zIndex:     '99999',
        transition: `transform ${500 + Math.random() * 300}ms cubic-bezier(0.25, 0.46, 0.45, 0.94), opacity 400ms ease`,
        transform:  'translate(-50%, -50%) scale(1)',
        opacity:    '1',
        imageRendering: 'pixelated',
      });

      document.body.appendChild(particle);

      setTimeout(() => {
        particle.style.transform = `translate(calc(-50% + ${dx}px), calc(-50% + ${dy}px)) scale(0.2)`;
        particle.style.opacity = '0';
      }, delay + 10);

      setTimeout(() => {
        particle.remove();
      }, delay + 900);
    }
  }
}
