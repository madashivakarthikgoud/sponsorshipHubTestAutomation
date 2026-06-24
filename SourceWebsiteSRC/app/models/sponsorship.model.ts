import { User } from './user.model';
import { Campaign } from './campaign.model';

export interface SponsorshipRequest {
  id: number;
  influencer: User;
  campaign: Campaign;
  proposal: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'COMPLETED';
  workDescription?: string;
  workSubmittedAt?: string;
  workCompletedAt?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface SponsorshipApplicationRequest {
  campaignId: number;
  proposal: string;
}

