// ============================================================
// Dummy Medical Records (MongoDB)
// Database: animal_registry, Collection: medical_records
// Run: mongosh animal_registry 04-medical-records.js
// ============================================================

const NIKOLA_ID  = "a1b2c3d4-0003-4000-8000-000000000003";

const now = new Date();
const daysAgo = (d) => new Date(now.getTime() - d * 86400000);
const dateOnly = (d) => {
  const dt = new Date(now.getTime() - d * 86400000);
  const str = dt.toISOString().split('T')[0];
  return new Date(str + 'T00:00:00Z');
};

// Animal IDs from 02-animals.js
const BORIS    = "66a000000000000000000001";
const BELLA    = "66a000000000000000000002";
const NUTTY    = "66a000000000000000000003";
const NALA     = "66a000000000000000000004";
const CARLOS   = "66a000000000000000000005";
const COCO     = "66a000000000000000000006";
const PEANUT   = "66a000000000000000000007";
const REX      = "66a00000000000000000000b";
const LUNA     = "66a00000000000000000000c";
const MAX      = "66a00000000000000000000d";
const ROCKY    = "66a00000000000000000000f";
const MIKA     = "66a000000000000000000010";
const SHADOW   = "66a000000000000000000011";
const GINGER   = "66a000000000000000000012";
const WHISKERS = "66a000000000000000000013";

db.medical_records.deleteMany({});

