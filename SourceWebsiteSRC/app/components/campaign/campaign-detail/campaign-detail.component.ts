import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CampaignService } from '../../../services/campaign.service';
import { SponsorshipService } from '../../../services/sponsorship.service';
import { AuthService } from '../../../services/auth.service';
import { Campaign } from '../../../models/campaign.model';
import { SponsorshipRequest } from '../../../models/sponsorship.model';

@Component({
  selector: 'app-campaign-detail',
  templateUrl: './campaign-detail.component.html',
  styleUrls: ['./campaign-detail.component.scss']
})
export class CampaignDetailComponent implements OnInit {
  campaign: Campaign | null = null;
  applications: SponsorshipRequest[] = [];
  isLoading = true;
  isBrand = false;
  isInfluencer = false;
  hasApplied = false;
  proposal = '';
  isApplying = false;

  displayedColumns = ['influencer', 'proposal', 'status', 'date', 'actions'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private campaignService: CampaignService,
    private sponsorshipService: SponsorshipService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.isBrand = this.authService.hasRole('BRAND');
    this.isInfluencer = this.authService.hasRole('INFLUENCER');
  }

  ngOnInit(): void {
    const campaignId = this.route.snapshot.params['id'];
    this.loadCampaign(campaignId);
  }

  loadCampaign(id: number): void {
    this.campaignService.getCampaignById(id).subscribe({
      next: (campaign) => {
        this.campaign = campaign;
        this.isLoading = false;

        if (this.isBrand) {
          this.loadApplications(id);
        }

        if (this.isInfluencer) {
          this.checkIfApplied();
        }
      },
      error: () => {
        this.snackBar.open('Campaign not found', 'Close', { duration: 3000 });
        this.router.navigate(['/campaigns']);
      }
    });
  }

  loadApplications(campaignId: number): void {
    this.sponsorshipService.getCampaignRequests(campaignId).subscribe({
      next: (applications) => this.applications = applications
    });
  }

  checkIfApplied(): void {
    this.sponsorshipService.getMyApplications().subscribe({
      next: (applications) => {
        this.hasApplied = applications.some(app => app.campaign?.id === this.campaign?.id);
      }
    });
  }

  applyForCampaign(): void {
    if (!this.campaign || !this.proposal.trim()) {
      this.snackBar.open('Please enter a proposal', 'Close', { duration: 3000 });
      return;
    }

    this.isApplying = true;
    this.sponsorshipService.applyForCampaign({
      campaignId: this.campaign.id,
      proposal: this.proposal
    }).subscribe({
      next: () => {
        this.snackBar.open('Application submitted successfully!', 'Close', { duration: 3000 });
        this.hasApplied = true;
        this.isApplying = false;
      },
      error: (error) => {
        this.isApplying = false;
        this.snackBar.open(error.error?.message || 'Failed to apply', 'Close', { duration: 3000 });
      }
    });
  }

  updateApplicationStatus(requestId: number, status: string): void {
    this.sponsorshipService.updateRequestStatus(requestId, status).subscribe({
      next: () => {
        this.snackBar.open(`Application ${status.toLowerCase()}`, 'Close', { duration: 3000 });
        if (this.campaign) {
          this.loadApplications(this.campaign.id);
        }
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/campaigns']);
  }
}

