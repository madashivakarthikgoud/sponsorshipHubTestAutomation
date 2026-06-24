import { Component, Inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface RatingDialogData {
  campaignName: string;
  userName: string;
  campaignId: number;
  userId: number;
}

@Component({
  selector: 'app-rating-dialog',
  template: `
    <h2 mat-dialog-title>
      <mat-icon class="title-icon">star_rate</mat-icon>
      Rate {{ data.userName }}
    </h2>
    <mat-dialog-content>
      <p class="dialog-subtitle">Campaign: <strong>{{ data.campaignName }}</strong></p>

      <div class="rating-stars">
        <button mat-icon-button *ngFor="let star of [1,2,3,4,5]"
                (click)="setRating(star)"
                class="star-btn"
                [class.selected]="star <= selectedRating">
          <mat-icon>{{ star <= selectedRating ? 'star' : 'star_border' }}</mat-icon>
        </button>
      </div>
      <div class="rating-text">{{ getRatingText() }}</div>

      <mat-form-field class="full-width" appearance="outline">
        <mat-label>Share your feedback</mat-label>
        <textarea matInput [formControl]="feedbackControl" rows="4"
                  placeholder="How was your experience working with {{ data.userName }}?"></textarea>
      </mat-form-field>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-raised-button color="primary"
              [disabled]="selectedRating === 0"
              (click)="submitRating()">
        <mat-icon>send</mat-icon>
        Submit Rating
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    h2 {
      display: flex;
      align-items: center;
      gap: 10px;
      margin: 0;
    }
    .title-icon {
      color: #ffc107;
    }
    .dialog-subtitle {
      color: #666;
      margin-bottom: 25px;
      font-size: 14px;
    }
    .rating-stars {
      display: flex;
      justify-content: center;
      gap: 8px;
      margin-bottom: 10px;
    }
    .star-btn {
      transform: scale(1.8);
      color: #ddd;
      transition: all 0.2s;
    }
    .star-btn.selected {
      color: #ffc107;
    }
    .star-btn:hover {
      transform: scale(2);
    }
    .rating-text {
      text-align: center;
      font-weight: 600;
      font-size: 18px;
      color: #ffc107;
      margin-bottom: 25px;
      min-height: 27px;
    }
    .full-width {
      width: 100%;
    }
    mat-dialog-content {
      min-width: 420px;
      padding-top: 20px;
    }
    mat-dialog-actions button mat-icon {
      margin-right: 5px;
    }
  `]
})
export class RatingDialogComponent {
  selectedRating = 0;
  feedbackControl = new FormControl('');

  constructor(
    public dialogRef: MatDialogRef<RatingDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: RatingDialogData
  ) {}

  setRating(rating: number): void {
    this.selectedRating = rating;
  }

  getRatingText(): string {
    const texts = ['', 'Poor', 'Fair', 'Good', 'Very Good', 'Excellent!'];
    return texts[this.selectedRating] || '';
  }

  submitRating(): void {
    this.dialogRef.close({
      campaignId: this.data.campaignId,
      ratedUserId: this.data.userId,
      score: this.selectedRating,
      feedback: this.feedbackControl.value
    });
  }
}


