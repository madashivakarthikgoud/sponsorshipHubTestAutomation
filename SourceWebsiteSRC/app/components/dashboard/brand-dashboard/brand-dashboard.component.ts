import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CampaignService } from '../../../services/campaign.service';
import { SponsorshipService } from '../../../services/sponsorship.service';
import { PaymentService } from '../../../services/payment.service';
import { Campaign } from '../../../models/campaign.model';
import { SponsorshipRequest } from '../../../models/sponsorship.model';

@Component({
  selector: 'app-brand-dashboard',
  templateUrl: './brand-dashboard.component.html',
  styleUrls: ['./brand-dashboard.component.scss']
})
export class BrandDashboardComponent implements OnInit {
  campaigns: Campaign[] = [];
  filteredCampaigns: Campaign[] = [];
  requests: SponsorshipRequest[] = [];
  totalSpending = 0;
  isLoading = true;
  error: string | null = null;

  // Filter
  selectedStatus = 'ALL';
  statuses = ['ALL', 'ACTIVE', 'COMPLETED', 'PAUSED', 'EXPIRED', 'CANCELLED'];

  constructor(
    private campaignService: CampaignService,
    private sponsorshipService: SponsorshipService,
    private paymentService: PaymentService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    this.error = null;

    this.campaignService.getMyCampaigns().subscribe({
      next: (campaigns) => {
        console.log('Campaigns loaded:', campaigns);
        this.campaigns = campaigns;
        this.filterCampaigns();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load campaigns:', err);
        this.isLoading = false;
        if (err.status === 401) {
          this.error = 'Session expired. Please login again.';
        } else if (err.status === 403) {
          this.error = 'Access denied. Please login with correct credentials.';
        } else if (err.status === 0) {
          this.error = 'Cannot connect to server. Please check if backend is running on port 7070.';
        } else {
          this.error = `Error: ${err.message || err.statusText || 'Unknown error'}`;
        }
      }
    });

    this.sponsorshipService.getBrandRequests().subscribe({
      next: (requests) => this.requests = requests,
      error: (err) => console.error('Failed to load requests:', err)
    });

    this.paymentService.getSpending().subscribe({
      next: (spending) => this.totalSpending = spending || 0,
      error: (err) => console.error('Failed to load spending:', err)
    });
  }

  filterCampaigns(): void {
    if (this.selectedStatus === 'ALL') {
      this.filteredCampaigns = this.campaigns;
    } else {
      this.filteredCampaigns = this.campaigns.filter(c => c.status === this.selectedStatus);
    }
  }

  get activeCampaigns(): number {
    return this.campaigns.filter(c => c.status === 'ACTIVE').length;
  }

  get completedCampaigns(): number {
    return this.campaigns.filter(c => c.status === 'COMPLETED').length;
  }

  get pendingRequests(): number {
    return this.requests.filter(r => r.status === 'PENDING').length;
  }

  createCampaign(): void {
    this.router.navigate(['/campaigns/new']);
  }

  viewCampaign(id: number): void {
    this.router.navigate(['/campaigns', id]);
  }
}

