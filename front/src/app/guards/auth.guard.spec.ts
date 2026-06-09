import {expect} from '@jest/globals';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { provideRouter } from '@angular/router';
import { SessionService } from '../core/service/session.service';
import { AuthGuard } from './auth.guard';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let sessionService: SessionService;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), AuthGuard, SessionService],
    });
    guard = TestBed.inject(AuthGuard);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  describe('canActivate()', () => {
    it('should return true when the user is logged in', () => {
      sessionService.isLogged = true;
      expect(guard.canActivate()).toBe(true);
    });

    it('should return false and redirect to login when not logged in', () => {
      sessionService.isLogged = false;
      const navigateSpy = jest.spyOn(router, 'navigate');
      const result = guard.canActivate();
      expect(result).toBe(false);
      expect(navigateSpy).toHaveBeenCalledWith(['login']);
    });
  });
});

