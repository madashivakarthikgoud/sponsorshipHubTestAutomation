import { User } from './user.model';
import { Campaign } from './campaign.model';

export interface Rating {
  id: number;
  campaign: Campaign;
  rater: User;
  rated: User;
  score: number;
  feedback: string;
  createdAt: string;
}

export interface RatingRequest {
  campaignId: number;
  ratedUserId: number;
  score: number;
  feedback: string;
}

