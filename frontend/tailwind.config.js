/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts,scss}"
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        'mg-dark': '#08090f',
        'mg-darker': '#050508',
        'mg-card': 'rgba(16, 18, 32, 0.85)',
        'mg-primary': '#6c63ff',
        'mg-secondary': '#00d4ff',
        'mg-accent': '#ff6b6b',
        'mg-green': '#00ff88',
        'mg-gold': '#ffd700',
        'mg-purple': '#a855f7',
        'mg-border': 'rgba(108, 99, 255, 0.25)',
      },
      backgroundImage: {
        'mg-gradient': 'linear-gradient(135deg, #0a0e1a 0%, #0d1117 50%, #0a0814 100%)',
        'mg-card-gradient': 'linear-gradient(135deg, rgba(16,18,32,0.9) 0%, rgba(10,14,26,0.95) 100%)',
        'mg-neon-gradient': 'linear-gradient(135deg, #6c63ff, #00d4ff)',
        'mg-danger-gradient': 'linear-gradient(135deg, #ff4757, #ff6b6b)',
        'mg-success-gradient': 'linear-gradient(135deg, #00ff88, #2ed573)',
        'mg-gold-gradient': 'linear-gradient(135deg, #ffd700, #ff9f43)',
      },
      fontFamily: {
        'gaming': ['Rajdhani', 'Orbitron', 'sans-serif'],
        'mono': ['JetBrains Mono', 'monospace'],
      },
      boxShadow: {
        'neon-primary': '0 0 20px rgba(108, 99, 255, 0.4), 0 0 40px rgba(108, 99, 255, 0.2)',
        'neon-secondary': '0 0 20px rgba(0, 212, 255, 0.4), 0 0 40px rgba(0, 212, 255, 0.2)',
        'neon-green': '0 0 20px rgba(0, 255, 136, 0.4)',
        'neon-red': '0 0 20px rgba(255, 71, 87, 0.4)',
        'neon-gold': '0 0 20px rgba(255, 215, 0, 0.4)',
        'card': '0 8px 32px rgba(0, 0, 0, 0.5)',
        'card-hover': '0 16px 48px rgba(108, 99, 255, 0.2)',
      },
      animation: {
        'pulse-neon': 'pulseNeon 2s ease-in-out infinite',
        'float': 'float 3s ease-in-out infinite',
        'shimmer': 'shimmer 2s linear infinite',
        'glow': 'glow 2s ease-in-out infinite alternate',
      },
      keyframes: {
        pulseNeon: {
          '0%, 100%': { opacity: '1', filter: 'brightness(1)' },
          '50%': { opacity: '0.8', filter: 'brightness(1.3)' },
        },
        float: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-6px)' },
        },
        shimmer: {
          '0%': { backgroundPosition: '-200% 0' },
          '100%': { backgroundPosition: '200% 0' },
        },
        glow: {
          '0%': { boxShadow: '0 0 5px rgba(108,99,255,0.5)' },
          '100%': { boxShadow: '0 0 20px rgba(108,99,255,0.9), 0 0 40px rgba(108,99,255,0.5)' },
        }
      }
    },
  },
  plugins: [],
}
