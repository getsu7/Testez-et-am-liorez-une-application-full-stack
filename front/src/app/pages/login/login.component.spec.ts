import {expect} from '@jest/globals';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { provideRouter } from '@angular/router';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { SessionInformation } from 'src/app/core/models/sessionInformation.interface';
import { AuthService } from '../../core/service/auth.service';
import { SessionService } from 'src/app/core/service/session.service';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let router: Router;
  let sessionService: SessionService;

  const mockSessionInfo: SessionInformation = {
    token: 'jwt-token',
    type: 'Bearer',
    id: 1,
    username: 'user@test.com',
    firstName: 'Jane',
    lastName: 'Doe',
    admin: false,
  };

  const mockAuthService = {
    login: jest.fn(),
  };

  beforeEach(async () => {
    jest.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideRouter([]),
        provideNoopAnimations(),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: mockAuthService },
        SessionService,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    sessionService = TestBed.inject(SessionService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('form', () => {
    it('should be invalid when empty', () => {
      expect(component.form.valid).toBe(false);
    });

    it('should be invalid with a bad email', () => {
      component.form.setValue({ email: 'not-an-email', password: 'pass' });
      expect(component.form.controls['email'].valid).toBe(false);
    });

    it('should be valid with correct values', () => {
      component.form.setValue({ email: 'user@test.com', password: 'password123' });
      expect(component.form.valid).toBe(true);
    });
  });

  describe('submit()', () => {
    it('should call authService.login and navigate to /sessions on success', () => {
      mockAuthService.login.mockReturnValue(of(mockSessionInfo));
      const navigateSpy = jest.spyOn(router, 'navigate');
      component.form.setValue({ email: 'user@test.com', password: 'password123' });

      component.submit();

      expect(mockAuthService.login).toHaveBeenCalled();
      expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
      expect(sessionService.isLogged).toBe(true);
    });

    it('should set onError to true on login failure', () => {
      mockAuthService.login.mockReturnValue(throwError(() => new Error('Unauthorized')));
      component.form.setValue({ email: 'user@test.com', password: 'wrong' });

      component.submit();

      expect(component.onError).toBe(true);
    });
  });
});
