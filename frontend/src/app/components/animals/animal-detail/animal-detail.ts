import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextarea } from 'primeng/inputtextarea';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DividerModule } from 'primeng/divider';
import { FileUploadModule } from 'primeng/fileupload';
import { ImageModule } from 'primeng/image';
import { TooltipModule } from 'primeng/tooltip';
import { CalendarModule } from 'primeng/calendar';
import { TimelineModule } from 'primeng/timeline';
import { MessageService, ConfirmationService } from 'primeng/api';

import { AnimalService } from '../../../services/animal/animal-service';
import { AuthService } from '../../../services/auth/auth-service';
import { AnimalResponse, StatusHistoryEntry } from '../../../dto/animal/AnimalResponse';
import { MedicalRecordResponse } from '../../../dto/animal/MedicalRecordResponse';
import { AnimalStatus, ANIMAL_STATUS_OPTIONS, getAnimalStatusSeverity } from '../../../dto/animal/AnimalStatus';
import { getCategorySeverity } from '../../../dto/animal/AnimalCategory';
import {
  MedicalRecordType,
  MEDICAL_RECORD_TYPE_OPTIONS,
  getMedicalRecordTypeSeverity
} from '../../../dto/animal/MedicalRecordType';

@Component({
  selector: 'app-animal-detail',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    CardModule,
    TagModule,
    ButtonModule,
    TableModule,
    DialogModule,
    DropdownModule,
    InputTextModule,
    InputTextarea,
    ToastModule,
    ConfirmDialogModule,
    DividerModule,
    FileUploadModule,
    ImageModule,
    TooltipModule,
    CalendarModule,
    TimelineModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: 'animal-detail.html'
})
export class AnimalDetail implements OnInit, OnDestroy {

  animal: AnimalResponse | null = null;
  medicalRecords: MedicalRecordResponse[] = [];
  loading = true;
  animalId!: string;

  // Status change dialog
  showStatusDialog = false;
  newStatus: AnimalStatus | null = null;
  statusNote = '';
  statusOptions = ANIMAL_STATUS_OPTIONS;

  // Medical record dialog
  showMedicalDialog = false;
  medicalForm!: FormGroup;
  medicalRecordTypeOptions = MEDICAL_RECORD_TYPE_OPTIONS;
  savingMedical = false;

  // Image upload
  showImageDialog = false;
  selectedFiles: File[] = [];
  uploadingImages = false;

  private destroy$ = new Subject<void>();

