import { AnimalCategory } from './AnimalCategory';
import { AnimalStatus } from './AnimalStatus';
import { Gender } from './Gender';

export interface StatusHistoryEntry {
  status: AnimalStatus;
  changed_at: string;
  changed_by: string;
  changed_by_username: string;
  note: string | null;
}

export interface AnimalResponse {
  id: string;
  name: string;
  category: AnimalCategory;
  breed: string | null;
  gender: Gender;
  age_months: number | null;
  weight: number | null;
  color: string | null;
  chip_id: string | null;
  status: AnimalStatus;
  description: string | null;
  image_paths: string[];
  status_history: StatusHistoryEntry[];
  registered_by: string;
  registered_by_username: string;
  created_at: string;
  updated_at: string;
}

export interface AnimalListResponse {
  success: boolean;
  animals: AnimalResponse[];
  total: number;
}

export interface ImageUploadResponse {
  success: boolean;
  image_paths: string[];
  message: string;
}

export interface MessageResponse {
  success: boolean;
  message: string;
}
