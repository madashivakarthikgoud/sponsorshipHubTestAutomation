import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const expectedRole = route.data['role'];
    const user = this.authService.getCurrentUser();

    if (user && user.role === expectedRole) {
      return true;
    }

    // Redirect to appropriate dashboard
    this.router.navigate([this.authService.getDashboardRoute()]);
    return false;
  }
}

