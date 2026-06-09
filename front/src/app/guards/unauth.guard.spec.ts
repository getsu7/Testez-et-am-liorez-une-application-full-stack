import {expect} from '@jest/globals';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { provideRouter } from '@angular/router';
import { SessionService } from '../core/service/session.service';
import { UnauthGuard } from './unauth.guard';

describe('UnauthGuard', () => {
  let guard: UnauthGuard;
  let sessionService: SessionService;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), UnauthGuard, SessionService],
    });
    guard = TestBed.inject(UnauthGuard);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  describe('canActivate()', () => {
    it('should return true when the user is NOT logged in', () => {
      sessionService.isLogged = false;
      expect(guard.canActivate()).toBe(true);
    });

    it('should return false and redirect to rentals when the user is already logged in', () => {
      sessionService.isLogged = true;
      const navigateSpy = jest.spyOn(router, 'navigate');
      const result = guard.canActivate();
      expect(result).toBe(false);
      expect(navigateSpy).toHaveBeenCalledWith(['rentals']);
    });
  });
});

