import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AnimalResponse,
  AnimalListResponse,
  ImageUploadResponse,
  MessageResponse,
  AnimalCategory,
  AnimalStatus,
  CreateAnimalRequest,
  MedicalRecordResponse,
  MedicalRecordListResponse,
  CreateMedicalRecordRequest,
  MedicalRecordType,
  RagSearchResponse
} from '../../dto/animal';

@Injectable({
  providedIn: 'root'
})
export class AnimalService {

  private readonly animalsUrl = `${environment.apiGatewayUrl}/animals`;
  private readonly medicalUrl = `${environment.apiGatewayUrl}/medical`;

  constructor(private http: HttpClient) {}

  // ── Animal CRUD ──────────────────────────────────────────

  getAllAnimals(): Observable<AnimalListResponse> {
    return this.http.get<AnimalListResponse>(this.animalsUrl);
  }

  searchAnimals(
    name?: string,
    category?: AnimalCategory,
    status?: AnimalStatus,
    chipId?: string
  ): Observable<AnimalListResponse> {
    let params = new HttpParams();
    if (name) params = params.set('name', name);
    if (category) params = params.set('category', category);
    if (status) params = params.set('status', status);
    if (chipId) params = params.set('chipId', chipId);
    return this.http.get<AnimalListResponse>(`${this.animalsUrl}/search`, { params });
  }

  getAnimalById(id: string): Observable<AnimalResponse> {
    return this.http.get<AnimalResponse>(`${this.animalsUrl}/${id}`);
  }

  createAnimal(request: CreateAnimalRequest): Observable<AnimalResponse> {
    return this.http.post<AnimalResponse>(this.animalsUrl, request);
  }

  updateAnimal(id: string, request: Partial<CreateAnimalRequest>): Observable<AnimalResponse> {
    return this.http.put<AnimalResponse>(`${this.animalsUrl}/${id}`, request);
  }

  updateAnimalStatus(id: string, status: AnimalStatus, note?: string): Observable<AnimalResponse> {
    return this.http.patch<AnimalResponse>(`${this.animalsUrl}/${id}/status`, { status, note });
  }

  deleteAnimal(id: string): Observable<MessageResponse> {
    return this.http.delete<MessageResponse>(`${this.animalsUrl}/${id}`);
  }

  // ── Image Management ─────────────────────────────────────

  uploadImages(id: string, files: File[]): Observable<ImageUploadResponse> {
    const formData = new FormData();
    files.forEach(file => formData.append('files', file));
    return this.http.post<ImageUploadResponse>(`${this.animalsUrl}/${id}/images`, formData);
  }

  deleteImage(id: string, imagePath: string): Observable<MessageResponse> {
    const params = new HttpParams().set('path', imagePath);
    return this.http.delete<MessageResponse>(`${this.animalsUrl}/${id}/images`, { params });
  }

  getImageUrl(imagePath: string): string {
    // Images are served through the API gateway
    return `${environment.apiGatewayUrl}/animals/images/${imagePath}`;
  }

  // ── Medical Records ──────────────────────────────────────

  getMedicalRecords(animalId: string, type?: MedicalRecordType): Observable<MedicalRecordListResponse> {
    let params = new HttpParams();
    if (type) params = params.set('type', type);
    return this.http.get<MedicalRecordListResponse>(`${this.medicalUrl}/animal/${animalId}`, { params });
  }

  createMedicalRecord(request: CreateMedicalRecordRequest): Observable<MedicalRecordResponse> {
    return this.http.post<MedicalRecordResponse>(this.medicalUrl, request);
  }

  deleteMedicalRecord(id: string): Observable<MessageResponse> {
    return this.http.delete<MessageResponse>(`${this.medicalUrl}/${id}`);
  }

  // ── RAG Semantic Search ────────────────────────────────────

  ragSearch(query: string, limit: number = 5): Observable<RagSearchResponse> {
    return this.http.post<RagSearchResponse>(`${this.animalsUrl}/rag-search`, { query, limit });
  }
}
