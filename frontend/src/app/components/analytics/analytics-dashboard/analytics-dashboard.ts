import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, forkJoin, takeUntil } from 'rxjs';

import { CardModule } from 'primeng/card';
import { TabViewModule } from 'primeng/tabview';
import { ChartModule } from 'primeng/chart';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { SelectModule } from 'primeng/select';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { DividerModule } from 'primeng/divider';
import { TooltipModule } from 'primeng/tooltip';
import { MessageService } from 'primeng/api';

import { AnalyticsService } from '../../../services/analytics/analytics-service';
import { AnimalService } from '../../../services/animal/animal-service';
import { AnimalResponse } from '../../../dto/animal/AnimalResponse';
import {
  PopulationOverview,
  ActivityTypeStats,
  DailySummary,
  HeatmapCell,
  TopAnimalActivity,
  FoodTypeStats,
  DashboardResponse,
  WeightTrendResponse,
  EnergyMoodTrendResponse,
} from '../../../dto/analytics/AnalyticsResponse';

import { ACTIVITY_TYPE_OPTIONS } from '../../activities/daily-tracking/daily-tracking';
import { getFoodTypeLabel } from '../../../dto/activity/FoodType';

@Component({
  selector: 'app-analytics-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CardModule,
    TabViewModule,
    ChartModule,
    TableModule,
    TagModule,
    SelectModule,
    ButtonModule,
    ToastModule,
    ProgressSpinnerModule,
    DividerModule,
    TooltipModule,
  ],
  providers: [MessageService],
  templateUrl: 'analytics-dashboard.html',
})
export class AnalyticsDashboard implements OnInit, OnDestroy {
  // ── Loading state ──────────────────────────────────────────
  loadingDashboard = true;
  loadingPopulation = true;
  loadingActivities = true;
  loadingFeeding = true;
  loadingHealth = false;

  // ── Period filter ──────────────────────────────────────────
  selectedDays = 30;
  periodOptions = [
    { label: 'Last 7 days', value: 7 },
    { label: 'Last 30 days', value: 30 },
    { label: 'Last 90 days', value: 90 },
    { label: 'Last 365 days', value: 365 },
  ];

  // ── Dashboard Overview ─────────────────────────────────────
  dashboard: DashboardResponse | null = null;

  // ── Population ─────────────────────────────────────────────
  population: PopulationOverview | null = null;
  categoryChartData: any;
  categoryChartOptions: any;
  statusChartData: any;
  statusChartOptions: any;
  genderChartData: any;
  genderChartOptions: any;
  ageChartData: any;
  ageChartOptions: any;

  // ── Activity Analytics ─────────────────────────────────────
  activityByType: ActivityTypeStats[] = [];
  activityDailySummary: DailySummary[] = [];
  activityHeatmap: HeatmapCell[] = [];
  topAnimals: TopAnimalActivity[] = [];
  activityByTypeChartData: any;
  activityByTypeChartOptions: any;
  activityDailyChartData: any;
  activityDailyChartOptions: any;
  heatmapGrid: number[][] = [];
  heatmapMaxValue = 0;

  // ── Feeding Analytics ──────────────────────────────────────
  feedingByType: FoodTypeStats[] = [];
  feedingDailySummary: DailySummary[] = [];
  feedingByTypeChartData: any;
  feedingByTypeChartOptions: any;
  feedingDailyChartData: any;
  feedingDailyChartOptions: any;

  // ── Health Analytics ───────────────────────────────────────
  animals: { label: string; value: string }[] = [];
  selectedAnimalId: string | null = null;
  healthDays = 30;
  weightTrend: WeightTrendResponse | null = null;
  energyMoodTrend: EnergyMoodTrendResponse | null = null;
  weightChartData: any;
  weightChartOptions: any;
  energyMoodChartData: any;
  energyMoodChartOptions: any;

  // Animal name cache for top animals table
  animalNameCache: Map<string, string> = new Map();

  // ── Reports & Export ─────────────────────────────────────────
  downloadingPdf = false;
  selectedReportSection = 'all';
  selectedReportPeriod = 'monthly';
  reportSectionOptions = [
    { label: 'All Sections', value: 'all' },
    { label: 'Population', value: 'population' },
    { label: 'Activities', value: 'activities' },
    { label: 'Feeding', value: 'feeding' },
    { label: 'Health', value: 'health' },
  ];
  reportPeriodOptions = [
    { label: 'Monthly (30 days)', value: 'monthly' },
    { label: 'Annual (365 days)', value: 'annual' },
  ];
  downloadingActivitiesCsv = false;
  downloadingFeedingsCsv = false;
  downloadingMeasurementsCsv = false;
  downloadingActivitiesExcel = false;
  downloadingFeedingsExcel = false;
  downloadingMeasurementsExcel = false;

