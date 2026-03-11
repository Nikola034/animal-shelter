import { AnimalCategory } from './AnimalCategory';
import { Gender } from './Gender';

export interface CreateAnimalRequest {
  name: string;
  category: AnimalCategory;
  breed?: string;
  gender: Gender;
  age_months?: number;
  weight?: number;
  color?: string;
  chip_id?: string;
  description?: string;
}
