import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { SponsorshipRequest, SponsorshipApplicationRequest } from '../models/sponsorship.model';
import { ApiResponse } from '../models/common.model';

@Injectable({
  providedIn: 'root'
})
export class SponsorshipService {
  private apiUrl = `${environment.apiUrl}/sponsorship`;

  constructor(private http: HttpClient) {}

  applyForCampaign(request: SponsorshipApplicationRequest): Observable<ApiResponse<SponsorshipRequest>> {
    return this.http.post<ApiResponse<SponsorshipRequest>>(`${this.apiUrl}/apply`, request);
  }

  getMyApplications(): Observable<SponsorshipRequest[]> {
    return this.http.get<SponsorshipRequest[]>(`${this.apiUrl}/my-applications`);
  }

  getBrandRequests(): Observable<SponsorshipRequest[]> {
    return this.http.get<SponsorshipRequest[]>(`${this.apiUrl}/brand-requests`);
  }

  getCampaignRequests(campaignId: number): Observable<SponsorshipRequest[]> {
    return this.http.get<SponsorshipRequest[]>(`${this.apiUrl}/campaign/${campaignId}`);
  }

  getRequestById(id: number): Observable<SponsorshipRequest> {
    return this.http.get<SponsorshipRequest>(`${this.apiUrl}/${id}`);
  }

  updateRequestStatus(id: number, status: string): Observable<ApiResponse<SponsorshipRequest>> {
    return this.http.put<ApiResponse<SponsorshipRequest>>(`${this.apiUrl}/${id}/status`, null, {
      params: { status }
    });
  }

  submitWork(id: number, workDescription?: string): Observable<ApiResponse<SponsorshipRequest>> {
    return this.http.post<ApiResponse<SponsorshipRequest>>(
      `${this.apiUrl}/${id}/submit-work`,
      null,
      { params: workDescription ? { workDescription } : {} }
    );
  }

  markWorkAsComplete(id: number): Observable<ApiResponse<SponsorshipRequest>> {
    return this.http.put<ApiResponse<SponsorshipRequest>>(
      `${this.apiUrl}/${id}/mark-work-complete`,
      null
    );
  }
}

