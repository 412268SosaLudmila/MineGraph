import { Component } from '@angular/core';
import { McWolfPetComponent } from './shared/mc-wolf-pet/mc-wolf-pet.component';

@Component({
  selector: 'mg-root',
  template: `
    <router-outlet></router-outlet>
    <mg-mc-toast></mg-mc-toast>
    <mg-wolf-pet></mg-wolf-pet>
  `,
  styles: [':host { display: block; height: 100%; }']
})
export class AppComponent {}
