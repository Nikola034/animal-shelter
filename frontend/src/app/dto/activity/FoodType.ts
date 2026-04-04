// Food type enum values matching backend FoodType enum
export const FOOD_TYPE_OPTIONS: { label: string; value: string }[] = [
  { label: 'Dry Food', value: 'DRY_FOOD' },
  { label: 'Wet Food', value: 'WET_FOOD' },
  { label: 'Raw Food', value: 'RAW_FOOD' },
  { label: 'Vegetables', value: 'VEGETABLES' },
  { label: 'Fruits', value: 'FRUITS' },
  { label: 'Treats', value: 'TREATS' },
  { label: 'Supplements', value: 'SUPPLEMENTS' },
  { label: 'Seeds', value: 'SEEDS' },
  { label: 'Hay', value: 'HAY' },
  { label: 'Pellets', value: 'PELLETS' }
];

export const getFoodTypeLabel = (value: string): string => {
  const option = FOOD_TYPE_OPTIONS.find(o => o.value === value);
  return option ? option.label : value;
};

export const getFoodTypeSeverity = (type: string): string => {
  switch (type) {
    case 'DRY_FOOD': return 'info';
    case 'WET_FOOD': return 'success';
    case 'RAW_FOOD': return 'warn';
    case 'VEGETABLES': return 'success';
    case 'FRUITS': return 'warn';
    case 'TREATS': return 'contrast';
    case 'SUPPLEMENTS': return 'secondary';
    case 'SEEDS': return 'info';
    case 'HAY': return 'warn';
    case 'PELLETS': return 'info';
    default: return 'info';
  }
};
