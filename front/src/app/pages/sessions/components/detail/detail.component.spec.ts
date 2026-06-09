import {expect} from '@jest/globals';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { Session } from '../../../../core/models/session.interface';
import { Teacher } from '../../../../core/models/teacher.interface';
import { SessionService } from '../../../../core/service/session.service';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { TeacherService } from '../../../../core/service/teacher.service';
import { DetailComponent } from './detail.component';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let router: Router;

  const mockSession: Session = {
    id: 1,
    name: 'Yoga Session',
    description: 'Morning yoga',
    date: new Date('2024-06-01'),
    teacher_id: 2,
    users: [1, 3],
  };

  const mockTeacher: Teacher = {
    id: 2,
    lastName: 'Smith',
    firstName: 'John',
    createdAt: new Date('2023-01-01'),
    updatedAt: new Date('2023-06-01'),
  };

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
    detail: jest.fn().mockReturnValue(of(mockSession)),
    delete: jest.fn().mockReturnValue(of(null)),
    participate: jest.fn().mockReturnValue(of(null)),
    unParticipate: jest.fn().mockReturnValue(of(null)),
  };

  const mockTeacherService = {
    detail: jest.fn().mockReturnValue(of(mockTeacher)),
  };

  const mockActivatedRoute = {
    snapshot: {
      paramMap: convertToParamMap({ id: '1' }),
    },
  };

  beforeEach(async () => {
    jest.clearAllMocks();
    mockSessionApiService.detail.mockReturnValue(of(mockSession));
    mockTeacherService.detail.mockReturnValue(of(mockTeacher));

    await TestBed.configureTestingModule({
      imports: [DetailComponent],
      providers: [
        provideRouter([]),
        provideNoopAnimations(),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit()', () => {
    it('should fetch session data on init', () => {
      expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
      expect(component.session).toEqual(mockSession);
    });

    it('should fetch teacher data after getting session', () => {
      expect(mockTeacherService.detail).toHaveBeenCalledWith('2');
      expect(component.teacher).toEqual(mockTeacher);
    });

    it('should set isAdmin from session service', () => {
      expect(component.isAdmin).toBe(true);
    });

    it('should set isParticipate based on user presence in session', () => {
      // userId = '1', session.users = [1, 3] → isParticipate = true
      expect(component.isParticipate).toBe(true);
    });
  });

  describe('back()', () => {
    it('should call history.back()', () => {
      const historySpy = jest.spyOn(globalThis.history, 'back').mockImplementation(() => {});
      component.back();
      expect(historySpy).toHaveBeenCalled();
    });
  });

  describe('delete()', () => {
    it('should call sessionApiService.delete and navigate to sessions', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      component.delete();
      expect(mockSessionApiService.delete).toHaveBeenCalledWith('1');
      expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
    });
  });

  describe('participate()', () => {
    it('should call sessionApiService.participate and refresh session', () => {
      component.participate();
      expect(mockSessionApiService.participate).toHaveBeenCalledWith('1', '1');
      expect(mockSessionApiService.detail).toHaveBeenCalledTimes(2);
    });
  });

  describe('unParticipate()', () => {
    it('should call sessionApiService.unParticipate and refresh session', () => {
      component.unParticipate();
      expect(mockSessionApiService.unParticipate).toHaveBeenCalledWith('1', '1');
      expect(mockSessionApiService.detail).toHaveBeenCalledTimes(2);
    });
  });
});
