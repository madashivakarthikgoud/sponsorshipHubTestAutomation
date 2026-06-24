import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CampaignService } from '../../../services/campaign.service';
import { SponsorshipService } from '../../../services/sponsorship.service';
import { AuthService } from '../../../services/auth.service';
import { Campaign } from '../../../models/campaign.model';

@Component({
  selector: 'app-campaign-list',
  templateUrl: './campaign-list.component.html',
  styleUrls: ['./campaign-list.component.scss']
})
export class CampaignListComponent implements OnInit {
  campaigns: Campaign[] = [];
  filteredCampaigns: Campaign[] = [];
  appliedCampaignIds: Set<number> = new Set();
  isLoading = true;
  isBrand = false;
  isInfluencer = false;

  searchName = '';
  filterPlatform = '';
  filterStatus = '';
  filterApplication = ''; // 'applied', 'not-applied', or ''

  platforms = ['Instagram', 'YouTube', 'TikTok', 'Twitter', 'Facebook'];
  statuses = ['ACTIVE', 'PAUSED', 'COMPLETED', 'CANCELLED', 'EXPIRED'];

  constructor(
    private campaignService: CampaignService,
    private sponsorshipService: SponsorshipService,
    private authService: AuthService,
    private router: Router
  ) {
    this.isBrand = this.authService.hasRole('BRAND');
    this.isInfluencer = this.authService.hasRole('INFLUENCER');
  }

  ngOnInit(): void {
    this.loadCampaigns();
    if (this.isInfluencer) {
      this.loadMyApplications();
    }
  }

  loadCampaigns(): void {
    const source = this.isBrand
      ? this.campaignService.getMyCampaigns()
      : this.campaignService.getActiveCampaigns();

    source.subscribe({
      next: (campaigns) => {
        this.campaigns = campaigns;
        this.filteredCampaigns = campaigns;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  loadMyApplications(): void {
    this.sponsorshipService.getMyApplications().subscribe({
      next: (applications) => {
        this.appliedCampaignIds = new Set(applications.map(app => app.campaign?.id).filter(id => id !== undefined) as number[]);
        this.applyFilters();
      }
    });
  }

  hasApplied(campaignId: number): boolean {
    return this.appliedCampaignIds.has(campaignId);
  }

  applyFilters(): void {
    this.filteredCampaigns = this.campaigns.filter(campaign => {
      const matchesName = !this.searchName ||
        campaign.name.toLowerCase().includes(this.searchName.toLowerCase());
      const matchesPlatform = !this.filterPlatform || campaign.platform === this.filterPlatform;
      const matchesStatus = !this.filterStatus || campaign.status === this.filterStatus;

      // Application filter for influencers
      let matchesApplication = true;
      if (this.isInfluencer && this.filterApplication) {
        if (this.filterApplication === 'applied') {
          matchesApplication = this.hasApplied(campaign.id);
        } else if (this.filterApplication === 'not-applied') {
          matchesApplication = !this.hasApplied(campaign.id);
        }
      }

      return matchesName && matchesPlatform && matchesStatus && matchesApplication;
    });
  }

  clearFilters(): void {
    this.searchName = '';
    this.filterPlatform = '';
    this.filterStatus = '';
    this.filterApplication = '';
    this.filteredCampaigns = this.campaigns;
  }

  viewCampaign(id: number): void {
    this.router.navigate(['/campaigns', id]);
  }

  editCampaign(id: number): void {
    this.router.navigate(['/campaigns/edit', id]);
  }

  createCampaign(): void {
    this.router.navigate(['/campaigns/new']);
  }

  deleteCampaign(id: number, event: Event): void {
    event.stopPropagation();
    if (confirm('Are you sure you want to delete this campaign?')) {
      this.campaignService.deleteCampaign(id).subscribe({
        next: () => this.loadCampaigns()
      });
    }
  }
}

