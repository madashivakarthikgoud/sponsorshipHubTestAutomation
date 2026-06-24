import { User } from './user.model';
import { Campaign } from './campaign.model';

export interface Payment {
  id: number;
  campaign: Campaign;
  influencer: User;
  brand: User;
  amount: number;
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'REFUNDED';
  createdAt: string;
  paidAt?: string;
  transactionId: string;
}

export interface PaymentRequest {
  campaignId: number;
  influencerId: number;
  amount: number;
}

