import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CampaignService } from '../../../services/campaign.service';

@Component({
  selector: 'app-campaign-form',
  templateUrl: './campaign-form.component.html',
  styleUrls: ['./campaign-form.component.scss']
})
export class CampaignFormComponent implements OnInit {
  campaignForm: FormGroup;
  isLoading = false;
  isEditMode = false;
  campaignId: number | null = null;

  platforms = ['Instagram', 'YouTube', 'TikTok', 'Twitter', 'Facebook', 'LinkedIn'];

  constructor(
    private fb: FormBuilder,
    private campaignService: CampaignService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.campaignForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      platform: ['', [Validators.required]],
      budget: [null, [Validators.required, Validators.min(1)]],
      startDate: ['', [Validators.required]],
      endDate: ['', [Validators.required]],
      eligibility: ['']
    });
  }

  ngOnInit(): void {
    this.campaignId = this.route.snapshot.params['id'];
    if (this.campaignId) {
      this.isEditMode = true;
      this.loadCampaign();
    }
  }

  loadCampaign(): void {
    if (!this.campaignId) return;

    this.isLoading = true;
    this.campaignService.getCampaignById(this.campaignId).subscribe({
      next: (campaign) => {
        this.campaignForm.patchValue({
          name: campaign.name,
          description: campaign.description,
          platform: campaign.platform,
          budget: campaign.budget,
          startDate: campaign.startDate,
          endDate: campaign.endDate,
          eligibility: campaign.eligibility
        });
        this.isLoading = false;
      },
      error: () => {
        this.snackBar.open('Failed to load campaign', 'Close', { duration: 3000 });
        this.router.navigate(['/campaigns']);
      }
    });
  }

  onSubmit(): void {
    if (this.campaignForm.invalid) return;

    this.isLoading = true;
    const formData = this.campaignForm.value;

    const request = this.isEditMode && this.campaignId
      ? this.campaignService.updateCampaign(this.campaignId, formData)
      : this.campaignService.createCampaign(formData);

    request.subscribe({
      next: () => {
        this.snackBar.open(
          `Campaign ${this.isEditMode ? 'updated' : 'created'} successfully!`,
          'Close',
          { duration: 3000 }
        );
        this.router.navigate(['/campaigns']);
      },
      error: (error) => {
        this.isLoading = false;
        this.snackBar.open(error.error?.message || 'Operation failed', 'Close', { duration: 3000 });
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/campaigns']);
  }
}

