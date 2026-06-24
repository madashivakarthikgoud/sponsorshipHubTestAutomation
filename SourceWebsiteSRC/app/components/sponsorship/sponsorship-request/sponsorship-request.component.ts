import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { SponsorshipService } from '../../../services/sponsorship.service';
import { PaymentService } from '../../../services/payment.service';
import { RatingService } from '../../../services/rating.service';
import { AuthService } from '../../../services/auth.service';
import { SponsorshipRequest } from '../../../models/sponsorship.model';
import { RatingDialogComponent } from '../../shared/rating-dialog/rating-dialog.component';
import { PaymentDialogComponent } from '../../shared/payment-dialog/payment-dialog.component';

@Component({
  selector: 'app-sponsorship-request',
  templateUrl: './sponsorship-request.component.html',
  styleUrls: ['./sponsorship-request.component.scss']
})
export class SponsorshipRequestComponent implements OnInit {
  requests: SponsorshipRequest[] = [];
  isLoading = true;
  isBrand = false;
  isInfluencer = false;
  paymentsByRequestId: Map<number, any> = new Map();
  ratingsByRequestId: Map<number, any> = new Map();

  displayedColumns: string[] = [];

  constructor(
    private sponsorshipService: SponsorshipService,
    private paymentService: PaymentService,
    private ratingService: RatingService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {
    this.isBrand = this.authService.hasRole('BRAND');
    this.isInfluencer = this.authService.hasRole('INFLUENCER');
    this.displayedColumns = this.isBrand
      ? ['campaign', 'influencer', 'proposal', 'status', 'date', 'actions']
      : ['campaign', 'brand', 'proposal', 'status', 'date', 'actions'];
  }

  ngOnInit(): void {
    this.loadRequests();
  }

  loadRequests(): void {
    this.isLoading = true;
    const source = this.isBrand
      ? this.sponsorshipService.getBrandRequests()
      : this.sponsorshipService.getMyApplications();

    source.subscribe({
      next: (requests) => {
        this.requests = requests;
        this.loadPaymentStatus();
        this.loadRatings();
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  loadPaymentStatus(): void {
    if (this.isBrand) {
      this.paymentService.getBrandPayments().subscribe({
        next: (payments) => {
          this.paymentsByRequestId = new Map();
          payments.forEach(payment => {
            if (payment.campaign) {
              // Map payments by campaign ID to find associated payments
              this.requests.forEach(request => {
                if (request.campaign?.id === payment.campaign?.id && 
                    request.influencer?.id === payment.influencer?.id) {
                  this.paymentsByRequestId.set(request.id, payment);
                }
              });
            }
          });
        },
        error: () => {}
      });
    }
  }

  loadRatings(): void {
    this.ratingService.getMyRatings().subscribe({
      next: (ratings) => {
        this.ratingsByRequestId = new Map();
        ratings.forEach(rating => {
          // Match rating to request by campaign and rated user
          this.requests.forEach(request => {
            let matches = false;
            if (this.isBrand) {
              // Brand is rating influencer
              matches = (rating.campaign?.id === request.campaign?.id && 
                        rating.rated?.id === request.influencer?.id);
            } else {
              // Influencer is rating brand
              matches = (rating.campaign?.id === request.campaign?.id && 
                        rating.rated?.id === request.campaign?.brand?.id);
            }
            if (matches) {
              this.ratingsByRequestId.set(request.id, rating);
            }
          });
        });
      },
      error: () => {}
    });
  }

  hasPaymentCompleted(request: SponsorshipRequest): boolean {
    const payment = this.paymentsByRequestId.get(request.id);
    return payment && payment.status === 'COMPLETED';
  }

  hasPaymentPending(request: SponsorshipRequest): boolean {
    const payment = this.paymentsByRequestId.get(request.id);
    return payment && payment.status === 'PENDING';
  }

  hasUserRated(request: SponsorshipRequest): boolean {
    return this.ratingsByRequestId.has(request.id);
  }

  getUserRating(request: SponsorshipRequest): any {
    return this.ratingsByRequestId.get(request.id);
  }

  getRatingStars(score: number): string {
    const filledStars = Math.round(score);
    const emptyStars = 5 - filledStars;
    return '★'.repeat(filledStars) + '☆'.repeat(emptyStars);
  }

  // Helper methods for counting
  getPendingCount(): number {
    return this.requests.filter(r => r.status === 'PENDING').length;
  }

  getAcceptedCount(): number {
    return this.requests.filter(r => r.status === 'ACCEPTED').length;
  }

  getCompletedCount(): number {
    return this.requests.filter(r => r.status === 'COMPLETED').length;
  }

  updateStatus(requestId: number, status: string): void {
    this.sponsorshipService.updateRequestStatus(requestId, status).subscribe({
      next: () => {
        this.snackBar.open(`Request ${status.toLowerCase()}`, 'Close', { duration: 3000 });
        this.loadRequests();
      },
      error: (error) => {
        this.snackBar.open(error.error?.message || 'Failed to update', 'Close', { duration: 3000 });
      }
    });
  }

  openPaymentDialog(request: SponsorshipRequest): void {
    const dialogRef = this.dialog.open(PaymentDialogComponent, {
      data: {
        campaignName: request.campaign?.name,
        influencerName: request.influencer?.name,
        campaignId: request.campaign?.id,
        influencerId: request.influencer?.id,
        suggestedAmount: request.campaign?.budget
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.paymentService.createPayment(result).subscribe({
          next: () => {
            this.snackBar.open('Payment created successfully!', 'Close', { duration: 3000 });
            this.loadRequests(); // Reload full requests to get updated status
          },
          error: (error) => {
            this.snackBar.open(error.error?.message || 'Payment failed', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }

  openRatingDialog(request: SponsorshipRequest): void {
    const userToRate = this.isBrand ? request.influencer : request.campaign?.brand;
    const dialogRef = this.dialog.open(RatingDialogComponent, {
      data: {
        campaignName: request.campaign?.name,
        userName: userToRate?.name,
        campaignId: request.campaign?.id,
        userId: userToRate?.id
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.ratingService.addRating(result).subscribe({
          next: () => {
            this.snackBar.open('Rating submitted successfully!', 'Close', { duration: 3000 });
            this.loadRatings(); // Reload ratings to show the disabled star display
          },
          error: (error) => {
            this.snackBar.open(error.error?.message || 'Failed to submit rating', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }

  markAsCompleted(requestId: number): void {
    this.updateStatus(requestId, 'COMPLETED');
  }

  submitWork(request: SponsorshipRequest): void {
    const workDescription = prompt('Enter work description (optional):', '');
    if (workDescription !== null) {
      this.sponsorshipService.submitWork(request.id, workDescription).subscribe({
        next: () => {
          this.snackBar.open('Work submitted successfully! Waiting for brand approval.', 'Close', { duration: 3000 });
          this.loadRequests();
        },
        error: (error) => {
          this.snackBar.open(error.error?.message || 'Failed to submit work', 'Close', { duration: 3000 });
        }
      });
    }
  }

  markWorkAsComplete(request: SponsorshipRequest): void {
    if (confirm('Mark this work as complete? Payment can now be processed.')) {
      this.sponsorshipService.markWorkAsComplete(request.id).subscribe({
        next: () => {
          this.snackBar.open('Work marked as complete! Ready for payment.', 'Close', { duration: 3000 });
          this.loadRequests();
        },
        error: (error) => {
          this.snackBar.open(error.error?.message || 'Failed to mark work as complete', 'Close', { duration: 3000 });
        }
      });
    }
  }

  hasWorkSubmitted(request: SponsorshipRequest): boolean {
    return request.workSubmittedAt !== null && request.workSubmittedAt !== undefined;
  }

  hasWorkCompleted(request: SponsorshipRequest): boolean {
    return request.workCompletedAt !== null && request.workCompletedAt !== undefined;
  }
}

