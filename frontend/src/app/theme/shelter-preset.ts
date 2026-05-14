import { definePreset } from '@primeng/themes';
import Aura from '@primeng/themes/aura';

/**
 * Shelter visual identity:
 * - Tailwind red as the primary palette (warm, professional, easy on white).
 * - Sharp corners (handled via global CSS) and a flat Material-ish feel.
 * - White surfaces with red accents for actions, links and selection.
 */
export const ShelterPreset = definePreset(Aura, {
  semantic: {
    primary: {
      50:  '#fef2f2',
      100: '#fee2e2',
      200: '#fecaca',
      300: '#fca5a5',
      400: '#f87171',
      500: '#ef4444',
      600: '#dc2626',
      700: '#b91c1c',
      800: '#991b1b',
      900: '#7f1d1d',
      950: '#450a0a',
    },
    colorScheme: {
      light: {
        primary: {
          color: '{primary.600}',
          contrastColor: '#ffffff',
          hoverColor: '{primary.700}',
          activeColor: '{primary.800}',
        },
        highlight: {
          background: '{primary.50}',
          focusBackground: '{primary.100}',
          color: '{primary.700}',
          focusColor: '{primary.800}',
        },
        formField: {
          // Slightly tighter borders so inputs feel crisp on white.
          borderColor: '{surface.300}',
          hoverBorderColor: '{primary.400}',
          focusBorderColor: '{primary.500}',
        },
      },
      dark: {
        primary: {
          color: '{primary.400}',
          contrastColor: '{surface.950}',
          hoverColor: '{primary.300}',
          activeColor: '{primary.200}',
        },
        highlight: {
          background: 'color-mix(in srgb, {primary.400}, transparent 84%)',
          focusBackground: 'color-mix(in srgb, {primary.400}, transparent 76%)',
          color: '{primary.300}',
          focusColor: '{primary.200}',
        },
      },
    },
  },
});
