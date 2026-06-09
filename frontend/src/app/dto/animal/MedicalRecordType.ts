export type MedicalRecordType = 'Vaccine' | 'Disease' | 'Treatment' | 'Diagnosis' | 'CheckUp';

export const MEDICAL_RECORD_TYPE_OPTIONS: { label: string; value: MedicalRecordType }[] = [
  { label: 'Vaccine', value: 'Vaccine' },
  { label: 'Disease', value: 'Disease' },
  { label: 'Treatment', value: 'Treatment' },
  { label: 'Diagnosis', value: 'Diagnosis' },
  { label: 'Check up', value: 'CheckUp' }
];

export const getMedicalRecordTypeSeverity = (type: MedicalRecordType): string => {
  switch (type) {
    case 'Vaccine': return 'success';
    case 'Disease': return 'danger';
    case 'Treatment': return 'info';
    case 'Diagnosis': return 'warn';
    case 'CheckUp': return 'info';
    default: return 'info';
  }
};
