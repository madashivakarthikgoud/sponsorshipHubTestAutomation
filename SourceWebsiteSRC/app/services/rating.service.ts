import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { Rating, RatingRequest } from '../models/rating.model';
import { ApiResponse } from '../models/common.model';

@Injectable({
  providedIn: 'root'
})
export class RatingService {
  private apiUrl = `${environment.apiUrl}/ratings`;

  constructor(private http: HttpClient) {}

  addRating(request: RatingRequest): Observable<ApiResponse<Rating>> {
    return this.http.post<ApiResponse<Rating>>(this.apiUrl, request);
  }

  getUserRatings(userId: number): Observable<Rating[]> {
    return this.http.get<Rating[]>(`${this.apiUrl}/user/${userId}`);
  }

  getMyRatings(): Observable<Rating[]> {
    return this.http.get<Rating[]>(`${this.apiUrl}/my-ratings`);
  }

  getAverageRating(userId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/average/${userId}`);
  }
}

