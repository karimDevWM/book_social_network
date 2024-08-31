import { Component } from '@angular/core';
import { RegistrationRequest } from '../../services/models';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../services/services';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {

  constructor(
    private router: Router,
    private authService: AuthenticationService
  ){}

  login() {
    this.router.navigate(['login']);
  }
  
  registerRequest: RegistrationRequest = {email: '', firstname: '', lastname: '', password: ''}
  errorMsg: Array<string> = [];

  register() {
    this.errorMsg = [];
    this.authService.register({
      body: this.registerRequest
    }).subscribe({
      next: (): void => {
        this.router.navigate(['activate-account']); 
      },
      error: (err): void => {
        console.log(err);
        if(err.error.validationErrors) {
          this.errorMsg = err.error.validationErrors;
        }
      }
    });
  }
}
