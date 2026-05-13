import { Component, HostListener, AfterViewInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'mg-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements AfterViewInit, OnDestroy {
  form: FormGroup;
  loading = false;
  error = '';
  showPassword = false;

  mouseX = 50;
  mouseY = 50;

  particles = Array.from({ length: 20 }, (_, i) => ({
    x: Math.random() * 100,
    dur: 4 + Math.random() * 6,
    delay: -(Math.random() * 8),
    size: 2 + Math.random() * 5
  }));

  @ViewChild('netherCanvas') canvasRef!: ElementRef<HTMLCanvasElement>;
  private animFrame!: number;
  private resizeListener!: () => void;

  @HostListener('mousemove', ['$event'])
  onMouseMove(e: MouseEvent) {
    this.mouseX = (e.clientX / window.innerWidth) * 100;
    this.mouseY = (e.clientY / window.innerHeight) * 100;
  }

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {
    if (this.auth.isAuthenticated()) this.router.navigate(['/dashboard']);
    this.form = this.fb.group({
      username: ['admin', [Validators.required]],
      password: ['admin123', [Validators.required]]
    });
  }

  ngAfterViewInit(): void {
    this.startNetherCanvas();
  }

  ngOnDestroy(): void {
    cancelAnimationFrame(this.animFrame);
    window.removeEventListener('resize', this.resizeListener);
  }

  // ─── Nether Portal Canvas Animation ──────────────────────────────────────
  // Replica el GIF del portal del Nether: grid de píxeles grandes púrpuras
  // animados con ruido sinusoidal en espiral — igual al bloque in-game.

  private startNetherCanvas(): void {
    const canvas = this.canvasRef.nativeElement;
    const ctx = canvas.getContext('2d')!;

    // Tamaño de cada "píxel" Minecraft — ajustado para verse como el GIF
    const CELL = 40;

    // Paleta de colores extraída del GIF del portal del Nether
    // De más oscuro a más claro, todos en tono azul-violeta
    const PALETTE: [number, number, number][] = [
      [14,   0,  66],   // 0 — casi negro violeta
      [28,   0,  99],   // 1 — morado muy oscuro
      [44,   0, 132],   // 2 — indigo oscuro
      [66,   0, 154],   // 3 — morado oscuro
      [88,  17, 176],   // 4 — morado medio
      [110,  33, 198],  // 5 — morado
      [132,  55, 210],  // 6 — morado claro
      [154,  77, 220],  // 7 — lila
      [110,   0, 187],  // 8 — morado saturado
      [77,   0, 154],   // 9 — morado medio-oscuro
      [55,   0, 132],   // 10 — variante oscura
    ];

    const resize = () => {
      canvas.width  = window.innerWidth;
      canvas.height = window.innerHeight;
    };
    resize();
    this.resizeListener = resize;
    window.addEventListener('resize', this.resizeListener);

    let t = 0;

    const draw = () => {
      const W = canvas.width;
      const H = canvas.height;
      const cols = Math.ceil(W / CELL) + 1;
      const rows = Math.ceil(H / CELL) + 1;

      // Incremento muy pequeño = movimiento lento como en Minecraft
      t += 0.006;

      for (let r = 0; r < rows; r++) {
        for (let c = 0; c < cols; c++) {
          // Ruido sinusoidal multicapa que crea el efecto de vórtice/espiral
          // Cada capa aporta una "onda" diferente — la combinación da el swirl
          const n =
            Math.sin(c * 0.55 + t * 1.3) * 0.22 +
            Math.sin(r * 0.65 + t * 1.0) * 0.22 +
            Math.sin((c + r) * 0.38 + t * 0.75) * 0.20 +
            Math.sin((c - r) * 0.28 + t * 1.6) * 0.18 +
            Math.sin(c * 0.18 - r * 0.22 + t * 0.5) * 0.18;

          // Normalizar a rango 0..1
          const norm  = (n + 1) / 2;
          const idx   = Math.min(Math.floor(norm * PALETTE.length), PALETTE.length - 1);
          const [ri, gi, bi] = PALETTE[idx];

          ctx.fillStyle = `rgb(${ri},${gi},${bi})`;
          ctx.fillRect(c * CELL, r * CELL, CELL, CELL);
        }
      }

      this.animFrame = requestAnimationFrame(draw);
    };

    draw();
  }

  // ─── Login ────────────────────────────────────────────────────────────────

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = '';
    const { username, password } = this.form.value;
    this.auth.login(username, password).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: () => {
        this.error = 'Credenciales inválidas. Intenta de nuevo.';
        this.loading = false;
      }
    });
  }
}
