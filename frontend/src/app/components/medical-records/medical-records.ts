import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, forkJoin, of, takeUntil } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { DatePickerModule } from 'primeng/datepicker';
import { ToastModule } from 'primeng/toast';
import { TooltipModule } from 'primeng/tooltip';
import { MessageService } from 'primeng/api';

import { AnimalService } from '../../services/animal/animal-service';
import { AnimalResponse } from '../../dto/animal/AnimalResponse';
import { MedicalRecordResponse } from '../../dto/animal/MedicalRecordResponse';
import {
  MedicalRecordType,
  MEDICAL_RECORD_TYPE_OPTIONS,
  getMedicalRecordTypeSeverity,
} from '../../dto/animal/MedicalRecordType';

interface MedicalRow extends MedicalRecordResponse {
  animal_name: string;
  animal_category: string;
}

@Component({
  selector: 'app-medical-records',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CardModule,
    TableModule,
    TagModule,
    ButtonModule,
    DropdownModule,
    InputTextModule,
    DatePickerModule,
    ToastModule,
    TooltipModule,
  ],
  providers: [MessageService],
  templateUrl: 'medical-records.html',
})
export class MedicalRecords implements OnInit, OnDestroy {

  loading = true;
  allRows: MedicalRow[] = [];
  filteredRows: MedicalRow[] = [];

  // Filters
  animalFilter: string | null = null;
  typeFilter: MedicalRecordType | null = null;
  dateRange: Date[] | null = null;

  animalOptions: { label: string; value: string }[] = [];
  typeOptions = [
    { label: 'All Types', value: null },
    ...MEDICAL_RECORD_TYPE_OPTIONS,
  ];

  private destroy$ = new Subject<void>();

  constructor(
    private animalService: AnimalService,
    private router: Router,
    private messageService: MessageService,
  ) {}

  ngOnInit(): void {
    this.loadAll();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadAll(): void {
    this.loading = true;
    this.animalService.getAllAnimals()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          const animals: AnimalResponse[] = response.animals;
          this.animalOptions = [
            { label: 'All Animals', value: '' },
            ...animals.map(a => ({ label: a.name, value: a.id })),
          ];

          if (animals.length === 0) {
            this.allRows = [];
            this.filteredRows = [];
            this.loading = false;
            return;
          }

          // Fetch medical records for every animal in parallel and flatten.
          // No backend endpoint returns all records across animals, so we do
          // an N-query fan-out here.
          const animalsById = new Map(animals.map(a => [a.id, a]));
          const calls = animals.map(a =>
            this.animalService.getMedicalRecords(a.id).pipe(
              map(res => res.records),
              catchError(() => of([])),
            )
          );

          forkJoin(calls).pipe(takeUntil(this.destroy$)).subscribe({
            next: (perAnimalRecords) => {
              const flat: MedicalRow[] = [];
              perAnimalRecords.forEach(records => {
                records.forEach(rec => {
                  const animal = animalsById.get(rec.animal_id);
                  flat.push({
                    ...rec,
                    animal_name: animal?.name ?? 'Unknown',
                    animal_category: animal?.category ?? '',
                  });
                });
              });

              flat.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
              this.allRows = flat;
              this.applyFilters();
              this.loading = false;
            },
            error: () => {
              this.messageService.add({
                severity: 'error',
                summary: 'Error',
                detail: 'Failed to load medical records.',
              });
              this.loading = false;
            },
          });
        },
        error: () => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to load animals.',
          });
          this.loading = false;
        },
      });
  }

  applyFilters(): void {
    let rows = this.allRows;

    if (this.animalFilter) {
      rows = rows.filter(r => r.animal_id === this.animalFilter);
    }
    if (this.typeFilter) {
      rows = rows.filter(r => r.type === this.typeFilter);
    }
    if (this.dateRange && this.dateRange.length === 2 && this.dateRange[0] && this.dateRange[1]) {
      const start = this.dateRange[0].getTime();
      const end = this.dateRange[1].getTime() + 24 * 3600 * 1000 - 1;
      rows = rows.filter(r => {
        const t = new Date(r.date).getTime();
        return t >= start && t <= end;
      });
    }

    this.filteredRows = rows;
  }

  clearFilters(): void {
    this.animalFilter = null;
    this.typeFilter = null;
    this.dateRange = null;
    this.applyFilters();
  }

  viewAnimal(animalId: string): void {
    this.router.navigate(['/app/animals', animalId]);
  }

  getTypeSeverity(type: MedicalRecordType): string {
    return getMedicalRecordTypeSeverity(type);
  }
}
