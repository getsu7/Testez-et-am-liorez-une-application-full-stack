import {expect} from '@jest/globals';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { Session } from '../../../../core/models/session.interface';
import { SessionService } from '../../../../core/service/session.service';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { ListComponent } from './list.component';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  const mockSessions: Session[] = [
    {
      id: 1,
      name: 'Yoga Session',
      description: 'Morning yoga',
      date: new Date('2024-06-01'),
      teacher_id: 2,
      users: [1],
    },
    {
      id: 2,
      name: 'Pilates Session',
      description: 'Evening pilates',
      date: new Date('2024-06-15'),
      teacher_id: 3,
      users: [],
    },
  ];

  const mockSessionService: Partial<SessionService> = {
    sessionInformation: {
      admin: true,
      id: 1,
      token: 'test-token',
      type: 'Bearer',
      username: 'test',
      firstName: 'Test',
      lastName: 'User',
    },
  };

  const mockSessionApiService = {
    all: jest.fn().mockReturnValue(of(mockSessions)),
  };

  beforeEach(async () => {
    jest.clearAllMocks();
    mockSessionApiService.all.mockReturnValue(of(mockSessions));

    await TestBed.configureTestingModule({
      imports: [ListComponent],
      providers: [
        provideRouter([]),
        provideNoopAnimations(),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('sessions$', () => {
    it('should fetch all sessions via SessionApiService', (done) => {
      component.sessions$.subscribe((sessions) => {
        expect(sessions).toEqual(mockSessions);
        expect(sessions.length).toBe(2);
        done();
      });
      expect(mockSessionApiService.all).toHaveBeenCalled();
    });
  });

  describe('user getter', () => {
    it('should return the session information from SessionService', () => {
      expect(component.user).toEqual(mockSessionService.sessionInformation);
    });
  });
});
