import {expect} from '@jest/globals';
import { TestBed } from '@angular/core/testing';
import { SessionInformation } from '../models/sessionInformation.interface';
import { SessionService } from './session.service';

describe('SessionService', () => {
  let service: SessionService;

  const mockSessionInfo: SessionInformation = {
    token: 'test-token',
    type: 'Bearer',
    id: 1,
    username: 'testuser',
    firstName: 'Test',
    lastName: 'User',
    admin: false,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('initial state', () => {
    it('should have isLogged = false by default', () => {
      expect(service.isLogged).toBe(false);
    });

    it('should have sessionInformation = undefined by default', () => {
      expect(service.sessionInformation).toBeUndefined();
    });
  });

  describe('$isLogged()', () => {
    it('should emit false initially', (done) => {
      service.$isLogged().subscribe((value) => {
        expect(value).toBe(false);
        done();
      });
    });

    it('should emit true after logIn()', (done) => {
      service.logIn(mockSessionInfo);
      service.$isLogged().subscribe((value) => {
        expect(value).toBe(true);
        done();
      });
    });

    it('should emit false after logOut()', (done) => {
      service.logIn(mockSessionInfo);
      service.logOut();
      service.$isLogged().subscribe((value) => {
        expect(value).toBe(false);
        done();
      });
    });
  });

  describe('logIn()', () => {
    it('should set isLogged to true', () => {
      service.logIn(mockSessionInfo);
      expect(service.isLogged).toBe(true);
    });

    it('should set sessionInformation with the provided user', () => {
      service.logIn(mockSessionInfo);
      expect(service.sessionInformation).toEqual(mockSessionInfo);
    });
  });

  describe('logOut()', () => {
    it('should set isLogged to false', () => {
      service.logIn(mockSessionInfo);
      service.logOut();
      expect(service.isLogged).toBe(false);
    });

    it('should clear sessionInformation', () => {
      service.logIn(mockSessionInfo);
      service.logOut();
      expect(service.sessionInformation).toBeUndefined();
    });
  });
});
