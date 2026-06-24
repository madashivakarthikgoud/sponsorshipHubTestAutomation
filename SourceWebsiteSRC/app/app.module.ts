import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Angular Material Modules
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatBadgeModule } from '@angular/material/badge';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTabsModule } from '@angular/material/tabs';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatTooltipModule } from '@angular/material/tooltip';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthInterceptor } from './interceptors/auth.interceptor';

// Components
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
import { NavbarComponent } from './components/shared/navbar/navbar.component';
import { RatingDialogComponent } from './components/shared/rating-dialog/rating-dialog.component';
import { PaymentDialogComponent } from './components/shared/payment-dialog/payment-dialog.component';
import { ChangePasswordComponent } from './components/shared/change-password/change-password.component';
import { FilterPipe } from './pipes/filter.pipe';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    SignupComponent,
    AdminDashboardComponent,
    BrandDashboardComponent,
    InfluencerDashboardComponent,
    CampaignListComponent,
    CampaignFormComponent,
    CampaignDetailComponent,
    SponsorshipRequestComponent,
    PaymentComponent,
    RatingComponent,
    NotificationComponent,
    NavbarComponent,
    RatingDialogComponent,
    PaymentDialogComponent,
    ChangePasswordComponent,
    FilterPipe
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    MatToolbarModule,
    MatButtonModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatIconModule,
    MatMenuModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatBadgeModule,
    MatListModule,
    MatSidenavModule,
    MatTabsModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTooltipModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

