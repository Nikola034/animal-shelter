# Animal Shelter Management System sa AI-podržanom pretragom

### Tema za diplomski rad

**Student**: Nikola Bandulaja
**Broj indeksa**: SV74/2022


## Opis problema

Animal Shelter Management System je mikroservisna aplikacija namenjena prihvatilištima za životinje (azili, centri za udomljavanje, zoološki vrtovi) koja omogućava centralizovano upravljanje životinjama i njihovim potrebama. Aplikacija integriše Large Language Model (LLM) i RAG (Retrieval-Augmented Generation) tehnologiju za inteligentnu pretragu životinja putem prirodnog jezika. Aplikacija rešava sledeće probleme:

- **Nedostatak centralizovane evidencije** - Prihvatilišta nemaju jedinstven sistem za praćenje svih životinja u objektu
- **Praćenje zdravlja i ponašanja** - Nema sistematskog načina za dnevno praćenje zdravlja, aktivnosti i raspoloženja životinja
- **Medicinska dokumentacija** - Nedostaje digitalna evidencija vakcina, tretmana i dijagnoza
- **Analitika i izveštavanje** - Nema uvida u trendove zdravlja populacije, potrošnju hrane i demografiju
- **Koordinacija osoblja** - Različiti timovi (upravnici, veterinari, volonteri) nemaju efikasan način za deljenje informacija
- **Neintuitivna pretraga** - Klasični filteri ne omogućavaju pretragu po opisnim karakteristikama životinja (npr. "miran pas pogodan za porodicu sa decom")

## Korisnici sistema

Sistem podržava sledeće tipove korisnika:

- **Volonter** - Read-only pristup, pregled osnovnih informacija o životinjama
- **Upravnik (Caretaker)** - Upravljanje životinjama, dnevno praćenje aktivnosti i ishrane, premeštanje životinja
- **Veterinar** - Upravljanje medicinskom evidencijom, tretmani, zdravstvene analitike
- **Admin** - Puna kontrola sistema, upravljanje korisnicima, pregled svih izveštaja

## Predloženo rešenje

Sistem je dizajniran kao mikroservisna aplikacija koja se sastoji od 5 glavnih komponenti (4 mikroservisa + API Gateway), svaki sa svojom bazom podataka prilagođenom specifičnoj nameni. Sistem koristi LLM (Large Language Model) i RAG (Retrieval-Augmented Generation) pristup za inteligentnu pretragu životinja na osnovu opisa putem prirodnog jezika, kombinujući vektorsku bazu podataka sa generativnom veštačkom inteligencijom.

## Arhitektura sistema

### 1. User Service
- **Odgovornosti**: Autentifikacija, autorizacija, upravljanje korisnicima
- **Tehnologije**: Java (Spring Boot), PostgreSQL
- **Ključne funkcionalnosti**:
  - Registracija i autentifikacija (JWT)
  - Upravljanje ulogama (Admin, Upravnik, Veterinar, Volonter)
  - Validacija tokena za API Gateway
  - Refresh token mehanizam

### 2. Animal Registry Service
- **Odgovornosti**: Centralna evidencija životinja i medicinska dokumentacija
- **Tehnologije**: Java (Spring Boot), MongoDB
- **Ključne funkcionalnosti**:
  - CRUD operacije za životinje
  - Evidencija kategorija: psi, mačke, ptice, gmizavci, glodari, kunići, egzotične životinje
  - Upload slika životinja
  - Medicinska evidencija (vakcine, bolesti, tretmani, dijagnoze)
  - Pretraga po vrsti, imenu, statusu, čip ID-u
  - RAG pretraga životinja putem prirodnog jezika (semantičko pretraživanje po opisu)
  - Istorija premeštanja i promena statusa

### 3. Activity Tracking Service
- **Odgovornosti**: Praćenje dnevnih aktivnosti, zdravlja i ishrane
- **Tehnologije**: Java (Spring Boot), MongoDB
- **Ključne funkcionalnosti**:
  - Dnevna merenja (težina, temperatura, energija/raspoloženje - skala 1-10)
  - Praćenje aktivnosti (šetnja, igra, trening, socijalna interakcija)
  - Evidencija ishrane (vrsta hrane, količina u gramima, vreme)
  - Beleške o ponašanju
  - Real-time unos podataka