  private destroy$ = new Subject<void>();

  // Heatmap helpers
  dayLabels = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
  hourLabels = Array.from(
    { length: 24 },
    (_, i) => `${i.toString().padStart(2, '0')}:00`,
  );

  constructor(
    private analyticsService: AnalyticsService,
    private animalService: AnimalService,
    private messageService: MessageService,
  ) {}

  ngOnInit(): void {
    this.loadAnimals();
    this.loadDashboard();
    this.loadPopulation();
    this.loadActivityAnalytics();
    this.loadFeedingAnalytics();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // ══════════════════════════════════════════════════════════
  //  DATA LOADING
  // ══════════════════════════════════════════════════════════

  loadAnimals(): void {
    this.animalService
      .getAllAnimals()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.animals = response.animals.map((a: AnimalResponse) => ({
            label: `${a.name} (${a.category})`,
            value: a.id,
          }));
          // Cache animal names
          response.animals.forEach((a: AnimalResponse) => {
            this.animalNameCache.set(a.id, a.name);
          });
        },
      });
  }

  loadDashboard(): void {
    this.loadingDashboard = true;
    this.analyticsService
      .getDashboard()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.dashboard = data;
          this.loadingDashboard = false;
        },
        error: () => {
          this.showError('Failed to load dashboard data');
          this.loadingDashboard = false;
        },
      });
  }

  loadPopulation(): void {
    this.loadingPopulation = true;
    this.analyticsService
      .getPopulationOverview()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.population = data;
          this.buildCategoryChart(data);
          this.buildStatusChart(data);
          this.buildGenderChart(data);
          this.buildAgeChart(data);
          this.loadingPopulation = false;
        },
        error: () => {
          this.showError('Failed to load population data');
          this.loadingPopulation = false;
        },
      });
  }

  loadActivityAnalytics(): void {
    this.loadingActivities = true;
    forkJoin({
      byType: this.analyticsService.getActivitiesByType(this.selectedDays),
      daily: this.analyticsService.getActivityDailySummary(this.selectedDays),
      heatmap: this.analyticsService.getActivityHeatmap(this.selectedDays),
      topAnimals: this.analyticsService.getTopAnimals(this.selectedDays, 10),
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.activityByType = result.byType;
          this.activityDailySummary = result.daily;
          this.activityHeatmap = result.heatmap;
          this.topAnimals = result.topAnimals;
          this.buildActivityByTypeChart();
          this.buildActivityDailyChart();
          this.buildHeatmapGrid();
          this.loadingActivities = false;
        },
        error: () => {
          this.showError('Failed to load activity analytics');
          this.loadingActivities = false;
        },
      });
  }

  loadFeedingAnalytics(): void {
    this.loadingFeeding = true;
    forkJoin({
      byType: this.analyticsService.getFeedingsByType(this.selectedDays),
      daily: this.analyticsService.getFeedingDailySummary(this.selectedDays),
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.feedingByType = result.byType;
          this.feedingDailySummary = result.daily;
          this.buildFeedingByTypeChart();
          this.buildFeedingDailyChart();
          this.loadingFeeding = false;
        },
        error: () => {
          this.showError('Failed to load feeding analytics');
          this.loadingFeeding = false;
        },
      });
  }

  loadHealthData(): void {
    if (!this.selectedAnimalId) return;

    this.loadingHealth = true;
    forkJoin({
      weight: this.analyticsService.getWeightTrend(
        this.selectedAnimalId,
        this.healthDays,
      ),
      energyMood: this.analyticsService.getEnergyMoodTrend(
        this.selectedAnimalId,
        this.healthDays,
      ),
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.weightTrend = result.weight;
          this.energyMoodTrend = result.energyMood;
          this.buildWeightChart();
          this.buildEnergyMoodChart();
          this.loadingHealth = false;
        },
        error: () => {
          this.showError('Failed to load health data');
          this.loadingHealth = false;
        },
      });
  }

  onPeriodChange(): void {
    this.loadActivityAnalytics();
    this.loadFeedingAnalytics();
  }

  onAnimalChange(): void {
    if (this.selectedAnimalId) {
      this.loadHealthData();
    }
  }

  onHealthDaysChange(): void {
    if (this.selectedAnimalId) {
      this.loadHealthData();
    }
  }

  // ══════════════════════════════════════════════════════════
  //  CHART BUILDERS — POPULATION
  // ══════════════════════════════════════════════════════════

  private buildCategoryChart(data: PopulationOverview): void {
    const colors = [
      '#42A5F5',
      '#66BB6A',
      '#FFA726',
      '#AB47BC',
      '#EF5350',
      '#26C6DA',
      '#EC407A',
    ];

    this.categoryChartData = {
      labels: data.by_category.map((c) => c.category),
      datasets: [
        {
          data: data.by_category.map((c) => c.count),
          backgroundColor: colors.slice(0, data.by_category.length),
        },
      ],
    };

    this.categoryChartOptions = {
      plugins: {
        legend: { position: 'bottom' },
      },
      responsive: true,
      maintainAspectRatio: false,
    };
  }

  private buildStatusChart(data: PopulationOverview): void {
    const statusColors: Record<string, string> = {
      Active: '#66BB6A',
      Adopted: '#42A5F5',
      Quarantine: '#FFA726',
      MedicalCare: '#EF5350',
      Deceased: '#78909C',
    };

    this.statusChartData = {
      labels: data.by_status.map((s) => s.status),
      datasets: [
        {
          data: data.by_status.map((s) => s.count),
          backgroundColor: data.by_status.map(
            (s) => statusColors[s.status] || '#9E9E9E',
          ),
        },
      ],
    };

    this.statusChartOptions = {
      plugins: {
        legend: { position: 'bottom' },
      },
      responsive: true,
      maintainAspectRatio: false,
    };
  }

  private buildGenderChart(data: PopulationOverview): void {
    const genderColors: Record<string, string> = {
      Male: '#42A5F5',
      Female: '#EC407A',
      Unknown: '#9E9E9E',
    };

    this.genderChartData = {
      labels: data.by_gender.map((g) => g.gender),
      datasets: [
        {
          data: data.by_gender.map((g) => g.count),
          backgroundColor: data.by_gender.map(
            (g) => genderColors[g.gender] || '#9E9E9E',
          ),
        },
      ],
    };

    this.genderChartOptions = {
      plugins: {
        legend: { position: 'bottom' },
      },
      responsive: true,
      maintainAspectRatio: false,
    };
  }

  private buildAgeChart(data: PopulationOverview): void {
    this.ageChartData = {
      labels: data.age_distribution.map((a) => a.range),
      datasets: [
        {
          label: 'Animals',
          data: data.age_distribution.map((a) => a.count),
          backgroundColor: '#42A5F5',
        },
      ],
    };

    this.ageChartOptions = {
      plugins: {
        legend: { display: false },
      },
      scales: {
        y: { beginAtZero: true, ticks: { stepSize: 1 } },
      },
      responsive: true,
      maintainAspectRatio: false,
    };
  }

  // ══════════════════════════════════════════════════════════
  //  CHART BUILDERS — ACTIVITIES
  // ══════════════════════════════════════════════════════════

  private buildActivityByTypeChart(): void {
    const colors = [
      '#42A5F5',
      '#66BB6A',
      '#FFA726',
      '#AB47BC',
      '#EF5350',
      '#26C6DA',
      '#EC407A',
      '#8D6E63',
      '#78909C',
      '#FFD54F',
    ];

    this.activityByTypeChartData = {
      labels: this.activityByType.map((a) =>
        this.getActivityLabel(a.activity_type),
      ),
      datasets: [
        {
          label: 'Total Minutes',
          data: this.activityByType.map((a) => a.total_minutes),
          backgroundColor: colors.slice(0, this.activityByType.length),
        },
      ],
    };

    this.activityByTypeChartOptions = {
      indexAxis: 'y',
      plugins: {
        legend: { display: false },
      },
      scales: {
        x: { beginAtZero: true },
      },
      responsive: true,
      maintainAspectRatio: false,
    };
  }

  private buildActivityDailyChart(): void {
    this.activityDailyChartData = {
      labels: this.activityDailySummary.map((d) =>
        this.formatDateLabel(d.date),
      ),
      datasets: [
        {
          label: 'Total Minutes',
          data: this.activityDailySummary.map((d) => d.total_value),
          borderColor: '#42A5F5',
          backgroundColor: 'rgba(66, 165, 245, 0.1)',
          fill: true,
          tension: 0.3,
        },
      ],
    };

    this.activityDailyChartOptions = {
      plugins: {
        legend: { display: false },
      },
      scales: {
        y: { beginAtZero: true },
        x: { ticks: { maxTicksLimit: 10 } },
      },
      responsive: true,
      maintainAspectRatio: false,
    };
  }

  private buildHeatmapGrid(): void {
    // Initialize 7x24 grid (days x hours)
    this.heatmapGrid = Array.from({ length: 7 }, () => Array(24).fill(0));
    this.heatmapMaxValue = 0;

    this.activityHeatmap.forEach((cell) => {
      // MongoDB $dayOfWeek: 1=Sunday, 7=Saturday
      const dayIdx = cell.day_of_week - 1;
      if (dayIdx >= 0 && dayIdx < 7 && cell.hour >= 0 && cell.hour < 24) {
        this.heatmapGrid[dayIdx][cell.hour] = cell.count;
        if (cell.count > this.heatmapMaxValue) {
          this.heatmapMaxValue = cell.count;
        }
      }
    });
  }

  // ══════════════════════════════════════════════════════════
  //  CHART BUILDERS — FEEDING
  // ══════════════════════════════════════════════════════════

  private buildFeedingByTypeChart(): void {
    const colors = [
      '#66BB6A',
      '#FFA726',
      '#42A5F5',
      '#AB47BC',
      '#EF5350',
      '#26C6DA',
      '#EC407A',
      '#8D6E63',
      '#78909C',
      '#FFD54F',
    ];

    this.feedingByTypeChartData = {
      labels: this.feedingByType.map((f) => getFoodTypeLabel(f.food_type)),
      datasets: [
        {
          data: this.feedingByType.map((f) => f.total_grams),
          backgroundColor: colors.slice(0, this.feedingByType.length),
        },
      ],
    };

    this.feedingByTypeChartOptions = {
      plugins: {
        legend: { position: 'bottom' },
      },
      responsive: true,
      maintainAspectRatio: false,
    };
  }

  private buildFeedingDailyChart(): void {
    this.feedingDailyChartData = {
      labels: this.feedingDailySummary.map((d) => this.formatDateLabel(d.date)),
      datasets: [
        {
          label: 'Total Grams',
          data: this.feedingDailySummary.map((d) => d.total_value),
          borderColor: '#66BB6A',
          backgroundColor: 'rgba(102, 187, 106, 0.1)',
          fill: true,
          tension: 0.3,
        },
      ],
    };

    this.feedingDailyChartOptions = {
      plugins: {
        legend: { display: false },
      },
      scales: {
        y: { beginAtZero: true },
        x: { ticks: { maxTicksLimit: 10 } },
      },
      responsive: true,
      maintainAspectRatio: false,
    };
  }

  // ══════════════════════════════════════════════════════════
  //  CHART BUILDERS — HEALTH
  // ══════════════════════════════════════════════════════════

  private buildWeightChart(): void {
    if (!this.weightTrend || this.weightTrend.data.length === 0) {
      this.weightChartData = null;
      return;
    }

    this.weightChartData = {
      labels: this.weightTrend.data.map((d) => this.formatDateLabel(d.date)),
      datasets: [
        {
          label: 'Weight (grams)',
          data: this.weightTrend.data.map((d) => d.weight_grams),
          borderColor: '#FFA726',
          backgroundColor: 'rgba(255, 167, 38, 0.1)',
          fill: true,
          tension: 0.3,
        },
      ],
    };

    this.weightChartOptions = {
      plugins: {
        legend: { display: true },
      },
      scales: {
        y: { beginAtZero: false },
        x: { ticks: { maxTicksLimit: 10 } },
      },
      responsive: true,
      maintainAspectRatio: false,
    };
  }

  private buildEnergyMoodChart(): void {
    if (!this.energyMoodTrend || this.energyMoodTrend.data.length === 0) {
      this.energyMoodChartData = null;
      return;
    }

    this.energyMoodChartData = {
      labels: this.energyMoodTrend.data.map((d) =>
        this.formatDateLabel(d.date),
      ),
      datasets: [
        {
          label: 'Energy Level',
          data: this.energyMoodTrend.data.map((d) => d.energy_level),
          borderColor: '#42A5F5',
          backgroundColor: 'rgba(66, 165, 245, 0.1)',
          fill: false,
          tension: 0.3,
        },
        {
          label: 'Mood Level',
          data: this.energyMoodTrend.data.map((d) => d.mood_level),
          borderColor: '#AB47BC',
          backgroundColor: 'rgba(171, 71, 188, 0.1)',
          fill: false,
          tension: 0.3,
        },
      ],
    };

    this.energyMoodChartOptions = {
      plugins: {
        legend: { position: 'top' },
      },
      scales: {
        y: { min: 0, max: 10, ticks: { stepSize: 1 } },
        x: { ticks: { maxTicksLimit: 10 } },
      },
      responsive: true,
      maintainAspectRatio: false,
    };
  }

  // ══════════════════════════════════════════════════════════
  //  HELPERS
  // ══════════════════════════════════════════════════════════

  getActivityLabel(type: string): string {
    const option = ACTIVITY_TYPE_OPTIONS.find((o) => o.value === type);
    return option ? option.label : type;
  }

  getFoodLabel(type: string): string {
    return getFoodTypeLabel(type);
  }

  getAnimalName(id: string): string {
    return this.animalNameCache.get(id) || id;
  }

  getHeatmapColor(value: number): string {
    if (value === 0) return '#f5f5f5';
    const intensity = Math.min(value / Math.max(this.heatmapMaxValue, 1), 1);
    const r = Math.round(66 + (255 - 66) * (1 - intensity));
    const g = Math.round(165 + (255 - 165) * (1 - intensity));
    const b = Math.round(245);
    return `rgb(${r}, ${g}, ${b})`;
  }

  formatDateLabel(dateStr: string): string {
    if (!dateStr) return '';
    const parts = dateStr.split('-');
    if (parts.length === 3) {
      return `${parts[2]}/${parts[1]}`;
    }
    return dateStr;
  }

  // ══════════════════════════════════════════════════════════
  //  REPORT DOWNLOADS
  // ══════════════════════════════════════════════════════════
  onTabChange() {
    setTimeout(() => {
      window.dispatchEvent(new Event('resize'));
    }, 50);
  }
  downloadReportPdf(): void {
    this.downloadingPdf = true;
    const section = this.selectedReportSection;
    const period = this.selectedReportPeriod;

    this.analyticsService
      .downloadReportPdf(period, section)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob) => {
          this.triggerDownload(
            blob,
            `${section}-${period}-report-${this.todayStr()}.pdf`,
          );
          this.downloadingPdf = false;
          const sectionLabel =
            this.reportSectionOptions.find((o) => o.value === section)?.label ||
            section;
          const periodLabel = period === 'annual' ? 'annual' : 'monthly';
          this.showSuccess(`${sectionLabel} ${periodLabel} report downloaded`);
        },
        error: () => {
          this.showError('Failed to download report');
          this.downloadingPdf = false;
        },
      });
  }

  exportCsv(type: 'activities' | 'feedings' | 'measurements'): void {
    const methods = {
      activities: () => this.analyticsService.exportActivitiesCsv(),
      feedings: () => this.analyticsService.exportFeedingsCsv(),
      measurements: () => this.analyticsService.exportMeasurementsCsv(),
    };
    const stateKeys = {
      activities: 'downloadingActivitiesCsv',
      feedings: 'downloadingFeedingsCsv',
      measurements: 'downloadingMeasurementsCsv',
    } as const;

    (this as any)[stateKeys[type]] = true;

    methods[type]()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob) => {
          this.triggerDownload(blob, `${type}-export-${this.todayStr()}.csv`);
          (this as any)[stateKeys[type]] = false;
          this.showSuccess(`${this.capitalize(type)} CSV exported`);
        },
        error: () => {
          this.showError(`Failed to export ${type} CSV`);
          (this as any)[stateKeys[type]] = false;
        },
      });
  }

  exportExcel(type: 'activities' | 'feedings' | 'measurements'): void {
    const methods = {
      activities: () => this.analyticsService.exportActivitiesExcel(),
      feedings: () => this.analyticsService.exportFeedingsExcel(),
      measurements: () => this.analyticsService.exportMeasurementsExcel(),
    };
    const stateKeys = {
      activities: 'downloadingActivitiesExcel',
      feedings: 'downloadingFeedingsExcel',
      measurements: 'downloadingMeasurementsExcel',
    } as const;

    (this as any)[stateKeys[type]] = true;

    methods[type]()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob) => {
          this.triggerDownload(blob, `${type}-export-${this.todayStr()}.xlsx`);
          (this as any)[stateKeys[type]] = false;
          this.showSuccess(`${this.capitalize(type)} Excel exported`);
        },
        error: () => {
          this.showError(`Failed to export ${type} Excel`);
          (this as any)[stateKeys[type]] = false;
        },
      });
  }

  private triggerDownload(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  }

  private todayStr(): string {
    const d = new Date();
    return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`;
  }

  private capitalize(s: string): string {
    return s.charAt(0).toUpperCase() + s.slice(1);
  }

  private showSuccess(detail: string): void {
    this.messageService.add({
      severity: 'success',
      summary: 'Success',
      detail,
      life: 3000,
    });
  }

  private showError(detail: string): void {
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail,
      life: 5000,
    });
  }
}
