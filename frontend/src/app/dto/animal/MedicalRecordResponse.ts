import { MedicalRecordType } from './MedicalRecordType';

export interface MedicalRecordResponse {
  id: string;
  animal_id: string;
  type: MedicalRecordType;
  title: string;
  description: string | null;
  date: string;
  veterinarian_id: string;
  veterinarian_name: string;
  notes: string | null;
  created_at: string;
}

export interface MedicalRecordListResponse {
  success: boolean;
  records: MedicalRecordResponse[];
  total: number;
}