### 4. Analytics Service
- **Odgovornosti**: Agregacija podataka i generisanje izveštaja
- **Tehnologije**: Java (Spring Boot), MongoDB
- **Ključne funkcionalnosti**:
  - Statistika populacije (po vrsti, polu, uzrastu)
  - Zdravstveni trendovi (težina kroz vreme, učestalost bolesti po vrstama)
  - Grafikoni aktivnosti (dnevni, nedeljni, mesečni)
  - Analitika ishrane (potrošnja hrane po vrsti, optimizacija)
  - Mesečni i godišnji izveštaji
  - Heatmap aktivnosti po danu/satu
  - Prediktivna analitika za zdravstvene probleme

### API Gateway
- **Odgovornosti**: Centralna tačka pristupa, routing, autentifikacija
- **Tehnologije**: Java (Spring Cloud Gateway)
- **Funkcionalnosti**:
  - JWT validacija za sve zahteve
  - Rate limiting po korisniku
  - Request logging i monitoring
  - Routing zahteva ka odgovarajućim mikroservisima
  - Error handling i aggregation

## Komunikacija između servisa

Sistem koristi API Gateway kao centralnu tačku pristupa kroz koju prolazi sva komunikacija sa frontend-om.

### Sinhrona komunikacija (REST API):
- Frontend → API Gateway → Mikroservisi
- Service-to-service komunikacija kada je potrebna trenutna razmena podataka
- API Gateway ↔ User Service (validacija tokena)

### Asinhrona komunikacija (RabbitMQ):
- Event-driven arhitektura za background processing
- **Activity Tracking Service → Analytics Service**:
  - `DailyMetricsRecorded` event (svaki unos merenja)
  - `FeedingRecorded` event (svaki unos ishrane)
- **Animal Registry Service → Analytics Service**:
  - `AnimalRegistered` event (nova životinja)
  - `AnimalStatusChanged` event (promena statusa: adopted, deceased, transferred)
  - `MedicalTreatmentAdded` event (novi tretman)

## Baze podataka

- **PostgreSQL** (User Service) - Relaciona baza za korisnike, uloge i autentifikaciju
- **MongoDB** (Animal Registry, Activity Tracking, Analytics Services) - NoSQL baza za fleksibilne strukture i brze upite
- **Weaviate** (Animal Registry Service) - Vektorska baza podataka za semantičku pretragu životinja (RAG)

## Kontejnerizacija

- **Docker** i **Docker Compose** za kontejnerizaciju svih servisa
- Omogućava lako pokretanje, deployment i skaliranje aplikacije
- Svaki servis radi u sopstvenom kontejneru
- Zajednička Docker mreža za komunikaciju između servisa

## Frontend

- **Platforma**: Web aplikacija (Angular)
- **Odgovornost**: Renderovanje dashboard-a, grafikona, formi i svih UI komponenti
- **Funkcionalnosti**:
  - Interaktivni dashboard sa real-time statistikama
  - Pregled životinja sa filterima i pretragom
  - Forme za unos dnevnih merenja i aktivnosti
  - Medicinska evidencija i istorija
  - Prikaz plana prostorija sa rasporedom životinja
  - Grafikoni i analitike (Chart.js)
  - Responzivan dizajn za tablet upotrebu

## Funkcionalnosti sistema

### Registracija (Neautentifikovani korisnik)
- Unos korisničkog imena, email-a i lozinke
- Izbor uloge za koju se aplicira (Upravnik, Veterinar, Volonter)
- Admin mora da odobri registraciju

### Logovanje (Neautentifikovani korisnik)
- Unos korisničkog imena i lozinke
- Autentifikacija putem JWT tokena
- Refresh token mehanizam za automatsku obnovu sesije

### Pregled životinja (Svi autentifikovani korisnici)
- Pretraga životinja po imenu, vrsti, čip ID-u
- Filteriranje po statusu (active, adopted, quarantine, medical care, deceased)
- Sortiranje po uzrastu, datumu prijema
- Prikaz osnovnih informacija i fotografije
- **RAG pretraga (natural language search)**:
  - Korisnik unosi upit na prirodnom jeziku (npr. "mirna mačka pogodna za stan", "energičan pas koji voli decu")
  - Opis svake životinje se pri registraciji i ažuriranju vektorizuje i čuva u vektorskoj bazi (Weaviate)
  - Na osnovu korisničkog upita, sistem vrši semantičku pretragu nad vektorizovanim opisima i pronalazi najrelevantnije životinje
  - LLM (Anthropic Claude API) na osnovu pronađenih rezultata generiše konačan odgovor sa objašnjenjem zašto je svaka životinja predložena
  - Rezultati se rangiraju po relevantnosti

