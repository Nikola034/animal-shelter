import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  PopulationOverview,
  CategoryCount,
  StatusCount,
  GenderCount,
  AgeGroupCount,
  WeightTrendResponse,
  EnergyMoodTrendResponse,
  WeightDataPoint,
  ActivityTypeStats,
  DailySummary,
  HeatmapCell,
  TopAnimalActivity,
  FoodTypeStats,
  DashboardResponse
} from '../../dto/analytics/AnalyticsResponse';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {

  private readonly populationUrl = `${environment.apiGatewayUrl}/analytics/population`;
  private readonly healthUrl = `${environment.apiGatewayUrl}/analytics/health`;
  private readonly activitiesUrl = `${environment.apiGatewayUrl}/analytics/activities`;
  private readonly feedingUrl = `${environment.apiGatewayUrl}/analytics/feeding`;
  private readonly reportsUrl = `${environment.apiGatewayUrl}/analytics/reports`;

  constructor(private http: HttpClient) {}

  // ── Population Analytics ──────────────────────────────────

  getPopulationOverview(): Observable<PopulationOverview> {
    return this.http.get<PopulationOverview>(`${this.populationUrl}/overview`);
  }

  getAnimalsByCategory(): Observable<CategoryCount[]> {
    return this.http.get<CategoryCount[]>(`${this.populationUrl}/by-category`);
  }

  getAnimalsByStatus(): Observable<StatusCount[]> {
    return this.http.get<StatusCount[]>(`${this.populationUrl}/by-status`);
  }

  getAnimalsByGender(): Observable<GenderCount[]> {
    return this.http.get<GenderCount[]>(`${this.populationUrl}/by-gender`);
  }

  getAgeDistribution(): Observable<AgeGroupCount[]> {
    return this.http.get<AgeGroupCount[]>(`${this.populationUrl}/age-distribution`);
  }

  // ── Health Analytics ──────────────────────────────────────

  getWeightTrend(animalId: string, days: number = 30): Observable<WeightTrendResponse> {
    const params = new HttpParams()
      .set('animalId', animalId)
      .set('days', days.toString());
    return this.http.get<WeightTrendResponse>(`${this.healthUrl}/weight-trend`, { params });
  }

  getEnergyMoodTrend(animalId: string, days: number = 30): Observable<EnergyMoodTrendResponse> {
    const params = new HttpParams()
      .set('animalId', animalId)
      .set('days', days.toString());
    return this.http.get<EnergyMoodTrendResponse>(`${this.healthUrl}/energy-mood-trend`, { params });
  }

  getAverageWeight(days: number = 30): Observable<WeightDataPoint[]> {
    const params = new HttpParams().set('days', days.toString());
    return this.http.get<WeightDataPoint[]>(`${this.healthUrl}/average-weight`, { params });
  }

  // ── Activity Analytics ────────────────────────────────────

  getActivitiesByType(days: number = 30): Observable<ActivityTypeStats[]> {
    const params = new HttpParams().set('days', days.toString());
    return this.http.get<ActivityTypeStats[]>(`${this.activitiesUrl}/by-type`, { params });
  }

  getActivityDailySummary(days: number = 30): Observable<DailySummary[]> {
    const params = new HttpParams().set('days', days.toString());
    return this.http.get<DailySummary[]>(`${this.activitiesUrl}/daily-summary`, { params });
  }

  getActivityHeatmap(days: number = 30): Observable<HeatmapCell[]> {
    const params = new HttpParams().set('days', days.toString());
    return this.http.get<HeatmapCell[]>(`${this.activitiesUrl}/heatmap`, { params });
  }

  getTopAnimals(days: number = 30, limit: number = 10): Observable<TopAnimalActivity[]> {
    const params = new HttpParams()
      .set('days', days.toString())
      .set('limit', limit.toString());
    return this.http.get<TopAnimalActivity[]>(`${this.activitiesUrl}/top-animals`, { params });
  }

  // ── Feeding Analytics ─────────────────────────────────────

  getFeedingsByType(days: number = 30): Observable<FoodTypeStats[]> {
    const params = new HttpParams().set('days', days.toString());
    return this.http.get<FoodTypeStats[]>(`${this.feedingUrl}/by-type`, { params });
  }

  getFeedingDailySummary(days: number = 30): Observable<DailySummary[]> {
    const params = new HttpParams().set('days', days.toString());
    return this.http.get<DailySummary[]>(`${this.feedingUrl}/daily-summary`, { params });
  }

  // ── Dashboard / Reports ───────────────────────────────────

  getDashboard(): Observable<DashboardResponse> {
    return this.http.get<DashboardResponse>(`${this.reportsUrl}/dashboard`);
  }

  // ── Report Downloads (PDF) ──────────────────────────────────

  private readonly reportBaseUrl = `${environment.apiGatewayUrl}/reports`;

  downloadReportPdf(period: string = 'monthly', section: string = 'all'): Observable<Blob> {
    const params = new HttpParams()
      .set('period', period)
      .set('section', section);
    return this.http.get(`${this.reportBaseUrl}/pdf`, {
      params,
      responseType: 'blob'
    });
  }

  // ── Data Exports (CSV) ─────────────────────────────────────

  exportActivitiesCsv(): Observable<Blob> {
    return this.http.get(`${this.reportBaseUrl}/export/activities/csv`, {
      responseType: 'blob'
    });
  }

  exportFeedingsCsv(): Observable<Blob> {
    return this.http.get(`${this.reportBaseUrl}/export/feedings/csv`, {
      responseType: 'blob'
    });
  }

  exportMeasurementsCsv(): Observable<Blob> {
    return this.http.get(`${this.reportBaseUrl}/export/measurements/csv`, {
      responseType: 'blob'
    });
  }

  // ── Data Exports (Excel) ───────────────────────────────────

  exportActivitiesExcel(): Observable<Blob> {
    return this.http.get(`${this.reportBaseUrl}/export/activities/excel`, {
      responseType: 'blob'
    });
  }

  exportFeedingsExcel(): Observable<Blob> {
    return this.http.get(`${this.reportBaseUrl}/export/feedings/excel`, {
      responseType: 'blob'
    });
  }

  exportMeasurementsExcel(): Observable<Blob> {
    return this.http.get(`${this.reportBaseUrl}/export/measurements/excel`, {
      responseType: 'blob'
    });
  }
}
