import { Component, OnDestroy, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { CursorPetService, CursorPetMode } from '../cursor-pet.service';

@Component({
  selector: 'mg-wolf-pet',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="pet-wrap" #petEl [class.hidden]="mode === 'off'">

      <!-- WOLF -->
      <svg *ngIf="mode === 'wolf'"
           class="pet-svg wolf-svg"
           [class.pet-flip]="facingLeft"
           [class.pet-bounce]="isMoving"
           viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg">
        <rect x="8"  y="16" width="16" height="10" fill="#B0A898"/>
        <rect x="9"  y="17" width="5"  height="8"  fill="#D8D0C0"/>
        <rect x="18" y="16" width="6"  height="7"  fill="#888078"/>
        <rect x="18" y="7"  width="12" height="11" fill="#B0A898"/>
        <rect x="19" y="4"  width="3"  height="4"  fill="#888078"/>
        <rect x="25" y="4"  width="3"  height="4"  fill="#888078"/>
        <rect x="26" y="10" width="2"  height="2"  fill="#4455CC"/>
        <rect x="24" y="13" width="6"  height="5"  fill="#D8D0C0"/>
        <rect x="28" y="13" width="2"  height="2"  fill="#333030"/>
        <rect x="27" y="17" width="2"  height="2"  fill="#DD5555"/>
        <rect x="18" y="17" width="7"  height="2"  fill="#CC3333"/>
        <rect x="2"  y="12" width="7"  height="3"  fill="#888078"/>
        <rect x="3"  y="10" width="3"  height="3"  fill="#888078"/>
        <rect x="5"  y="9"  width="2"  height="2"  fill="#D8D0C0"/>
        <rect x="18" y="26" width="3"  height="5"  fill="#B0A898"/>
        <rect x="14" y="26" width="3"  height="5"  fill="#B0A898"/>
        <rect x="10" y="26" width="3"  height="5"  fill="#B0A898"/>
        <rect x="8"  y="26" width="3"  height="5"  fill="#B0A898"/>
        <rect x="8"  y="30" width="5"  height="2"  fill="#888078"/>
        <rect x="14" y="30" width="7"  height="2"  fill="#888078"/>
      </svg>

      <!-- DIAMOND SWORD -->
      <img *ngIf="mode === 'sword'"
           src="assets/items/diamond_sword.png"
           class="pet-svg sword-img"
           [class.pet-bounce]="isMoving"
           alt="sword"
           draggable="false">

    </div>
  `,
  styles: [`
    .pet-wrap {
      position: fixed;
      pointer-events: none;
      z-index: 99999;
      left: -100px;
      top: -100px;
      width: 48px;
      height: 48px;
    }
    .pet-wrap.hidden { display: none; }

    .pet-svg {
      width: 48px;
      height: 48px;
      display: block;
      filter: drop-shadow(1px 2px 3px rgba(0,0,0,0.6));
    }

    /* Wolf SVG specific */
    .wolf-svg {
      image-rendering: pixelated;
      shape-rendering: crispEdges;
    }
    .pet-flip { transform: scaleX(-1); }

    /* Sword image */
    .sword-img {
      image-rendering: pixelated;
      image-rendering: crisp-edges;
      transform: rotate(-45deg);
      filter: drop-shadow(0 0 6px rgba(0,200,255,0.6)) drop-shadow(1px 2px 2px rgba(0,0,0,0.5));
    }

    /* Bounce animations */
    @keyframes petBounce {
      0%, 100% { transform: translateY(0); }
      50%       { transform: translateY(-4px); }
    }
    @keyframes petBounceFlip {
      0%, 100% { transform: scaleX(-1) translateY(0); }
      50%       { transform: scaleX(-1) translateY(-4px); }
    }
    @keyframes swordBounce {
      0%, 100% { transform: rotate(-45deg) translateY(0); }
      50%       { transform: rotate(-45deg) translateY(-4px); }
    }

    .wolf-svg.pet-bounce:not(.pet-flip) { animation: petBounce     0.3s steps(2) infinite; }
    .wolf-svg.pet-bounce.pet-flip       { animation: petBounceFlip 0.3s steps(2) infinite; }
    .sword-img.pet-bounce               { animation: swordBounce   0.3s steps(2) infinite; }
  `]
})
export class McWolfPetComponent implements AfterViewInit, OnDestroy {
  @ViewChild('petEl') petEl!: ElementRef<HTMLDivElement>;

  mode: CursorPetMode = 'wolf';
  isMoving  = false;
  facingLeft = false;

  private cx = -100; private cy = -100;
  private tx = -100; private ty = -100;
  private rafId = 0;
  private moveTimeout: any;
  private onMove!: (e: MouseEvent) => void;
  private sub!: Subscription;

  constructor(private petSvc: CursorPetService) {}

  ngAfterViewInit(): void {
    this.sub = this.petSvc.mode$.subscribe(m => this.mode = m);

    this.onMove = (e: MouseEvent) => {
      this.tx = e.clientX + 20;
      this.ty = e.clientY + 12;
    };
    document.addEventListener('mousemove', this.onMove);
    this.loop();
  }

  private loop = () => {
    if (this.mode !== 'off') {
      const dx = this.tx - this.cx;
      const dy = this.ty - this.cy;
      const dist = Math.sqrt(dx * dx + dy * dy);

      if (dist > 1.5) {
        const speed = Math.min(dist * 0.13, 20);
        this.cx += (dx / dist) * speed;
        this.cy += (dy / dist) * speed;
        this.facingLeft = dx < 0;
        this.isMoving = true;
        clearTimeout(this.moveTimeout);
        this.moveTimeout = setTimeout(() => { this.isMoving = false; }, 200);
      }

      const el = this.petEl?.nativeElement;
      if (el) { el.style.left = this.cx + 'px'; el.style.top = this.cy + 'px'; }
    }
    this.rafId = requestAnimationFrame(this.loop);
  };

  ngOnDestroy(): void {
    cancelAnimationFrame(this.rafId);
    document.removeEventListener('mousemove', this.onMove);
    clearTimeout(this.moveTimeout);
    this.sub?.unsubscribe();
  }
}
