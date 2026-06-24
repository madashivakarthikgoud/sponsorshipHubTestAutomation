import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RatingService } from '../../services/rating.service';
import { AuthService } from '../../services/auth.service';
import { Rating } from '../../models/rating.model';

@Component({
  selector: 'app-rating',
  templateUrl: './rating.component.html',
  styleUrls: ['./rating.component.scss']
})
export class RatingComponent implements OnInit {
  ratings: Rating[] = [];
  averageRating = 0;
  isLoading = true;

  constructor(
    private ratingService: RatingService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadRatings();
  }

  loadRatings(): void {
    this.ratingService.getMyRatings().subscribe({
      next: (ratings) => {
        this.ratings = ratings;
        this.calculateAverage();
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  calculateAverage(): void {
    if (this.ratings.length > 0) {
      const sum = this.ratings.reduce((acc, r) => acc + r.score, 0);
      this.averageRating = sum / this.ratings.length;
    }
  }

  getStars(score: number): string {
    return '⭐'.repeat(score);
  }

  getCountForRating(rating: number): number {
    return this.ratings.filter(r => r.score === rating).length;
  }

  getPercentage(rating: number): number {
    if (this.ratings.length === 0) return 0;
    return (this.getCountForRating(rating) / this.ratings.length) * 100;
  }
}

