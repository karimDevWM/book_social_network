import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  isTokenNotValid(): boolean {
    return !this.isTokenValid()
  }
  isTokenValid(): boolean {
    const token = this.token;
    if(!token) {
      return false;
    }
    // decode the token
    const jwtHelper: JwtHelperService = new JwtHelperService();
    //check expiry date
    const isTokenExpired = jwtHelper.isTokenExpired(token);
    if(isTokenExpired) {
      localStorage.clear();
      return false;
    }
    return true;
  }

  getFullname(): string | null {
    const token = this.token;
    if(!token) {
      return null;
    }

    const jwtHelper: JwtHelperService = new JwtHelperService();
    const decodeToken = jwtHelper.decodeToken(token);
    const fullnameClaim = 'fullName';

    return decodeToken[fullnameClaim] || null;
  }

  set token(token: string) {
    localStorage.setItem('token', token);
  }

  get token() {
    return localStorage.getItem('token') as string;
  }
}
