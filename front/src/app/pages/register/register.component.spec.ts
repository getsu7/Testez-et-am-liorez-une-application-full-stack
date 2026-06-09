import {expect} from '@jest/globals';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { provideRouter } from '@angular/router';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../core/service/auth.service';
import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let router: Router;

  const mockAuthService = {
    register: jest.fn(),
  };

  beforeEach(async () => {
    jest.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        provideRouter([]),
        provideNoopAnimations(),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: mockAuthService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('form', () => {
    it('should be invalid when empty', () => {
      expect(component.form.valid).toBe(false);
    });

    it('should be invalid with a missing field', () => {
      component.form.setValue({
        email: 'user@test.com',
        firstName: 'Jane',
        lastName: 'Doe',
        password: '',
      });
      expect(component.form.valid).toBe(false);
    });

    it('should be valid with all correct values', () => {
      component.form.setValue({
        email: 'user@test.com',
        firstName: 'Jane',
        lastName: 'Doe',
        password: 'password123',
      });
      expect(component.form.valid).toBe(true);
    });
  });

  describe('submit()', () => {
    it('should call authService.register and navigate to /login on success', () => {
      mockAuthService.register.mockReturnValue(of(null));
      const navigateSpy = jest.spyOn(router, 'navigate');
      component.form.setValue({
        email: 'user@test.com',
        firstName: 'Jane',
        lastName: 'Doe',
        password: 'password123',
      });

      component.submit();

      expect(mockAuthService.register).toHaveBeenCalled();
      expect(navigateSpy).toHaveBeenCalledWith(['/login']);
    });

    it('should set onError to true on register failure', () => {
      mockAuthService.register.mockReturnValue(
        throwError(() => new Error('Email already used'))
      );
      component.form.setValue({
        email: 'existing@test.com',
        firstName: 'Jane',
        lastName: 'Doe',
        password: 'password123',
      });

      component.submit();

      expect(component.onError).toBe(true);
    });
  });
});
