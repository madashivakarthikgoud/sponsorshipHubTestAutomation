import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { Campaign, CampaignRequest } from '../models/campaign.model';
import { ApiResponse } from '../models/common.model';

@Injectable({
  providedIn: 'root'
})
export class CampaignService {
  private apiUrl = `${environment.apiUrl}/campaigns`;

  constructor(private http: HttpClient) {}

  getAllCampaigns(): Observable<Campaign[]> {
    return this.http.get<Campaign[]>(this.apiUrl);
  }

  getActiveCampaigns(): Observable<Campaign[]> {
    return this.http.get<Campaign[]>(`${this.apiUrl}/active`);
  }

  getMyCampaigns(): Observable<Campaign[]> {
    return this.http.get<Campaign[]>(`${this.apiUrl}/my-campaigns`);
  }

  getCampaignById(id: number): Observable<Campaign> {
    return this.http.get<Campaign>(`${this.apiUrl}/${id}`);
  }

  createCampaign(campaign: CampaignRequest): Observable<ApiResponse<Campaign>> {
    return this.http.post<ApiResponse<Campaign>>(this.apiUrl, campaign);
  }

  updateCampaign(id: number, campaign: CampaignRequest): Observable<ApiResponse<Campaign>> {
    return this.http.put<ApiResponse<Campaign>>(`${this.apiUrl}/${id}`, campaign);
  }

  deleteCampaign(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }

  searchCampaigns(name?: string, platform?: string, status?: string): Observable<Campaign[]> {
    let params = new HttpParams();
    if (name) params = params.set('name', name);
    if (platform) params = params.set('platform', platform);
    if (status) params = params.set('status', status);

    return this.http.get<Campaign[]>(`${this.apiUrl}/search`, { params });
  }

  updateCampaignStatus(id: number, status: string): Observable<ApiResponse<Campaign>> {
    return this.http.put<ApiResponse<Campaign>>(`${this.apiUrl}/${id}/status`, null, {
      params: { status }
    });
  }
}

