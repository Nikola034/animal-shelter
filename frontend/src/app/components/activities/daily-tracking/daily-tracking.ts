import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, forkJoin, takeUntil } from 'rxjs';

import { CardModule } from 'primeng/card';
import { DropdownModule } from 'primeng/dropdown';
import { DatePickerModule } from 'primeng/datepicker';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextarea } from 'primeng/inputtextarea';
import { ButtonModule } from 'primeng/button';
import { SliderModule } from 'primeng/slider';
import { TagModule } from 'primeng/tag';
import { DividerModule } from 'primeng/divider';
import { ToastModule } from 'primeng/toast';
import { TableModule } from 'primeng/table';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TooltipModule } from 'primeng/tooltip';
import { CheckboxModule } from 'primeng/checkbox';
import { MessageService, ConfirmationService } from 'primeng/api';

import { ActivityService } from '../../../services/activity/activity-service';
import { AnimalService } from '../../../services/animal/animal-service';
import { AuthService } from '../../../services/auth/auth-service';
import { AnimalResponse } from '../../../dto/animal/AnimalResponse';
import {
  DailyMeasurementResponse,
  ActivityRecordResponse,
  FeedingRecordResponse
} from '../../../dto/activity/DailyRecordResponse';
import { FOOD_TYPE_OPTIONS, getFoodTypeLabel, getFoodTypeSeverity } from '../../../dto/activity/FoodType';

// Activity type suggestions
export const ACTIVITY_TYPE_OPTIONS: { label: string; value: string }[] = [
  { label: 'Walking', value: 'walking' },
  { label: 'Playing', value: 'playing' },
  { label: 'Training', value: 'training' },
  { label: 'Social Interaction', value: 'social_interaction' },
  { label: 'Wheel Running', value: 'wheel_running' },
  { label: 'Digging', value: 'digging' },
  { label: 'Swimming', value: 'swimming' },
  { label: 'Grooming', value: 'grooming' },
  { label: 'Running', value: 'running' },
  { label: 'Climbing', value: 'climbing' }
];

@Component({
  selector: 'app-daily-tracking',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CardModule,
    DropdownModule,
    DatePickerModule,
    InputNumberModule,
    InputTextModule,
    InputTextarea,
    ButtonModule,
    SliderModule,
    TagModule,
    DividerModule,
    ToastModule,
    TableModule,
    ConfirmDialogModule,
    TooltipModule,
    CheckboxModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: 'daily-tracking.html'
})
export class DailyTracking implements OnInit, OnDestroy {

  // Animal selection
  animals: { label: string; value: string }[] = [];
  selectedAnimalId: string | null = null;
  selectedDate: Date = new Date();
  today: Date = new Date();  // fixed maxDate for datepicker
  loadingAnimals = true;

  // Loading state
  loadingData = false;
  dataLoaded = false;

  // Measurement (separate collection)
  measurement: DailyMeasurementResponse | null = null;
  weightGrams: number | null = null;
  temperatureCelsius: number | null = null;
  energyLevel: number = 5;
  moodLevel: number = 5;
  savingMeasurement = false;

  // Activities (separate collection)
  activities: ActivityRecordResponse[] = [];
  newActivityType: string | null = null;
  newDurationMinutes: number | null = null;
  newActivityTime: Date | null = null;
  newActivityNotes: string = '';
  addingActivity = false;
  activityTypeOptions = ACTIVITY_TYPE_OPTIONS;

  // Feedings (separate collection)
  feedings: FeedingRecordResponse[] = [];
  newFoodType: string | null = null;
  newQuantityGrams: number | null = null;
  newMealTime: Date | null = null;
  newFeedingNotes: string = '';
  newConsumedFully: boolean = false;
  addingFeeding = false;
  foodTypeOptions = FOOD_TYPE_OPTIONS;

  private destroy$ = new Subject<void>();

