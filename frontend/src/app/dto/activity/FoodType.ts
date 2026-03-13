// Food type options for dropdown (string-based, not enum)
export const FOOD_TYPE_OPTIONS: { label: string; value: string }[] = [
  { label: 'Dry Food', value: 'dry_food' },
  { label: 'Wet Food', value: 'wet_food' },
  { label: 'Raw Food', value: 'raw_food' },
  { label: 'Vegetables', value: 'vegetables' },
  { label: 'Fruits', value: 'fruits' },
  { label: 'Treats', value: 'treats' },
  { label: 'Supplements', value: 'supplements' },
  { label: 'Seeds', value: 'seeds' },
  { label: 'Hay', value: 'hay' },
  { label: 'Pellets', value: 'pellets' }
];

export const getFoodTypeLabel = (value: string): string => {
  const option = FOOD_TYPE_OPTIONS.find(o => o.value === value);
  return option ? option.label : value;
};

export const getFoodTypeSeverity = (type: string): string => {
  switch (type) {
    case 'dry_food': return 'info';
    case 'wet_food': return 'success';
    case 'raw_food': return 'warn';
    case 'vegetables': return 'success';
    case 'fruits': return 'warn';
    case 'treats': return 'contrast';
    case 'supplements': return 'secondary';
    case 'seeds': return 'info';
    case 'hay': return 'warn';
    case 'pellets': return 'info';
    default: return 'info';
  }
};
