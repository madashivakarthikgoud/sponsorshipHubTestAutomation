import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CampaignService } from '../../../services/campaign.service';
import { SponsorshipService } from '../../../services/sponsorship.service';
import { PaymentService } from '../../../services/payment.service';
import { RatingService } from '../../../services/rating.service';
import { AuthService } from '../../../services/auth.service';
import { Campaign } from '../../../models/campaign.model';
import { SponsorshipRequest } from '../../../models/sponsorship.model';

@Component({
  selector: 'app-influencer-dashboard',
  templateUrl: './influencer-dashboard.component.html',
  styleUrls: ['./influencer-dashboard.component.scss']
})
export class InfluencerDashboardComponent implements OnInit {
  activeCampaigns: Campaign[] = [];
  myApplications: SponsorshipRequest[] = [];
  totalEarnings = 0;
  averageRating = 0;
  isLoading = true;
  error: string | null = null;

  constructor(
    private campaignService: CampaignService,
    private sponsorshipService: SponsorshipService,
    private paymentService: PaymentService,
    private ratingService: RatingService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    this.error = null;

    this.campaignService.getActiveCampaigns().subscribe({
      next: (campaigns) => {
        this.activeCampaigns = campaigns;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load campaigns:', err);
        this.isLoading = false;
        this.error = 'Failed to load data. Please check if the backend is running.';
      }
    });

    this.sponsorshipService.getMyApplications().subscribe({
      next: (applications) => this.myApplications = applications,
      error: (err) => console.error('Failed to load applications:', err)
    });

    this.paymentService.getEarnings().subscribe({
      next: (earnings) => this.totalEarnings = earnings || 0,
      error: (err) => console.error('Failed to load earnings:', err)
    });

    const user = this.authService.getCurrentUser();
    if (user) {
      this.ratingService.getAverageRating(user.id).subscribe({
        next: (rating) => this.averageRating = rating || 0,
        error: (err) => console.error('Failed to load rating:', err)
      });
    }
  }

  get pendingApplications(): number {
    return this.myApplications.filter(a => a.status === 'PENDING').length;
  }

  get acceptedApplications(): number {
    return this.myApplications.filter(a => a.status === 'ACCEPTED').length;
  }

  viewCampaign(id: number): void {
    this.router.navigate(['/campaigns', id]);
  }

  browseCampaigns(): void {
    this.router.navigate(['/campaigns']);
  }
}

