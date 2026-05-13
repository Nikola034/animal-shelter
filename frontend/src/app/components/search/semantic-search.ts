import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { TextareaModule } from 'primeng/textarea';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { TagModule } from 'primeng/tag';
import { DividerModule } from 'primeng/divider';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

import { AnimalService } from '../../services/animal/animal-service';
import { RagSearchMatch } from '../../dto/animal/RagSearchResponse';
import { getAnimalStatusSeverity, AnimalStatus } from '../../dto/animal/AnimalStatus';

@Component({
  selector: 'app-semantic-search',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CardModule,
    ButtonModule,
    DropdownModule,
    TextareaModule,
    ProgressSpinnerModule,
    TagModule,
    DividerModule,
    ToastModule,
  ],
  providers: [MessageService],
  templateUrl: 'semantic-search.html',
})
export class SemanticSearch implements OnDestroy {

  query = '';
  limit = 5;
  limitOptions = [
    { label: '3 results', value: 3 },
    { label: '5 results', value: 5 },
    { label: '10 results', value: 10 },
  ];

  searching = false;
  results: RagSearchMatch[] = [];
  answer = '';
  lastQuery = '';

  private destroy$ = new Subject<void>();

  constructor(
    private animalService: AnimalService,
    private router: Router,
    private messageService: MessageService,
  ) {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  search(): void {
    const q = this.query.trim();
    if (!q) return;

    this.searching = true;
    this.results = [];
    this.answer = '';
    this.lastQuery = q;

    this.animalService.ragSearch(q, this.limit)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.results = response.matched_animals;
          this.answer = response.answer;
          this.searching = false;
        },
        error: () => {
          this.searching = false;
          this.messageService.add({
            severity: 'error',
            summary: 'AI Search Error',
            detail: 'Failed to perform AI search. Please try again.',
            life: 5000,
          });
        },
      });
  }

  viewAnimal(id: string): void {
    this.router.navigate(['/app/animals', id]);
  }

  getScorePercent(score: number): number {
    return Math.round(score * 100);
  }

  getScoreSeverity(score: number): string {
    if (score >= 0.8) return 'success';
    if (score >= 0.6) return 'warn';
    return 'info';
  }

  getStatusSeverity(status: string): string {
    return getAnimalStatusSeverity(status as AnimalStatus);
  }
}
