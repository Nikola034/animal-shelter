import { Component, HostListener, OnInit } from '@angular/core';
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
export class Layout implements OnInit {

  sidebarVisible = true;
  isMobile = false;

  private readonly MOBILE_BREAKPOINT = 768;

  ngOnInit(): void {
    this.checkScreenSize();
  }

  @HostListener('window:resize')
  onResize(): void {
    this.checkScreenSize();
  }

  private checkScreenSize(): void {
    const wasMobile = this.isMobile;
    this.isMobile = window.innerWidth <= this.MOBILE_BREAKPOINT;
    // Auto-collapse sidebar when entering mobile
    if (this.isMobile && !wasMobile) {
      this.sidebarVisible = false;
    }
    // Auto-expand sidebar when leaving mobile
    if (!this.isMobile && wasMobile) {
      this.sidebarVisible = true;
    }
  }

  onToggleSidebar(): void {
    this.sidebarVisible = !this.sidebarVisible;
  }

  onBackdropClick(): void {
    if (this.isMobile) {
      this.sidebarVisible = false;
    }
  }
}
