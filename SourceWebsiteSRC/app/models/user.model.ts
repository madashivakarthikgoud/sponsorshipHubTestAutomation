export interface User {
  id: number;
  name: string;
  email: string;
  role: 'ADMIN' | 'BRAND' | 'INFLUENCER';
  bio?: string;
  profileImage?: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  name: string;
  email: string;
  role: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  role: string;
}