### Registracija nove životinje (Upravnik, Admin)
- Unos osnovnih podataka:
  - Kategorija (pas, mačka, ptica, gmizavac, glodar, kunić, egzotična životinja)
  - Rasa/vrsta
  - Ime/identifikator
  - Pol
  - Datum rođenja (procena ako je nepoznat)
  - Čip ID (opciono)
  - Status (active, quarantine, medical care)
  - Opis životinje (temperament, navike, posebne karakteristike - koristi se za RAG pretragu)
  - Posebne napomene
- Upload jedne ili više slika
- Triggerovanje `AnimalRegistered` eventa

### Dnevno praćenje aktivnosti (Upravnik)
- Izbor životinje za koju se unose podaci
- Unos merenja:
  - Težina (u gramima/kilogramima u zavisnosti od vrste)
  - Temperatura (°C)
  - Energija/raspoloženje (skala 1-10)
  - Beleške o ponašanju
- Praćenje aktivnosti:
  - Šetnja (minuti)
  - Igra (minuti)
  - Trening/obuka (minuti)
  - Socijalna interakcija (minuti)
- Unos obroka:
  - Vrsta hrane (suva hrana, vlažna hrana, sirovine, poslastice, suplementi)
  - Količina (grami)
  - Vreme obroka
- Automatsko čuvanje podataka
- Triggerovanje `DailyMetricsRecorded` i `FeedingRecorded` evenata

### Medicinska evidencija (Veterinar, Admin)
- Kreiranje medicinskog zapisa:
  - Tip: vakcina, tretman, dijagnoza, operacija, kontrolni pregled
  - Datum
  - Opis/dijagnoza
  - Prepisani lekovi i doziranje
  - Rezultati testova
  - Sledeći termin (ako je potreban follow-up)
- Pregled medicinske istorije životinje
- Triggerovanje `MedicalTreatmentAdded` eventa

### Analitike (Upravnik, Veterinar, Admin)

#### Izveštaji (Aktivnost, ishrana, populaciona statistika i zdravlje)
- Mesečni izveštaji (PDF)
- Godišnji izveštaji (PDF)
- Export podataka (CSV, Excel)

### Upravljanje korisnicima (Admin)
- Odobravanje novih registracija
- Dodela i izmena uloga
- Deaktivacija korisničkih naloga
- Pregled log-a aktivnosti korisnika

## Tehnologije

### Backend
- **Programski jezik**: Java 21
- **Web framework**: Spring Boot 3
- **API Gateway**: Spring Cloud Gateway
- **Relaciona baza**: PostgreSQL
- **NoSQL baza**: MongoDB
- **Message Broker**: RabbitMQ
- **Autentifikacija**: Spring Security + JWT
- **Build tool**: Maven
- **Dokumentacija API-ja**: Springdoc OpenAPI (Swagger)
- **Vektorska baza**: Weaviate (semantička pretraga za RAG)
- **LLM integracija**: Anthropic Claude API (generisanje odgovora na osnovu pretrage)
- **Embedding**: Weaviate text2vec modul (vektorizacija opisa životinja)

### Kontrola verzija
- **Git** i **GitHub** za verzionisanje koda i kolaboraciju

### Frontend
- **Framework**: Angular (najnovija verzija)
- **State management**: NgRx
- **HTTP client**: HttpClient
- **Routing**: Angular Router
- **Forms**: Reactive Forms
- **UI komponente**: PrimeNG
- **Charts**: Chart.js sa ng2-charts
- **File upload**: ng2-file-upload

<div style="page-break-before: always;"></div>

## Pokretanje projekta
```bash
# Kloniranje repozitorijuma
git clone https://github.com/Nikola034/animal-shelter.git
cd animal-shelter

# Pokretanje svih servisa sa Docker Compose
docker-compose up -d

# Backend servisi će biti dostupni na:
# API Gateway: http://localhost:8000
# User Service: http://localhost:8001
# Animal Registry Service: http://localhost:8002
# Activity Tracking Service: http://localhost:8003
# Analytics Service: http://localhost:8004

# Frontend će biti dostupan na:
# Angular app: http://localhost:4200
```


