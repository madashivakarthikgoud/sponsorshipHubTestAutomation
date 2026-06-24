import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PaymentService } from '../../services/payment.service';
import { AuthService } from '../../services/auth.service';
import { Payment } from '../../models/payment.model';

@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.scss']
})
export class PaymentComponent implements OnInit {
  payments: Payment[] = [];
  totalAmount = 0;
  isLoading = true;
  isBrand = false;

  displayedColumns = ['id', 'campaign', 'amount', 'status', 'date', 'actions'];

  constructor(
    private paymentService: PaymentService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.isBrand = this.authService.hasRole('BRAND');
  }

  ngOnInit(): void {
    this.loadPayments();
    this.loadTotals();
  }

  loadPayments(): void {
    const source = this.isBrand
      ? this.paymentService.getBrandPayments()
      : this.paymentService.getInfluencerPayments();

    source.subscribe({
      next: (payments) => {
        this.payments = payments;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  loadTotals(): void {
    const source = this.isBrand
      ? this.paymentService.getSpending()
      : this.paymentService.getEarnings();

    source.subscribe({
      next: (total) => this.totalAmount = total || 0
    });
  }

  completePayment(paymentId: number): void {
    this.paymentService.completePayment(paymentId).subscribe({
      next: () => {
        this.snackBar.open('Payment completed', 'Close', { duration: 3000 });
        this.loadPayments();
        this.loadTotals();
      },
      error: (error) => {
        this.snackBar.open(error.error?.message || 'Failed to complete payment', 'Close', { duration: 3000 });
      }
    });
  }
}