  constructor(
    private activityService: ActivityService,
    private animalService: AnimalService,
    private authService: AuthService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit(): void {
    this.loadAnimals();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // ── Animal Loading ───────────────────────────────────────

  loadAnimals(): void {
    this.loadingAnimals = true;
    this.animalService.getAllAnimals()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.animals = response.animals.map((a: AnimalResponse) => ({
            label: `${a.name} (${a.category}${a.breed ? ' - ' + a.breed : ''})`,
            value: a.id
          }));
          this.loadingAnimals = false;
        },
        error: () => {
          this.messageService.add({
            severity: 'error', summary: 'Error',
            detail: 'Failed to load animals', life: 5000
          });
          this.loadingAnimals = false;
        }
      });
  }

  // ── Data Loading (3 parallel requests) ─────────────────

  onAnimalOrDateChange(): void {
    if (this.selectedAnimalId && this.selectedDate) {
      this.loadAllData();
    }
  }

  loadAllData(): void {
    if (!this.selectedAnimalId) return;

    this.loadingData = true;
    this.dataLoaded = false;
    const dateStr = this.formatDate(this.selectedDate);

    forkJoin({
      measurement: this.activityService.getMeasurement(this.selectedAnimalId, dateStr),
      activities: this.activityService.getActivities(this.selectedAnimalId, dateStr),
      feedings: this.activityService.getFeedings(this.selectedAnimalId, dateStr)
    }).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.measurement = result.measurement;
          this.activities = result.activities;
          this.feedings = result.feedings;
          this.populateFromMeasurement(result.measurement);
          this.loadingData = false;
          this.dataLoaded = true;
        },
        error: () => {
          this.messageService.add({
            severity: 'error', summary: 'Error',
            detail: 'Failed to load tracking data', life: 5000
          });
          this.loadingData = false;
        }
      });
  }

  private populateFromMeasurement(m: DailyMeasurementResponse | null): void {
    this.weightGrams = m?.weight_grams ?? null;
    this.temperatureCelsius = m?.temperature_celsius ?? null;
    this.energyLevel = m?.energy_level ?? 5;
    this.moodLevel = m?.mood_level ?? 5;
  }

  // ── Save Measurement ──────────────────────────────────

  saveMeasurement(): void {
    if (!this.selectedAnimalId) return;

    this.savingMeasurement = true;
    const dateStr = this.formatDate(this.selectedDate);

    this.activityService.saveMeasurement({
      animal_id: this.selectedAnimalId,
      date: dateStr,
      weight_grams: this.weightGrams,
      temperature_celsius: this.temperatureCelsius,
      energy_level: this.energyLevel,
      mood_level: this.moodLevel
    }).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (saved) => {
          this.measurement = saved;
          this.savingMeasurement = false;
          this.messageService.add({
            severity: 'success', summary: 'Saved',
            detail: 'Measurement updated', life: 2000
          });
        },
        error: (err) => {
          this.savingMeasurement = false;
          this.messageService.add({
            severity: 'error', summary: 'Error',
            detail: err.error?.message || 'Failed to save measurement', life: 5000
          });
        }
      });
  }

  // ── Activity Management ───────────────────────────────

  addActivity(): void {
    if (!this.selectedAnimalId || !this.newActivityType || !this.newDurationMinutes || !this.newActivityTime) return;

    this.addingActivity = true;
    const recordedAt = this.combineDateAndTime(this.selectedDate, this.newActivityTime);

    this.activityService.addActivity({
      animal_id: this.selectedAnimalId,
      activity_type: this.newActivityType,
      duration_minutes: this.newDurationMinutes,
      notes: this.newActivityNotes || null,
      recorded_at: recordedAt
    }).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (created) => {
          this.activities.push(created);
          this.activities.sort((a, b) => a.recorded_at.localeCompare(b.recorded_at));
          this.resetActivityForm();
          this.addingActivity = false;
          this.messageService.add({
            severity: 'success', summary: 'Added',
            detail: 'Activity recorded', life: 2000
          });
        },
        error: (err) => {
          this.addingActivity = false;
          this.messageService.add({
            severity: 'error', summary: 'Error',
            detail: err.error?.message || 'Failed to add activity', life: 5000
          });
        }
      });
  }

  confirmDeleteActivity(activity: ActivityRecordResponse): void {
    this.confirmationService.confirm({
      message: `Remove "${activity.activity_type}" activity?`,
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => this.deleteActivity(activity.id)
    });
  }

  private deleteActivity(id: string): void {
    this.activityService.deleteActivity(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.activities = this.activities.filter(a => a.id !== id);
          this.messageService.add({
            severity: 'success', summary: 'Removed',
            detail: 'Activity removed', life: 2000
          });
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error', summary: 'Error',
            detail: err.error?.message || 'Failed to remove activity', life: 5000
          });
        }
      });
  }

  private resetActivityForm(): void {
    this.newActivityType = null;
    this.newDurationMinutes = null;
    this.newActivityTime = null;
    this.newActivityNotes = '';
  }

  // ── Feeding Management ────────────────────────────────

  addFeeding(): void {
    if (!this.selectedAnimalId || !this.newFoodType || !this.newQuantityGrams || !this.newMealTime) return;

    this.addingFeeding = true;
    const mealTime = this.combineDateAndTime(this.selectedDate, this.newMealTime);

    this.activityService.addFeeding({
      animal_id: this.selectedAnimalId,
      food_type: this.newFoodType,
      quantity_grams: this.newQuantityGrams,
      meal_time: mealTime,
      notes: this.newFeedingNotes || null,
      consumed_fully: this.newConsumedFully || null
    }).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (created) => {
          this.feedings.push(created);
          this.feedings.sort((a, b) => a.meal_time.localeCompare(b.meal_time));
          this.resetFeedingForm();
          this.addingFeeding = false;
          this.messageService.add({
            severity: 'success', summary: 'Added',
            detail: 'Feeding recorded', life: 2000
          });
        },
        error: (err) => {
          this.addingFeeding = false;
          this.messageService.add({
            severity: 'error', summary: 'Error',
            detail: err.error?.message || 'Failed to add feeding', life: 5000
          });
        }
      });
  }

  confirmDeleteFeeding(feeding: FeedingRecordResponse): void {
    this.confirmationService.confirm({
      message: `Remove "${getFoodTypeLabel(feeding.food_type)}" feeding?`,
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => this.deleteFeeding(feeding.id)
    });
  }

  private deleteFeeding(id: string): void {
    this.activityService.deleteFeeding(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.feedings = this.feedings.filter(f => f.id !== id);
          this.messageService.add({
            severity: 'success', summary: 'Removed',
            detail: 'Feeding removed', life: 2000
          });
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error', summary: 'Error',
            detail: err.error?.message || 'Failed to remove feeding', life: 5000
          });
        }
      });
  }

  private resetFeedingForm(): void {
    this.newFoodType = null;
    this.newQuantityGrams = null;
    this.newMealTime = null;
    this.newFeedingNotes = '';
    this.newConsumedFully = false;
  }

  // ── Helpers ──────────────────────────────────────────────

  canManage(): boolean {
    return this.authService.canManageAnimals();
  }

  getFoodTypeSeverity(type: string): string {
    return getFoodTypeSeverity(type);
  }

  getFoodTypeLabel(type: string): string {
    return getFoodTypeLabel(type);
  }

  getActivityTypeLabel(value: string): string {
    const option = ACTIVITY_TYPE_OPTIONS.find(o => o.value === value);
    return option ? option.label : value;
  }

  getTotalActivityMinutes(): number {
    return this.activities.reduce((sum, a) => sum + a.duration_minutes, 0);
  }

  getTotalFeedingGrams(): number {
    return this.feedings.reduce((sum, f) => sum + f.quantity_grams, 0);
  }

  getEnergyLabel(): string {
    if (this.energyLevel <= 3) return 'Low';
    if (this.energyLevel <= 6) return 'Medium';
    return 'High';
  }

  getEnergySeverity(): string {
    if (this.energyLevel <= 3) return 'danger';
    if (this.energyLevel <= 6) return 'warn';
    return 'success';
  }

  getMoodLabel(): string {
    if (this.moodLevel <= 3) return 'Low';
    if (this.moodLevel <= 6) return 'Neutral';
    return 'Happy';
  }

  getMoodSeverity(): string {
    if (this.moodLevel <= 3) return 'danger';
    if (this.moodLevel <= 6) return 'warn';
    return 'success';
  }

  formatInstantTime(instantStr: string): string {
    if (!instantStr) return '';
    const date = new Date(instantStr);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false });
  }

  private formatDate(date: Date): string {
    const y = date.getFullYear();
    const m = (date.getMonth() + 1).toString().padStart(2, '0');
    const d = date.getDate().toString().padStart(2, '0');
    return `${y}-${m}-${d}`;
  }

  private combineDateAndTime(date: Date, time: Date): string {
    const pad = (n: number) => n.toString().padStart(2, '0');
    const y = date.getFullYear();
    const m = pad(date.getMonth() + 1);
    const d = pad(date.getDate());
    const hh = pad(time.getHours());
    const mm = pad(time.getMinutes());
    return `${y}-${m}-${d}T${hh}:${mm}:00`;
  }
}
