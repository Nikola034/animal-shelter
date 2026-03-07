export type AnimalStatus = 'Active' | 'Adopted' | 'Quarantine' | 'MedicalCare' | 'Deceased';

export const ANIMAL_STATUS_OPTIONS: { label: string; value: AnimalStatus }[] = [
  { label: 'Active', value: 'Active' },
  { label: 'Adopted', value: 'Adopted' },
  { label: 'Quarantine', value: 'Quarantine' },
  { label: 'Medical Care', value: 'MedicalCare' },
  { label: 'Deceased', value: 'Deceased' }
];

export const getAnimalStatusSeverity = (status: AnimalStatus): string => {
  switch (status) {
    case 'Active': return 'success';
    case 'Adopted': return 'info';
    case 'Quarantine': return 'warn';
    case 'MedicalCare': return 'danger';
    case 'Deceased': return 'secondary';
    default: return 'info';
  }
};
