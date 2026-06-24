import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { Payment, PaymentRequest } from '../models/payment.model';
import { ApiResponse } from '../models/common.model';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiUrl = `${environment.apiUrl}/payments`;

  constructor(private http: HttpClient) {}

  createPayment(request: PaymentRequest): Observable<ApiResponse<Payment>> {
    return this.http.post<ApiResponse<Payment>>(this.apiUrl, request);
  }

  completePayment(id: number): Observable<ApiResponse<Payment>> {
    return this.http.put<ApiResponse<Payment>>(`${this.apiUrl}/${id}/complete`, null);
  }

  getPaymentById(id: number): Observable<Payment> {
    return this.http.get<Payment>(`${this.apiUrl}/${id}`);
  }

  getInfluencerPayments(): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.apiUrl}/influencer`);
  }

  getBrandPayments(): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.apiUrl}/brand`);
  }

  getEarnings(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/earnings`);
  }

  getSpending(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/spending`);
  }
}

