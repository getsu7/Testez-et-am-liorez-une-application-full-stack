import { provideHttpClient } from '@angular/common/http';
import { expect } from '@jest/globals';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoginRequest } from '../models/loginRequest.interface';
import { RegisterRequest } from '../models/registerRequest.interface';
import { SessionInformation } from '../models/sessionInformation.interface';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const mockLoginRequest: LoginRequest = {
    email: 'user@test.com',
    password: 'password123',
  };

  const mockRegisterRequest: RegisterRequest = {
    email: 'user@test.com',
    firstName: 'Jane',
    lastName: 'Doe',
    password: 'password123',
  };

  const mockSessionInfo: SessionInformation = {
    token: 'jwt-token',
    type: 'Bearer',
    id: 1,
    username: 'user@test.com',
    firstName: 'Jane',
    lastName: 'Doe',
    admin: false,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('register()', () => {
    it('should send a POST request to the register endpoint', () => {
      service.register(mockRegisterRequest).subscribe((result) => {
        expect(result).toBeUndefined();
      });
      const req = httpMock.expectOne('/api/auth/register');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockRegisterRequest);
      req.flush(null);
    });
  });

  describe('login()', () => {
    it('should send a POST request to the login endpoint and return session info', () => {
      service.login(mockLoginRequest).subscribe((session) => {
        expect(session).toEqual(mockSessionInfo);
      });
      const req = httpMock.expectOne('/api/auth/login');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockLoginRequest);
      req.flush(mockSessionInfo);
    });
  });
});

