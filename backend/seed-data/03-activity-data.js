// ============================================================
// Dummy Activity Tracking Data (MongoDB)
// Database: activity_tracking
// Collections: feedings, activities, daily_measurements
// Run: mongosh activity_tracking 03-activity-data.js
// ============================================================

const JELENA_ID  = "a1b2c3d4-0002-4000-8000-000000000002";
const NIKOLA_ID  = "a1b2c3d4-0003-4000-8000-000000000003";

const now = new Date();
const daysAgo = (d, h = 8) => new Date(now.getTime() - d * 86400000 + h * 3600000);

// Animal IDs from 02-animals.js
const BORIS    = "66a000000000000000000001";
const BELLA    = "66a000000000000000000002";
const NUTTY    = "66a000000000000000000003";
const NALA     = "66a000000000000000000004";
const CARLOS   = "66a000000000000000000005";
const COCO     = "66a000000000000000000006";
const PEANUT   = "66a000000000000000000007";
const WILLOW   = "66a000000000000000000009";
const REX      = "66a00000000000000000000b";
const LUNA     = "66a00000000000000000000c";
const MAX      = "66a00000000000000000000d";
const ROCKY    = "66a00000000000000000000f";
const MIKA     = "66a000000000000000000010";
const SHADOW   = "66a000000000000000000011";
const WHISKERS = "66a000000000000000000013";
const CHESTNUT = "66a00000000000000000000a";

// ============================================================
//  30 FEEDING RECORDS
// ============================================================
db.feedings.deleteMany({});