const records = [
  // Boris - Vaccine
  {
    animalId: BORIS,
    type: "Vaccine",
    title: "Rabies Vaccination",
    description: "Annual rabies vaccination administered.",
    date: dateOnly(50),
    veterinarianId: NIKOLA_ID,
    veterinarianName: "Dr. Nikola Ilic",
    notes: "No adverse reactions observed. Next dose due in 12 months.",
    createdAt: daysAgo(50)
  },

  // Bella - Diagnosis + Treatment
  {
    animalId: BELLA,
    type: "Diagnosis",
    title: "General Health Checkup",
    description: "Complete physical examination upon admission.",
    date: dateOnly(55),
    veterinarianId: NIKOLA_ID,
    veterinarianName: "Dr. Nikola Ilic",
    notes: "Overall healthy. Minor dental wear, typical for age.",
    createdAt: daysAgo(55)
  },

  // Nala - Disease + Treatment (skin condition)
  {
    animalId: NALA,
    type: "Disease",
    title: "Dermatitis Diagnosis",
    description: "Fungal skin infection identified on dorsal area.",
    date: dateOnly(10),
    veterinarianId: NIKOLA_ID,
    veterinarianName: "Dr. Nikola Ilic",
    notes: "Skin scraping sent for culture. Preliminary diagnosis: dermatophytosis.",
    createdAt: daysAgo(10)
  },
  {
    animalId: NALA,
    type: "Treatment",
    title: "Antifungal Treatment Started",
    description: "Topical antifungal medication applied. Oral itraconazole prescribed.",
    date: dateOnly(9),
    veterinarianId: NIKOLA_ID,
    veterinarianName: "Dr. Nikola Ilic",
    notes: "Apply cream twice daily. Oral medication for 4 weeks. Recheck in 2 weeks.",
    createdAt: daysAgo(9)
  },

  // Carlos - Vaccine + checkup
  {
    animalId: CARLOS,
    type: "Vaccine",
    title: "Leptospirosis Vaccination",
    description: "Vaccination against leptospirosis for semi-aquatic rodent.",
    date: dateOnly(80),
    veterinarianId: NIKOLA_ID,
    veterinarianName: "Dr. Nikola Ilic",
    notes: "Capybara-specific dosage administered. Monitoring for 48h.",
    createdAt: daysAgo(80)
  },

  // Peanut - Quarantine checkup
  {
    animalId: PEANUT,
    type: "Diagnosis",
    title: "Quarantine Admission Exam",
    description: "Full physical exam on quarantine admission.",
    date: dateOnly(7),
    veterinarianId: NIKOLA_ID,
    veterinarianName: "Dr. Nikola Ilic",
    notes: "Slightly underweight but otherwise healthy. Fecal test pending. Continue quarantine for 14 days.",
    createdAt: daysAgo(7)
  },

  // Rex - Vaccine
  {
    animalId: REX,
    type: "Vaccine",
    title: "DHPP Vaccination",
    description: "Distemper, Hepatitis, Parainfluenza, Parvovirus combination vaccine.",
    date: dateOnly(40),
    veterinarianId: NIKOLA_ID,
    veterinarianName: "Dr. Nikola Ilic",
    notes: "Booster dose. Previous vaccination history unknown - started full series.",
    createdAt: daysAgo(40)
  },

  // Luna - Vaccine + Diagnosis
  {
    animalId: LUNA,
    type: "Vaccine",
    title: "Rabies Vaccination",
    description: "Standard rabies vaccine.",
    date: dateOnly(30),
    veterinarianId: NIKOLA_ID,
    veterinarianName: "Dr. Nikola Ilic",
    notes: "First vaccination at shelter. Tag #LR-2025-031.",
    createdAt: daysAgo(30)
  },

  // Max - Disease + Treatment (ear infection)
  {
    animalId: MAX,
    type: "Disease",
    title: "Bilateral Otitis Externa",
    description: "Ear infection in both ears, bacterial origin suspected.",
    date: dateOnly(5),
    veterinarianId: NIKOLA_ID,
    veterinarianName: "Dr. Nikola Ilic",
    notes: "Ear swab taken for culture. Significant inflammation and discharge.",
    createdAt: daysAgo(5)
  },
  {
    animalId: MAX,
    type: "Treatment",
    title: "Ear Infection Treatment",
    description: "Ear cleaning and antibiotic ear drops prescribed. Oral antibiotics started.",
    date: dateOnly(4),
    veterinarianId: NIKOLA_ID,
    veterinarianName: "Dr. Nikola Ilic",
    notes: "Clean ears twice daily before applying drops. Oral amoxicillin for 10 days. Follow-up in 7 days.",
    createdAt: daysAgo(4)
  },

  // Rocky - Diagnosis
  {
    animalId: ROCKY,
    type: "Diagnosis",
    title: "Admission Health Assessment",
    description: "Complete physical and blood panel on transfer admission.",
    date: dateOnly(38),
    veterinarianId: NIKOLA_ID,
    veterinarianName: "Dr. Nikola Ilic",
    notes: "All blood values normal. Good body condition. Microchip verified.",
    createdAt: daysAgo(38)
  },

  // Mika - Treatment (dental)
  {
    animalId: MIKA,
    type: "Treatment",
    title: "Dental Cleaning",
    description: "Professional dental cleaning under sedation.",
    date: dateOnly(20),
    veterinarianId: NIKOLA_ID,
    veterinarianName: "Dr. Nikola Ilic",
    notes: "Mild tartar buildup removed. No extractions needed. Recovered well from sedation.",
    createdAt: daysAgo(20)
  },

  // Ginger - Quarantine diagnosis
  {
    animalId: GINGER,
    type: "Diagnosis",
    title: "Quarantine Health Screen",
    description: "Initial health screening for new cat arrival.",
    date: dateOnly(5),
    veterinarianId: NIKOLA_ID,
    veterinarianName: "Dr. Nikola Ilic",
    notes: "FIV/FeLV test negative. Mild upper respiratory symptoms - monitoring. Quarantine for 14 days.",
    createdAt: daysAgo(5)
  },

  // Shadow - Vaccine
  {
    animalId: SHADOW,
    type: "Vaccine",
    title: "Rabies Vaccination",
    description: "Standard rabies vaccine for domestic cat.",
    date: dateOnly(25),
    veterinarianId: NIKOLA_ID,
    veterinarianName: "Dr. Nikola Ilic",
    notes: "First rabies shot. Tag #DSH-2025-011. No adverse reactions.",
    createdAt: daysAgo(25)
  },

  // Whiskers - Vaccine
  {
    animalId: WHISKERS,
    type: "Vaccine",
    title: "FVRCP Vaccination",
    description: "Feline viral rhinotracheitis, calicivirus, panleukopenia vaccine.",
    date: dateOnly(30),
    veterinarianId: NIKOLA_ID,
    veterinarianName: "Dr. Nikola Ilic",
    notes: "First dose at shelter. Booster scheduled in 3 weeks.",
    createdAt: daysAgo(30)
  }
];

db.medical_records.insertMany(records);
print("Inserted " + records.length + " medical records");
