export interface DashboardStats {
  totalCampaigns: number;
  activeCampaigns: number;
  totalRequests: number;
  pendingRequests: number;
  totalEarnings: number;
  totalSpending: number;
  averageRating: number;
  totalUsers: number;
  totalBrands: number;
  totalInfluencers: number;
}

export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data?: T;
}