  constructor(
    private animalService: AnimalService,
    private authService: AuthService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.animalId = this.route.snapshot.paramMap.get('id')!;
    this.initMedicalForm();
    this.loadAnimal();
    this.loadMedicalRecords();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initMedicalForm(): void {
    this.medicalForm = this.fb.group({
      type: [null, Validators.required],
      title: ['', [Validators.required, Validators.minLength(2)]],
      description: [''],
      date: [new Date(), Validators.required],
      notes: ['']
    });
  }

  loadAnimal(): void {
    this.loading = true;
    this.animalService.getAnimalById(this.animalId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (animal) => {
          this.animal = animal;
          this.loading = false;
        },
        error: () => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to load animal',
            life: 5000
          });
          this.loading = false;
          this.router.navigate(['/app/animals']);
        }
      });
  }

  loadMedicalRecords(): void {
    this.animalService.getMedicalRecords(this.animalId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.medicalRecords = response.records;
        },
        error: () => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to load medical records',
            life: 5000
          });
        }
      });
  }

  // ── Navigation ───────────────────────────────────────────

  goBack(): void {
    this.router.navigate(['/app/animals']);
  }

  editAnimal(): void {
    this.router.navigate(['/app/animals', this.animalId, 'edit']);
  }

  // ── Role Checks ──────────────────────────────────────────

  canManageAnimals(): boolean {
    return this.authService.canManageAnimals();
  }

  canManageMedicalRecords(): boolean {
    return this.authService.canManageMedicalRecords();
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  // ── Status Change ────────────────────────────────────────

  openStatusDialog(): void {
    this.newStatus = this.animal?.status || null;
    this.statusNote = '';
    this.showStatusDialog = true;
  }

  confirmStatusChange(): void {
    if (!this.newStatus || !this.animal) return;

    this.animalService.updateAnimalStatus(this.animalId, this.newStatus, this.statusNote || undefined)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updated) => {
          this.animal = updated;
          this.showStatusDialog = false;
          this.messageService.add({
            severity: 'success',
            summary: 'Status Updated',
            detail: `Status changed to ${updated.status}`,
            life: 3000
          });
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: err.error?.message || 'Failed to update status',
            life: 5000
          });
        }
      });
  }

  // ── Delete Animal ────────────────────────────────────────

  confirmDelete(): void {
    this.confirmationService.confirm({
      message: `Are you sure you want to delete "${this.animal?.name}"? This action cannot be undone.`,
      header: 'Confirm Deletion',
      icon: 'pi pi-exclamation-triangle',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => this.deleteAnimal()
    });
  }

  private deleteAnimal(): void {
    this.animalService.deleteAnimal(this.animalId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Deleted',
            detail: `${this.animal?.name} has been deleted`,
            life: 3000
          });
          setTimeout(() => this.router.navigate(['/app/animals']), 1000);
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: err.error?.message || 'Failed to delete animal',
            life: 5000
          });
        }
      });
  }

  // ── Image Management ─────────────────────────────────────

  openImageDialog(): void {
    this.selectedFiles = [];
    this.showImageDialog = true;
  }

  onImagesSelected(event: any): void {
    this.selectedFiles = event.currentFiles || event.files || [];
  }

  uploadImages(): void {
    if (this.selectedFiles.length === 0) return;

    this.uploadingImages = true;
    this.animalService.uploadImages(this.animalId, this.selectedFiles)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.messageService.add({
            severity: 'success',
            summary: 'Images Uploaded',
            detail: response.message,
            life: 3000
          });
          this.showImageDialog = false;
          this.uploadingImages = false;
          this.loadAnimal(); // reload to get updated image_paths
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: err.error?.message || 'Failed to upload images',
            life: 5000
          });
          this.uploadingImages = false;
        }
      });
  }

  confirmDeleteImage(imagePath: string): void {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete this image?',
      header: 'Delete Image',
      icon: 'pi pi-exclamation-triangle',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => this.deleteImage(imagePath)
    });
  }

  private deleteImage(imagePath: string): void {
    this.animalService.deleteImage(this.animalId, imagePath)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Image Deleted',
            detail: 'Image has been removed',
            life: 3000
          });
          this.loadAnimal();
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: err.error?.message || 'Failed to delete image',
            life: 5000
          });
        }
      });
  }

  getImageUrl(path: string): string {
    return this.animalService.getImageUrl(path);
  }

  // ── Medical Records ──────────────────────────────────────

  openMedicalDialog(): void {
    this.medicalForm.reset({ date: new Date() });
    this.showMedicalDialog = true;
  }

  saveMedicalRecord(): void {
    if (this.medicalForm.invalid) {
      this.medicalForm.markAllAsTouched();
      return;
    }

    this.savingMedical = true;
    const formValue = this.medicalForm.value;

    const dateValue = formValue.date instanceof Date
      ? `${formValue.date.getFullYear()}-${(formValue.date.getMonth() + 1).toString().padStart(2, '0')}-${formValue.date.getDate().toString().padStart(2, '0')}`
      : formValue.date;

    this.animalService.createMedicalRecord({
      animal_id: this.animalId,
      type: formValue.type,
      title: formValue.title,
      description: formValue.description || undefined,
      date: dateValue,
      notes: formValue.notes || undefined
    }).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Record Added',
            detail: 'Medical record created successfully',
            life: 3000
          });
          this.showMedicalDialog = false;
          this.savingMedical = false;
          this.loadMedicalRecords();
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: err.error?.message || 'Failed to create medical record',
            life: 5000
          });
          this.savingMedical = false;
        }
      });
  }

  confirmDeleteMedicalRecord(record: MedicalRecordResponse): void {
    this.confirmationService.confirm({
      message: `Delete medical record "${record.title}"?`,
      header: 'Delete Record',
      icon: 'pi pi-exclamation-triangle',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => this.deleteMedicalRecord(record.id)
    });
  }

  private deleteMedicalRecord(id: string): void {
    this.animalService.deleteMedicalRecord(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Deleted',
            detail: 'Medical record deleted',
            life: 3000
          });
          this.loadMedicalRecords();
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: err.error?.message || 'Failed to delete record',
            life: 5000
          });
        }
      });
  }

  // ── Helpers ──────────────────────────────────────────────

  getStatusSeverity(status: AnimalStatus): string {
    return getAnimalStatusSeverity(status);
  }

  getCategorySeverity(category: string): string {
    return getCategorySeverity(category as any);
  }

  getMedicalTypeSeverity(type: MedicalRecordType): string {
    return getMedicalRecordTypeSeverity(type);
  }

  getGenderIcon(gender: string): string {
    switch (gender) {
      case 'Male': return 'pi pi-mars';
      case 'Female': return 'pi pi-venus';
      default: return 'pi pi-question-circle';
    }
  }

  formatAge(months: number | null): string {
    if (months === null || months === undefined) return 'Unknown';
    if (months < 12) return `${months} month${months !== 1 ? 's' : ''}`;
    const years = Math.floor(months / 12);
    const remainingMonths = months % 12;
    let result = `${years} year${years !== 1 ? 's' : ''}`;
    if (remainingMonths > 0) result += ` ${remainingMonths} mo`;
    return result;
  }

  isMedicalFormInvalid(field: string): boolean {
    const control = this.medicalForm.get(field);
    return !!(control && control.invalid && control.touched);
  }
}