const feedings = [
  // Boris (Beaver) - wood/plant diet
  { animal_id: BORIS, food_type: "VEGETABLES", quantity_grams: 500, meal_time: daysAgo(1, 7), notes: "Fresh willow branches and vegetables", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 7) },
  { animal_id: BORIS, food_type: "VEGETABLES", quantity_grams: 450, meal_time: daysAgo(1, 18), notes: "Evening feeding - carrots and bark", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 18) },
  { animal_id: BORIS, food_type: "VEGETABLES", quantity_grams: 520, meal_time: daysAgo(2, 7), notes: "Morning vegetables", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(2, 7) },

  // Bella (Beaver)
  { animal_id: BELLA, food_type: "VEGETABLES", quantity_grams: 400, meal_time: daysAgo(1, 7), notes: "Willow branches and root vegetables", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 7) },
  { animal_id: BELLA, food_type: "FRUITS", quantity_grams: 150, meal_time: daysAgo(1, 12), notes: "Apple slices as treat", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 12) },

  // Nutty (Nutria)
  { animal_id: NUTTY, food_type: "PELLETS", quantity_grams: 200, meal_time: daysAgo(1, 8), notes: "Rodent pellets", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 8) },
  { animal_id: NUTTY, food_type: "VEGETABLES", quantity_grams: 300, meal_time: daysAgo(1, 17), notes: "Mixed vegetables - lettuce, carrots", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 17) },
  { animal_id: NUTTY, food_type: "VEGETABLES", quantity_grams: 280, meal_time: daysAgo(2, 8), notes: "Morning greens", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(2, 8) },

  // Carlos (Capybara)
  { animal_id: CARLOS, food_type: "HAY", quantity_grams: 800, meal_time: daysAgo(1, 7), notes: "Timothy hay - large portion", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 7) },
  { animal_id: CARLOS, food_type: "VEGETABLES", quantity_grams: 600, meal_time: daysAgo(1, 12), notes: "Mixed veggies and watermelon", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 12) },
  { animal_id: CARLOS, food_type: "FRUITS", quantity_grams: 400, meal_time: daysAgo(1, 17), notes: "Watermelon and banana", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 17) },
  { animal_id: CARLOS, food_type: "HAY", quantity_grams: 750, meal_time: daysAgo(2, 7), notes: "Morning hay", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(2, 7) },

  // Coco (Capybara)
  { animal_id: COCO, food_type: "HAY", quantity_grams: 700, meal_time: daysAgo(1, 7), notes: "Timothy hay", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 7) },
  { animal_id: COCO, food_type: "VEGETABLES", quantity_grams: 500, meal_time: daysAgo(1, 17), notes: "Evening vegetables", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 17) },

  // Rex (German Shepherd)
  { animal_id: REX, food_type: "DRY_FOOD", quantity_grams: 350, meal_time: daysAgo(1, 7), notes: "Morning kibble", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 7) },
  { animal_id: REX, food_type: "WET_FOOD", quantity_grams: 200, meal_time: daysAgo(1, 17), notes: "Evening wet food with supplements", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 17) },
  { animal_id: REX, food_type: "DRY_FOOD", quantity_grams: 350, meal_time: daysAgo(2, 7), notes: "Morning kibble", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(2, 7) },
  { animal_id: REX, food_type: "TREATS", quantity_grams: 50, meal_time: daysAgo(2, 14), notes: "Training treats", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(2, 14) },

  // Luna (Labrador)
  { animal_id: LUNA, food_type: "DRY_FOOD", quantity_grams: 300, meal_time: daysAgo(1, 7), notes: "Morning meal", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 7) },
  { animal_id: LUNA, food_type: "DRY_FOOD", quantity_grams: 300, meal_time: daysAgo(1, 18), notes: "Evening meal", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 18) },

  // Max (Beagle - medical care)
  { animal_id: MAX, food_type: "WET_FOOD", quantity_grams: 180, meal_time: daysAgo(1, 8), notes: "Soft food due to treatment", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 8) },
  { animal_id: MAX, food_type: "SUPPLEMENTS", quantity_grams: 10, meal_time: daysAgo(1, 8), notes: "Antibiotics mixed with food", recorded_by: NIKOLA_ID, recorded_by_name: "dr_nikola", created_at: daysAgo(1, 8) },

  // Rocky (Husky)
  { animal_id: ROCKY, food_type: "DRY_FOOD", quantity_grams: 320, meal_time: daysAgo(1, 7), notes: "High-protein kibble", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 7) },
  { animal_id: ROCKY, food_type: "RAW_FOOD", quantity_grams: 250, meal_time: daysAgo(1, 17), notes: "Raw chicken and vegetables", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 17) },

  // Mika (Persian Cat)
  { animal_id: MIKA, food_type: "WET_FOOD", quantity_grams: 80, meal_time: daysAgo(1, 7), notes: "Premium wet food", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 7) },
  { animal_id: MIKA, food_type: "DRY_FOOD", quantity_grams: 50, meal_time: daysAgo(1, 18), notes: "Evening dry food", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 18) },

  // Shadow (DSH Cat)
  { animal_id: SHADOW, food_type: "WET_FOOD", quantity_grams: 70, meal_time: daysAgo(1, 7), notes: "Morning wet food", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 7) },
  { animal_id: SHADOW, food_type: "DRY_FOOD", quantity_grams: 40, meal_time: daysAgo(1, 18), notes: "Free-choice kibble", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 18) },

  // Whiskers (Siamese)
  { animal_id: WHISKERS, food_type: "WET_FOOD", quantity_grams: 75, meal_time: daysAgo(1, 7), notes: "Tuna flavour wet food", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 7) },
  { animal_id: WHISKERS, food_type: "TREATS", quantity_grams: 15, meal_time: daysAgo(1, 14), notes: "Cat treats for socialization", recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 14) }
];

db.feedings.insertMany(feedings);
print("Inserted " + feedings.length + " feeding records");

// ============================================================
//  30 ACTIVITY RECORDS
// ============================================================
db.activities.deleteMany({});

