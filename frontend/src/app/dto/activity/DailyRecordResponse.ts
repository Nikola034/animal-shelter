// ── Daily Measurement (separate collection) ──────────────
export interface DailyMeasurementResponse {
  id: string;
  animal_id: string;
  date: string;
  weight_grams: number | null;
  temperature_celsius: number | null;
  energy_level: number | null;
  mood_level: number | null;
  created_by: string;
  created_by_name: string;
  updated_at: string;
}

// ── Activity Record (separate collection) ─────────────────
export interface ActivityRecordResponse {
  id: string;
  animal_id: string;
  activity_type: string;
  duration_minutes: number;
  notes: string | null;
  recorded_at: string;
  recorded_by: string;
  recorded_by_name: string;
  created_at: string;
}

// ── Feeding Record (separate collection) ──────────────────
export interface FeedingRecordResponse {
  id: string;
  animal_id: string;
  food_type: string;
  quantity_grams: number;
  meal_time: string;
  notes: string | null;
  recorded_by: string;
  recorded_by_name: string;
  created_at: string;
}

// ── Generic message response ──────────────────────────────
export interface MessageResponse {
  success: boolean;
  message: string;
}
