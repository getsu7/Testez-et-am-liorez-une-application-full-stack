import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Session } from '../models/session.interface';
import { SessionApiService } from './session-api.service';
import {expect} from "@jest/globals";

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const mockSession: Session = {
    id: 1,
    name: 'Test Session',
    description: 'A test description',
    date: new Date('2024-01-15'),
    teacher_id: 2,
    users: [1, 3],
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('all()', () => {
    it('should return an array of sessions via GET', () => {
      service.all().subscribe((sessions) => {
        expect(sessions).toEqual([mockSession]);
        expect(sessions.length).toBe(1);
      });
      const req = httpMock.expectOne('api/session');
      expect(req.request.method).toBe('GET');
      req.flush([mockSession]);
    });
  });

  describe('detail()', () => {
    it('should return a session by id via GET', () => {
      service.detail('1').subscribe((session) => {
        expect(session).toEqual(mockSession);
      });
      const req = httpMock.expectOne('api/session/1');
      expect(req.request.method).toBe('GET');
      req.flush(mockSession);
    });
  });

  describe('create()', () => {
    it('should create a session via POST', () => {
      service.create(mockSession).subscribe((session) => {
        expect(session).toEqual(mockSession);
      });
      const req = httpMock.expectOne('api/session');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockSession);
      req.flush(mockSession);
    });
  });

  describe('update()', () => {
    it('should update a session via PUT', () => {
      const updated = { ...mockSession, name: 'Updated Session' };
      service.update('1', updated).subscribe((session) => {
        expect(session).toEqual(updated);
      });
      const req = httpMock.expectOne('api/session/1');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updated);
      req.flush(updated);
    });
  });

  describe('delete()', () => {
    it('should delete a session via DELETE', () => {
      service.delete('1').subscribe();
      const req = httpMock.expectOne('api/session/1');
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });

  describe('participate()', () => {
    it('should call participate endpoint via POST', () => {
      service.participate('1', '2').subscribe();
      const req = httpMock.expectOne('api/session/1/participate/2');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toBeNull();
      req.flush(null);
    });
  });

  describe('unParticipate()', () => {
    it('should call unParticipate endpoint via DELETE', () => {
      service.unParticipate('1', '2').subscribe();
      const req = httpMock.expectOne('api/session/1/participate/2');
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});
