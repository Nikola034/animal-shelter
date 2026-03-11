import { MedicalRecordType } from './MedicalRecordType';

export interface CreateMedicalRecordRequest {
  animal_id: string;
  type: MedicalRecordType;
  title: string;
  description?: string;
  date: string;
  notes?: string;
}
