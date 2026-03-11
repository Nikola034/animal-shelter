import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextarea } from 'primeng/inputtextarea';
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { FileUploadModule } from 'primeng/fileupload';
import { DividerModule } from 'primeng/divider';
import { MessageService } from 'primeng/api';

import { AnimalService } from '../../../services/animal/animal-service';
import { ANIMAL_CATEGORY_OPTIONS } from '../../../dto/animal/AnimalCategory';
import { ANIMAL_STATUS_OPTIONS } from '../../../dto/animal/AnimalStatus';
import { GENDER_OPTIONS } from '../../../dto/animal/Gender';
import { CreateAnimalRequest } from '../../../dto/animal/CreateAnimalRequest';
import { AnimalResponse } from '../../../dto/animal/AnimalResponse';

@Component({
  selector: 'app-animal-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CardModule,
    InputTextModule,
    InputNumberModule,
    InputTextarea,
    DropdownModule,
    ButtonModule,
    ToastModule,
    FileUploadModule,
    DividerModule
  ],
  providers: [MessageService],
  templateUrl: 'animal-form.html'
})
export class AnimalForm implements OnInit, OnDestroy {

  form!: FormGroup;
  isEditMode = false;
  animalId: string | null = null;
  animal: AnimalResponse | null = null;
  loading = false;
  saving = false;

  categoryOptions = ANIMAL_CATEGORY_OPTIONS;
  genderOptions = GENDER_OPTIONS;
  statusOptions = ANIMAL_STATUS_OPTIONS;

  selectedFiles: File[] = [];
  uploading = false;

  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private animalService: AnimalService,
    private messageService: MessageService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();

    this.animalId = this.route.snapshot.paramMap.get('id');
    if (this.animalId) {
      this.isEditMode = true;
      this.loadAnimal(this.animalId);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm(): void {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      category: [null, Validators.required],
      breed: [''],
      gender: [null, Validators.required],
      age_months: [null],
      weight: [null],
      color: [''],
      chip_id: [''],
      description: [''],
      status: [null]
    });
  }

  private loadAnimal(id: string): void {
    this.loading = true;
    this.animalService.getAnimalById(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (animal) => {
          this.animal = animal;
          this.form.patchValue({
            name: animal.name,
            category: animal.category,
            breed: animal.breed || '',
            gender: animal.gender,
            age_months: animal.age_months,
            weight: animal.weight,
            color: animal.color || '',
            chip_id: animal.chip_id || '',
            description: animal.description || '',
            status: animal.status
          });
          this.loading = false;
        },
        error: () => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to load animal data',
            life: 5000
          });
          this.loading = false;
          this.router.navigate(['/app/animals']);
        }
      });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    const formValue = this.form.value;

    const request: CreateAnimalRequest = {
      name: formValue.name,
      category: formValue.category,
      gender: formValue.gender,
      breed: formValue.breed || undefined,
      age_months: formValue.age_months || undefined,
      weight: formValue.weight || undefined,
      color: formValue.color || undefined,
      chip_id: formValue.chip_id || undefined,
      description: formValue.description || undefined
    };

    if (this.isEditMode && this.animalId) {
      const updateRequest: any = { ...request };
      if (formValue.status) {
        updateRequest.status = formValue.status;
      }
      this.animalService.updateAnimal(this.animalId, updateRequest)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (animal) => {
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: `${animal.name} updated successfully`,
              life: 3000
            });
            this.saving = false;
            setTimeout(() => this.router.navigate(['/app/animals', animal.id]), 1000);
          },
          error: (err) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: err.error?.message || 'Failed to update animal',
              life: 5000
            });
            this.saving = false;
          }
        });
    } else {
      this.animalService.createAnimal(request)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (animal) => {
            // If files were selected, upload them
            if (this.selectedFiles.length > 0) {
              this.uploadImages(animal.id, animal.name);
            } else {
              this.messageService.add({
                severity: 'success',
                summary: 'Success',
                detail: `${animal.name} registered successfully`,
                life: 3000
              });
              setTimeout(() => this.router.navigate(['/app/animals', animal.id]), 1000);
            }
            this.saving = false;
          },
          error: (err) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: err.error?.message || 'Failed to register animal',
              life: 5000
            });
            this.saving = false;
          }
        });
    }
  }

  onFilesSelected(event: any): void {
    this.selectedFiles = event.currentFiles || event.files || [];
  }

  onFilesRemoved(): void {
    this.selectedFiles = [];
  }

  uploadImages(animalId: string, animalName: string): void {
    this.uploading = true;
    this.animalService.uploadImages(animalId, this.selectedFiles)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: `${animalName} registered with images`,
            life: 3000
          });
          this.uploading = false;
          setTimeout(() => this.router.navigate(['/app/animals', animalId]), 1000);
        },
        error: () => {
          this.messageService.add({
            severity: 'warn',
            summary: 'Partial Success',
            detail: `${animalName} registered but image upload failed`,
            life: 5000
          });
          this.uploading = false;
          setTimeout(() => this.router.navigate(['/app/animals', animalId]), 1000);
        }
      });
  }

  goBack(): void {
    if (this.isEditMode && this.animalId) {
      this.router.navigate(['/app/animals', this.animalId]);
    } else {
      this.router.navigate(['/app/animals']);
    }
  }

  get pageTitle(): string {
    return this.isEditMode ? 'Edit Animal' : 'Register Animal';
  }

  isFieldInvalid(field: string): boolean {
    const control = this.form.get(field);
    return !!(control && control.invalid && control.touched);
  }
}
