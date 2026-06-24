import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AdminService } from '../../../services/admin.service';
import { DashboardStats } from '../../../models/common.model';
import { User } from '../../../models/user.model';
import { Campaign } from '../../../models/campaign.model';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {
  stats: DashboardStats = {
    totalUsers: 0,
    totalBrands: 0,
    totalInfluencers: 0,
    totalCampaigns: 0,
    activeCampaigns: 0,
    totalRequests: 0,
    pendingRequests: 0,
    totalEarnings: 0,
    totalSpending: 0,
    averageRating: 0
  };
  users: User[] = [];
  filteredUsers: User[] = [];
  campaigns: Campaign[] = [];
  isLoading = true;
  error: string | null = null;
  selectedRole = 'ALL';

  displayedUserColumns = ['id', 'name', 'email', 'role', 'actions'];
  displayedCampaignColumns = ['id', 'name', 'brand', 'platform', 'budget', 'status'];

  constructor(
    private adminService: AdminService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    this.error = null;

    // Load stats
    this.adminService.getStats().subscribe({
      next: (stats) => {
        this.stats = stats;
      },
      error: (err) => {
        console.error('Failed to load stats:', err);
      }
    });

    // Load users
    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        console.log('Users loaded:', users);
        this.users = users;
        this.filterUsers();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load users:', err);
        this.isLoading = false;
        if (err.status === 401) {
          this.error = 'Session expired. Please login again.';
        } else if (err.status === 403) {
          this.error = 'Access denied. Please login with admin credentials.';
        } else if (err.status === 0) {
          this.error = 'Cannot connect to server. Please check if backend is running on port 7070.';
        } else {
          this.error = `Error: ${err.message || err.statusText || 'Unknown error'}`;
        }
      }
    });

    // Load campaigns
    this.adminService.getAllCampaigns().subscribe({
      next: (campaigns) => this.campaigns = campaigns,
      error: (err) => console.error('Failed to load campaigns:', err)
    });
  }

  filterUsers(): void {
    if (this.selectedRole === 'ALL') {
      this.filteredUsers = this.users;
    } else {
      this.filteredUsers = this.users.filter(u => u.role === this.selectedRole);
    }
  }

  deleteUser(id: number): void {
    if (confirm('Are you sure you want to delete this user? This will also delete all their related data (campaigns, payments, ratings, etc.)')) {
      this.adminService.deleteUser(id).subscribe({
        next: () => {
          this.snackBar.open('User deleted successfully', 'Close', { duration: 3000 });
          this.loadData();
        },
        error: (err) => {
          console.error('Failed to delete user:', err);
          this.snackBar.open('Failed to delete user: ' + (err.error?.message || 'Unknown error'), 'Close', { duration: 5000 });
        }
      });
    }
  }
}

