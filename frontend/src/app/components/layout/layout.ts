import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { Topbar } from './topbar/topbar';
import { Sidebar } from './sidebar/sidebar';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    Topbar,
    Sidebar
  ],
  templateUrl: 'layout.html'
})
export class Layout {

  sidebarVisible = true;

  onToggleSidebar(): void {
    this.sidebarVisible = !this.sidebarVisible;
  }
}
