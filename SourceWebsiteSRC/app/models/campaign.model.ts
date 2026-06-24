import { User } from './user.model';

export interface Campaign {
  id: number;
  name: string;
  description: string;
  platform: string;
  budget: number;
  startDate: string;
  endDate: string;
  eligibility: string;
  status: 'ACTIVE' | 'PAUSED' | 'COMPLETED' | 'CANCELLED';
  brand: User;
}

export interface CampaignRequest {
  name: string;
  description: string;
  platform: string;
  budget: number;
  startDate: string;
  endDate: string;
  eligibility: string;
}

