export type AnimalCategory = 'Dog' | 'Cat' | 'Bird' | 'Reptile' | 'Rodent' | 'Rabbit' | 'Exotic';

export const ANIMAL_CATEGORY_OPTIONS: { label: string; value: AnimalCategory }[] = [
  { label: 'Dog', value: 'Dog' },
  { label: 'Cat', value: 'Cat' },
  { label: 'Bird', value: 'Bird' },
  { label: 'Reptile', value: 'Reptile' },
  { label: 'Rodent', value: 'Rodent' },
  { label: 'Rabbit', value: 'Rabbit' },
  { label: 'Exotic', value: 'Exotic' }
];

export const getCategorySeverity = (category: AnimalCategory): string => {
  switch (category) {
    case 'Dog': return 'info';
    case 'Cat': return 'success';
    case 'Bird': return 'warn';
    case 'Reptile': return 'danger';
    case 'Rodent': return 'secondary';
    case 'Rabbit': return 'info';
    case 'Exotic': return 'contrast';
    default: return 'info';
  }
};
