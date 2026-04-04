// ── Population Analytics ────────────────────────────────────

export interface CategoryCount {
  category: string;
  count: number;
}

export interface StatusCount {
  status: string;
  count: number;
}

export interface GenderCount {
  gender: string;
  count: number;
}

export interface AgeGroupCount {
  range: string;
  count: number;
}

export interface PopulationOverview {
  total_animals: number;
  by_category: CategoryCount[];
  by_status: StatusCount[];
  by_gender: GenderCount[];
  age_distribution: AgeGroupCount[];
}

// ── Health Analytics ────────────────────────────────────────

export interface WeightDataPoint {
  date: string;
  weight_grams: number;
}

export interface WeightTrendResponse {
  animal_id: string;
  data: WeightDataPoint[];
}

export interface EnergyMoodDataPoint {
  date: string;
  energy_level: number;
  mood_level: number;
}

export interface EnergyMoodTrendResponse {
  animal_id: string;
  data: EnergyMoodDataPoint[];
}

// ── Activity Analytics ──────────────────────────────────────

export interface ActivityTypeStats {
  activity_type: string;
  total_minutes: number;
  count: number;
}

export interface DailySummary {
  date: string;
  total_value: number;
  count: number;
}

export interface HeatmapCell {
  day_of_week: number;
  hour: number;
  count: number;
}

export interface TopAnimalActivity {
  animal_id: string;
  total_minutes: number;
  activity_count: number;
}

// ── Feeding Analytics ───────────────────────────────────────

export interface FoodTypeStats {
  food_type: string;
  total_grams: number;
  count: number;
}

// ── Dashboard / Reports ─────────────────────────────────────

export interface DashboardResponse {
  total_animals: number;
  active_animals: number;
  adopted_animals: number;
  total_activities_last30_days: number;
  total_activity_minutes_last30_days: number;
  total_feedings_last30_days: number;
  total_food_grams_last30_days: number;
  animals_by_category: CategoryCount[];
  top_activity_types: ActivityTypeStats[];
  top_food_types: FoodTypeStats[];
}
