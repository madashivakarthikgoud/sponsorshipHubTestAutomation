import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { DashboardStats, ApiResponse } from '../models/common.model';
import { User } from '../models/user.model';
import { Campaign } from '../models/campaign.model';
import { SponsorshipRequest } from '../models/sponsorship.model';
import { Payment } from '../models/payment.model';
import { Rating } from '../models/rating.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  getStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.apiUrl}/stats`);
  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/users`);
  }

  getUsersByRole(role: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/users/role/${role}`);
  }

  deleteUser(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/users/${id}`);
  }

  getAllCampaigns(): Observable<Campaign[]> {
    return this.http.get<Campaign[]>(`${this.apiUrl}/campaigns`);
  }

  getAllRequests(): Observable<SponsorshipRequest[]> {
    return this.http.get<SponsorshipRequest[]>(`${this.apiUrl}/requests`);
  }

  getAllPayments(): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.apiUrl}/payments`);
  }

  getAllRatings(): Observable<Rating[]> {
    return this.http.get<Rating[]>(`${this.apiUrl}/ratings`);
  }
}

