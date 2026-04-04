import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import {
  DailyMeasurementResponse,
  ActivityRecordResponse,
  FeedingRecordResponse,
  MessageResponse
} from '../../dto/activity';

@Injectable({
  providedIn: 'root'
})
export class ActivityService {

  private readonly measurementsUrl = `${environment.apiGatewayUrl}/measurements`;
  private readonly activitiesUrl = `${environment.apiGatewayUrl}/activities`;
  private readonly feedingUrl = `${environment.apiGatewayUrl}/feeding`;

  constructor(private http: HttpClient) {}

  // ── Measurements ───────────────────────────────────────────

  saveMeasurement(data: {
    animal_id: string;
    date: string;
    weight_grams: number | null;
    temperature_celsius: number | null;
    energy_level: number | null;
    mood_level: number | null;
  }): Observable<DailyMeasurementResponse> {
    return this.http.post<DailyMeasurementResponse>(this.measurementsUrl, data);
  }

  getMeasurement(animalId: string, date: string): Observable<DailyMeasurementResponse | null> {
    const params = new HttpParams().set('date', date);
    return this.http.get<DailyMeasurementResponse>(
      `${this.measurementsUrl}/animal/${animalId}`, { params }
    ).pipe(
      catchError(() => of(null))  // 204 No Content or 404 → null
    );
  }

  // ── Activities ─────────────────────────────────────────────

  addActivity(data: {
    animal_id: string;
    activity_type: string;
    duration_minutes: number;
    notes: string | null;
    recorded_at: string;  // ISO datetime
  }): Observable<ActivityRecordResponse> {
    return this.http.post<ActivityRecordResponse>(this.activitiesUrl, data);
  }

  getActivities(animalId: string, date: string): Observable<ActivityRecordResponse[]> {
    const params = new HttpParams().set('date', date);
    return this.http.get<ActivityRecordResponse[]>(
      `${this.activitiesUrl}/animal/${animalId}`, { params }
    ).pipe(
      catchError(() => of([]))
    );
  }

  deleteActivity(id: string): Observable<MessageResponse> {
    return this.http.delete<MessageResponse>(`${this.activitiesUrl}/${id}`);
  }

  // ── Feedings ───────────────────────────────────────────────

  addFeeding(data: {
    animal_id: string;
    food_type: string;
    quantity_grams: number;
    meal_time: string;  // ISO datetime
    notes: string | null;
  }): Observable<FeedingRecordResponse> {
    return this.http.post<FeedingRecordResponse>(this.feedingUrl, data);
  }

  getFeedings(animalId: string, date: string): Observable<FeedingRecordResponse[]> {
    const params = new HttpParams().set('date', date);
    return this.http.get<FeedingRecordResponse[]>(
      `${this.feedingUrl}/animal/${animalId}`, { params }
    ).pipe(
      catchError(() => of([]))
    );
  }

  deleteFeeding(id: string): Observable<MessageResponse> {
    return this.http.delete<MessageResponse>(`${this.feedingUrl}/${id}`);
  }
}
