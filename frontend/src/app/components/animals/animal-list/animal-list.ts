import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { ToastModule } from 'primeng/toast';
import { CardModule } from 'primeng/card';
import { TooltipModule } from 'primeng/tooltip';
import { DialogModule } from 'primeng/dialog';
import { TextareaModule } from 'primeng/textarea';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { MessageService } from 'primeng/api';

import { AnimalService } from '../../../services/animal/animal-service';
import { AuthService } from '../../../services/auth/auth-service';
import { AnimalResponse } from '../../../dto/animal/AnimalResponse';
import {
  AnimalCategory,
  ANIMAL_CATEGORY_OPTIONS,
  getCategorySeverity
} from '../../../dto/animal/AnimalCategory';
import {
  AnimalStatus,
  ANIMAL_STATUS_OPTIONS,
  getAnimalStatusSeverity
} from '../../../dto/animal/AnimalStatus';

@Component({
  selector: 'app-animal-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    TagModule,
    ButtonModule,
    DropdownModule,
    InputTextModule,
    ToastModule,
    CardModule,
    TooltipModule,
    DialogModule,
    TextareaModule,
    ProgressSpinnerModule
  ],
  providers: [MessageService],
  templateUrl: 'animal-list.html'
})
export class AnimalList implements OnInit, OnDestroy {

  animals: AnimalResponse[] = [];
  loading = true;

  // Filters
  nameFilter = '';
  categoryFilter: AnimalCategory | null = null;
  statusFilter: AnimalStatus | null = null;

  categoryFilterOptions = [
    { label: 'All Categories', value: null },
    ...ANIMAL_CATEGORY_OPTIONS
  ];

  statusFilterOptions = [
    { label: 'All Statuses', value: null },
    ...ANIMAL_STATUS_OPTIONS
  ];

  private destroy$ = new Subject<void>();

  constructor(
    private animalService: AnimalService,
    private authService: AuthService,
    private messageService: MessageService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAnimals();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadAnimals(): void {
    this.loading = true;

    const hasFilters = this.nameFilter || this.categoryFilter || this.statusFilter;

    const request = hasFilters
      ? this.animalService.searchAnimals(
          this.nameFilter || undefined,
          this.categoryFilter || undefined,
          this.statusFilter || undefined
        )
      : this.animalService.getAllAnimals();

    request.pipe(takeUntil(this.destroy$)).subscribe({
      next: (response) => {
        this.animals = response.animals;
        this.loading = false;
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load animals',
          life: 5000
        });
        this.loading = false;
      }
    });
  }

  onFilterChange(): void {
    this.loadAnimals();
  }

  clearFilters(): void {
    this.nameFilter = '';
    this.categoryFilter = null;
    this.statusFilter = null;
    this.loadAnimals();
  }

  viewAnimal(animal: AnimalResponse): void {
    this.router.navigate(['/app/animals', animal.id]);
  }

  registerAnimal(): void {
    this.router.navigate(['/app/animals/new']);
  }

  canManageAnimals(): boolean {
    return this.authService.canManageAnimals();
  }

  getCategorySeverity(category: AnimalCategory): string {
    return getCategorySeverity(category);
  }

  getStatusSeverity(status: AnimalStatus): string {
    return getAnimalStatusSeverity(status);
  }

  getImageUrl(path: string): string {
    return this.animalService.getImageUrl(path);
  }

  getGenderIcon(gender: string): string {
    switch (gender) {
      case 'Male': return 'pi pi-mars';
      case 'Female': return 'pi pi-venus';
      default: return 'pi pi-question-circle';
    }
  }
}
