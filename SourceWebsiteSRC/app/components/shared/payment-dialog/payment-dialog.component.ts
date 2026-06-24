import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface PaymentDialogData {
  campaignName: string;
  influencerName: string;
  campaignId: number;
  influencerId: number;
  suggestedAmount: number;
}

@Component({
  selector: 'app-payment-dialog',
  template: `
    <h2 mat-dialog-title>
      <mat-icon class="title-icon">payment</mat-icon>
      Make Payment
    </h2>
    <mat-dialog-content>
      <div class="payment-info">
        <div class="info-row">
          <span class="label">Campaign:</span>
          <span class="value">{{ data.campaignName }}</span>
        </div>
        <div class="info-row">
          <span class="label">Influencer:</span>
          <span class="value">{{ data.influencerName }}</span>
        </div>
      </div>

      <form [formGroup]="paymentForm">
        <mat-form-field class="full-width" appearance="outline">
          <mat-label>Payment Amount</mat-label>
          <span matPrefix>$ &nbsp;</span>
          <input matInput type="number" formControlName="amount" placeholder="Enter amount">
          <mat-error *ngIf="paymentForm.get('amount')?.hasError('required')">Amount is required</mat-error>
          <mat-error *ngIf="paymentForm.get('amount')?.hasError('min')">Amount must be positive</mat-error>
        </mat-form-field>
      </form>

      <div class="payment-note">
        <mat-icon>info</mat-icon>
        <span>Payment will be processed and the influencer will be notified.</span>
      </div>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-raised-button color="primary"
              [disabled]="paymentForm.invalid"
              (click)="submitPayment()">
        <mat-icon>send</mat-icon>
        Process Payment
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
      color: #4caf50;
    }
    .payment-info {
      background: #f5f5f5;
      border-radius: 8px;
      padding: 15px;
      margin-bottom: 20px;
    }
    .info-row {
      display: flex;
      justify-content: space-between;
      margin-bottom: 8px;
    }
    .info-row:last-child {
      margin-bottom: 0;
    }
    .label {
      color: #666;
    }
    .value {
      font-weight: 500;
    }
    .full-width {
      width: 100%;
    }
    .payment-note {
      display: flex;
      align-items: center;
      gap: 10px;
      background: #e3f2fd;
      padding: 12px;
      border-radius: 8px;
      font-size: 13px;
      color: #1976d2;
    }
    .payment-note mat-icon {
      font-size: 20px;
      width: 20px;
      height: 20px;
    }
    mat-dialog-content {
      min-width: 400px;
      padding-top: 20px;
    }
    mat-dialog-actions button mat-icon {
      margin-right: 5px;
    }
  `]
})
export class PaymentDialogComponent {
  paymentForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<PaymentDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PaymentDialogData
  ) {
    this.paymentForm = this.fb.group({
      amount: [data.suggestedAmount || '', [Validators.required, Validators.min(1)]]
    });
  }

  submitPayment(): void {
    if (this.paymentForm.valid) {
      this.dialogRef.close({
        campaignId: this.data.campaignId,
        influencerId: this.data.influencerId,
        amount: this.paymentForm.value.amount
      });
    }
  }
}