const activities = [
  // Boris (Beaver) - swimming, digging
  { animal_id: BORIS, activity_type: "SWIMMING", duration_minutes: 45, notes: "Pool time, very active", recorded_at: daysAgo(1, 9), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 9) },
  { animal_id: BORIS, activity_type: "DIGGING", duration_minutes: 30, notes: "Digging in enclosure substrate", recorded_at: daysAgo(1, 15), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 15) },
  { animal_id: BORIS, activity_type: "SWIMMING", duration_minutes: 50, notes: "Extended swim session", recorded_at: daysAgo(2, 10), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(2, 10) },

  // Bella (Beaver)
  { animal_id: BELLA, activity_type: "SWIMMING", duration_minutes: 40, notes: "Swimming with Boris", recorded_at: daysAgo(1, 9), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 9) },
  { animal_id: BELLA, activity_type: "GROOMING", duration_minutes: 20, notes: "Self-grooming observed", recorded_at: daysAgo(1, 14), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 14) },

  // Nutty (Nutria)
  { animal_id: NUTTY, activity_type: "SWIMMING", duration_minutes: 35, notes: "Pool exercise", recorded_at: daysAgo(1, 10), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 10) },
  { animal_id: NUTTY, activity_type: "SOCIAL_INTERACTION", duration_minutes: 25, notes: "Interaction with caretaker", recorded_at: daysAgo(2, 11), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(2, 11) },

  // Carlos (Capybara) - swimming, social
  { animal_id: CARLOS, activity_type: "SWIMMING", duration_minutes: 60, notes: "Long pool session - loves it", recorded_at: daysAgo(1, 9), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 9) },
  { animal_id: CARLOS, activity_type: "SOCIAL_INTERACTION", duration_minutes: 40, notes: "Group time with Coco and Chestnut", recorded_at: daysAgo(1, 14), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 14) },
  { animal_id: CARLOS, activity_type: "WALKING", duration_minutes: 30, notes: "Guided walk around facility", recorded_at: daysAgo(2, 9), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(2, 9) },

  // Coco (Capybara)
  { animal_id: COCO, activity_type: "SWIMMING", duration_minutes: 50, notes: "Pool time", recorded_at: daysAgo(1, 10), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 10) },
  { animal_id: COCO, activity_type: "GROOMING", duration_minutes: 15, notes: "Grooming session", recorded_at: daysAgo(2, 14), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(2, 14) },

  // Chestnut (Capybara)
  { animal_id: CHESTNUT, activity_type: "PLAYING", duration_minutes: 35, notes: "Playing with enrichment toys", recorded_at: daysAgo(1, 11), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 11) },
  { animal_id: CHESTNUT, activity_type: "SWIMMING", duration_minutes: 40, notes: "Swimming with group", recorded_at: daysAgo(2, 10), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(2, 10) },

  // Rex (German Shepherd) - walking, training
  { animal_id: REX, activity_type: "WALKING", duration_minutes: 45, notes: "Morning walk around park", recorded_at: daysAgo(1, 8), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 8) },
  { animal_id: REX, activity_type: "TRAINING", duration_minutes: 30, notes: "Obedience training - sit, stay, come", recorded_at: daysAgo(1, 14), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 14) },
  { animal_id: REX, activity_type: "RUNNING", duration_minutes: 20, notes: "Off-leash run in yard", recorded_at: daysAgo(2, 8), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(2, 8) },

  // Luna (Labrador)
  { animal_id: LUNA, activity_type: "WALKING", duration_minutes: 40, notes: "Walk with fetch breaks", recorded_at: daysAgo(1, 8), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 8) },
  { animal_id: LUNA, activity_type: "PLAYING", duration_minutes: 25, notes: "Fetch and tug-of-war", recorded_at: daysAgo(1, 16), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 16) },
  { animal_id: LUNA, activity_type: "SWIMMING", duration_minutes: 30, notes: "Pool time - great swimmer", recorded_at: daysAgo(2, 10), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(2, 10) },

  // Rocky (Husky)
  { animal_id: ROCKY, activity_type: "RUNNING", duration_minutes: 40, notes: "High-energy run", recorded_at: daysAgo(1, 7), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 7) },
  { animal_id: ROCKY, activity_type: "WALKING", duration_minutes: 50, notes: "Long walk", recorded_at: daysAgo(1, 16), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 16) },
  { animal_id: ROCKY, activity_type: "PLAYING", duration_minutes: 30, notes: "Playing with Luna", recorded_at: daysAgo(2, 9), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(2, 9) },

  // Mika (Persian)
  { animal_id: MIKA, activity_type: "PLAYING", duration_minutes: 15, notes: "Feather toy play", recorded_at: daysAgo(1, 10), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 10) },
  { animal_id: MIKA, activity_type: "GROOMING", duration_minutes: 20, notes: "Brushing session", recorded_at: daysAgo(1, 15), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 15) },

  // Shadow (Cat)
  { animal_id: SHADOW, activity_type: "CLIMBING", duration_minutes: 20, notes: "Cat tree climbing", recorded_at: daysAgo(1, 11), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 11) },
  { animal_id: SHADOW, activity_type: "PLAYING", duration_minutes: 25, notes: "Laser pointer play", recorded_at: daysAgo(2, 10), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(2, 10) },

  // Whiskers (Siamese)
  { animal_id: WHISKERS, activity_type: "SOCIAL_INTERACTION", duration_minutes: 30, notes: "Lap time and petting", recorded_at: daysAgo(1, 14), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 14) },
  { animal_id: WHISKERS, activity_type: "PLAYING", duration_minutes: 20, notes: "Interactive toy play", recorded_at: daysAgo(2, 11), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(2, 11) },

  // Willow (Young Beaver)
  { animal_id: WILLOW, activity_type: "SWIMMING", duration_minutes: 20, notes: "Learning to swim - supervised", recorded_at: daysAgo(1, 10), recorded_by: JELENA_ID, recorded_by_name: "jelena_care", created_at: daysAgo(1, 10) }
];

