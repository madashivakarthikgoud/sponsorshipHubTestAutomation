import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';

import { LoginComponent } from './components/auth/login/login.component';
import { SignupComponent } from './components/auth/signup/signup.component';
import { AdminDashboardComponent } from './components/dashboard/admin-dashboard/admin-dashboard.component';
import { BrandDashboardComponent } from './components/dashboard/brand-dashboard/brand-dashboard.component';
import { InfluencerDashboardComponent } from './components/dashboard/influencer-dashboard/influencer-dashboard.component';
import { CampaignListComponent } from './components/campaign/campaign-list/campaign-list.component';
import { CampaignFormComponent } from './components/campaign/campaign-form/campaign-form.component';
import { CampaignDetailComponent } from './components/campaign/campaign-detail/campaign-detail.component';
import { SponsorshipRequestComponent } from './components/sponsorship/sponsorship-request/sponsorship-request.component';
import { PaymentComponent } from './components/payment/payment.component';
import { RatingComponent } from './components/rating/rating.component';
import { NotificationComponent } from './components/notification/notification.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  {
    path: 'dashboard/admin',
    component: AdminDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'ADMIN' }
  },
  {
    path: 'dashboard/brand',
    component: BrandDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'BRAND' }
  },
  {
    path: 'dashboard/influencer',
    component: InfluencerDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'INFLUENCER' }
  },
  { path: 'campaigns', component: CampaignListComponent, canActivate: [AuthGuard] },
  { path: 'campaigns/new', component: CampaignFormComponent, canActivate: [AuthGuard, RoleGuard], data: { role: 'BRAND' } },
  { path: 'campaigns/edit/:id', component: CampaignFormComponent, canActivate: [AuthGuard, RoleGuard], data: { role: 'BRAND' } },
  { path: 'campaigns/:id', component: CampaignDetailComponent, canActivate: [AuthGuard] },
  { path: 'sponsorship-requests', component: SponsorshipRequestComponent, canActivate: [AuthGuard] },
  { path: 'payments', component: PaymentComponent, canActivate: [AuthGuard] },
  { path: 'ratings', component: RatingComponent, canActivate: [AuthGuard] },
  { path: 'notifications', component: NotificationComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