db.activities.insertMany(activities);
print("Inserted " + activities.length + " activity records");

// ============================================================
//  20 DAILY MEASUREMENTS
// ============================================================
db.daily_measurements.deleteMany({});

// Helper to create a date value compatible with Java LocalDate (stored as ISODate by Spring Data)
const dateOnly = (d) => {
  const dt = new Date(now.getTime() - d * 86400000);
  const str = dt.toISOString().split('T')[0];
  return new Date(str + 'T00:00:00Z');
};

const measurements = [
  // Boris (Beaver) - multiple days
  { animal_id: BORIS, date: dateOnly(1), weight_grams: 22500, temperature_celsius: 37.2, energy_level: 8, mood_level: 7, created_by: JELENA_ID, created_by_name: "jelena_care", updated_at: daysAgo(1, 8) },
  { animal_id: BORIS, date: dateOnly(2), weight_grams: 22450, temperature_celsius: 37.1, energy_level: 7, mood_level: 8, created_by: JELENA_ID, created_by_name: "jelena_care", updated_at: daysAgo(2, 8) },
  { animal_id: BORIS, date: dateOnly(3), weight_grams: 22400, temperature_celsius: 37.3, energy_level: 8, mood_level: 7, created_by: JELENA_ID, created_by_name: "jelena_care", updated_at: daysAgo(3, 8) },

  // Carlos (Capybara)
  { animal_id: CARLOS, date: dateOnly(1), weight_grams: 55000, temperature_celsius: 37.5, energy_level: 9, mood_level: 9, created_by: JELENA_ID, created_by_name: "jelena_care", updated_at: daysAgo(1, 8) },
  { animal_id: CARLOS, date: dateOnly(2), weight_grams: 54800, temperature_celsius: 37.4, energy_level: 8, mood_level: 9, created_by: JELENA_ID, created_by_name: "jelena_care", updated_at: daysAgo(2, 8) },

  // Coco (Capybara)
  { animal_id: COCO, date: dateOnly(1), weight_grams: 48000, temperature_celsius: 37.3, energy_level: 7, mood_level: 8, created_by: JELENA_ID, created_by_name: "jelena_care", updated_at: daysAgo(1, 9) },

  // Nutty (Nutria)
  { animal_id: NUTTY, date: dateOnly(1), weight_grams: 7500, temperature_celsius: 37.0, energy_level: 8, mood_level: 7, created_by: JELENA_ID, created_by_name: "jelena_care", updated_at: daysAgo(1, 8) },
  { animal_id: NUTTY, date: dateOnly(2), weight_grams: 7520, temperature_celsius: 37.1, energy_level: 7, mood_level: 7, created_by: JELENA_ID, created_by_name: "jelena_care", updated_at: daysAgo(2, 8) },

  // Nala (Nutria - medical care)
  { animal_id: NALA, date: dateOnly(1), weight_grams: 6000, temperature_celsius: 37.8, energy_level: 5, mood_level: 4, created_by: NIKOLA_ID, created_by_name: "dr_nikola", updated_at: daysAgo(1, 9) },
  { animal_id: NALA, date: dateOnly(2), weight_grams: 5950, temperature_celsius: 38.0, energy_level: 4, mood_level: 4, created_by: NIKOLA_ID, created_by_name: "dr_nikola", updated_at: daysAgo(2, 9) },

  // Rex (German Shepherd)
  { animal_id: REX, date: dateOnly(1), weight_grams: 34000, temperature_celsius: 38.5, energy_level: 9, mood_level: 8, created_by: JELENA_ID, created_by_name: "jelena_care", updated_at: daysAgo(1, 7) },
  { animal_id: REX, date: dateOnly(2), weight_grams: 34050, temperature_celsius: 38.4, energy_level: 8, mood_level: 9, created_by: JELENA_ID, created_by_name: "jelena_care", updated_at: daysAgo(2, 7) },

  // Luna (Labrador)
  { animal_id: LUNA, date: dateOnly(1), weight_grams: 28000, temperature_celsius: 38.3, energy_level: 9, mood_level: 9, created_by: JELENA_ID, created_by_name: "jelena_care", updated_at: daysAgo(1, 7) },

  // Max (Beagle - medical)
  { animal_id: MAX, date: dateOnly(1), weight_grams: 12500, temperature_celsius: 39.0, energy_level: 5, mood_level: 5, created_by: NIKOLA_ID, created_by_name: "dr_nikola", updated_at: daysAgo(1, 8) },
  { animal_id: MAX, date: dateOnly(2), weight_grams: 12450, temperature_celsius: 38.8, energy_level: 6, mood_level: 5, created_by: NIKOLA_ID, created_by_name: "dr_nikola", updated_at: daysAgo(2, 8) },

  // Rocky (Husky)
  { animal_id: ROCKY, date: dateOnly(1), weight_grams: 25000, temperature_celsius: 38.2, energy_level: 10, mood_level: 8, created_by: JELENA_ID, created_by_name: "jelena_care", updated_at: daysAgo(1, 7) },

  // Mika (Persian Cat)
  { animal_id: MIKA, date: dateOnly(1), weight_grams: 4500, temperature_celsius: 38.5, energy_level: 6, mood_level: 7, created_by: JELENA_ID, created_by_name: "jelena_care", updated_at: daysAgo(1, 9) },

  // Shadow (Cat)
  { animal_id: SHADOW, date: dateOnly(1), weight_grams: 3800, temperature_celsius: 38.6, energy_level: 8, mood_level: 8, created_by: JELENA_ID, created_by_name: "jelena_care", updated_at: daysAgo(1, 9) },

  // Whiskers (Siamese)
  { animal_id: WHISKERS, date: dateOnly(1), weight_grams: 4000, temperature_celsius: 38.4, energy_level: 7, mood_level: 8, created_by: JELENA_ID, created_by_name: "jelena_care", updated_at: daysAgo(1, 9) },

  // Chestnut (Capybara)
  { animal_id: CHESTNUT, date: dateOnly(1), weight_grams: 42000, temperature_celsius: 37.4, energy_level: 8, mood_level: 8, created_by: JELENA_ID, created_by_name: "jelena_care", updated_at: daysAgo(1, 8) }
];

db.daily_measurements.insertMany(measurements);
print("Inserted " + measurements.length + " daily measurements");
