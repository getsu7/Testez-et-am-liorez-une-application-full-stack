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
import { FormComponent } from './form.component';

const mockSession: Session = {
  id: 1,
  name: 'Yoga Session',
  description: 'Morning yoga',
  date: new Date('2024-06-01'),
  teacher_id: 2,
  users: [],
};

const mockTeacher: Teacher = {
  id: 2,
  lastName: 'Smith',
  firstName: 'John',
  createdAt: new Date('2023-01-01'),
  updatedAt: new Date('2023-06-01'),
};

const mockActivatedRoute = {
  snapshot: {
    paramMap: convertToParamMap({ id: '1' }),
  },
};

const mockSessionService: Partial<SessionService> = {
  sessionInformation: {
    admin: true, id: 1, token: 'test-token', type: 'Bearer',
    username: 'test', firstName: 'Test', lastName: 'User',
  },
};

const mockSessionApiService = {
  detail: jest.fn().mockReturnValue(of(mockSession)),
  create: jest.fn().mockReturnValue(of(mockSession)),
  update: jest.fn().mockReturnValue(of(mockSession)),
};

const mockTeacherService = {
  all: jest.fn().mockReturnValue(of([mockTeacher])),
};

describe('FormComponent — create mode', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let router: Router;



  beforeEach(async () => {
    jest.clearAllMocks();
    mockSessionApiService.create.mockReturnValue(of(mockSession));
    mockTeacherService.all.mockReturnValue(of([mockTeacher]));

    await TestBed.configureTestingModule({
      imports: [FormComponent],
      providers: [
        provideRouter([
          { path: 'sessions/create', component: FormComponent },
          { path: 'sessions', component: FormComponent },
        ]),
        provideNoopAnimations(),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize an empty form in create mode', () => {
    expect(component.onUpdate).toBe(false);
    expect(component.sessionForm).toBeDefined();
    expect(component.sessionForm?.value.name).toBe('');
  });

  it('should load the list of teachers', () => {
    expect(mockTeacherService.all).toHaveBeenCalled();
  });

  it('should call sessionApiService.create and navigate to sessions on submit', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    component.sessionForm?.setValue({
      name: 'New Session',
      date: '2024-06-01',
      teacher_id: 2,
      description: 'A new yoga session',
    });
    component.submit();
    expect(mockSessionApiService.create).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
  });
});

describe('FormComponent — update mode', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let router: Router;

  beforeEach(async () => {
    jest.clearAllMocks();
    mockSessionApiService.detail.mockReturnValue(of(mockSession));
    mockSessionApiService.update.mockReturnValue(of(mockSession));
    mockTeacherService.all.mockReturnValue(of([mockTeacher]));

    await TestBed.configureTestingModule({
      imports: [FormComponent],
      providers: [
        provideRouter([
          { path: 'sessions/update/:id', component: FormComponent },
          { path: 'sessions', component: FormComponent },
        ]),
        provideNoopAnimations(),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);

    // Simuler une URL contenant 'update' AVANT detectChanges (= avant ngOnInit)
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');

    fixture.detectChanges();
  });

  it('should set onUpdate to true when URL contains "update"', () => {
    expect(component.onUpdate).toBe(true);
  });

  it('should call sessionApiService.detail with the route id', () => {
    expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
  });

  it('should initialize the form with the session data', () => {
    expect(component.sessionForm?.value.name).toBe(mockSession.name);
    expect(component.sessionForm?.value.description).toBe(mockSession.description);
    expect(component.sessionForm?.value.teacher_id).toBe(mockSession.teacher_id);
  });

  it('should call sessionApiService.update and navigate to sessions on submit', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    component.sessionForm?.setValue({
      name: 'Updated Session',
      date: '2024-06-01',
      teacher_id: 2,
      description: 'Updated description',
    });
    component.submit();
    expect(mockSessionApiService.update).toHaveBeenCalledWith('1', expect.any(Object));
    expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
  });
});

describe('FormComponent — non-admin user', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let router: Router;

  const nonAdminSessionService: Partial<SessionService> = {
    sessionInformation: {
      admin: false, id: 2, token: 'test-token', type: 'Bearer',
      username: 'user', firstName: 'Regular', lastName: 'User',
    },
  };

  const mockSessionApiService = {
    detail: jest.fn().mockReturnValue(of(mockSession)),
    create: jest.fn().mockReturnValue(of(mockSession)),
    update: jest.fn().mockReturnValue(of(mockSession)),
  };

  const mockTeacherService = {
    all: jest.fn().mockReturnValue(of([mockTeacher])),
  };

  beforeEach(async () => {
    jest.clearAllMocks();
    mockTeacherService.all.mockReturnValue(of([mockTeacher]));

    await TestBed.configureTestingModule({
      imports: [FormComponent],
      providers: [
        provideRouter([
          { path: 'sessions/create', component: FormComponent },
          { path: 'sessions', component: FormComponent },
        ]),
        provideNoopAnimations(),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: SessionService, useValue: nonAdminSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should redirect to /sessions when user is not admin', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    component.ngOnInit();
    expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
  });
});

describe('FormComponent — ngOnDestroy', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  beforeEach(async () => {
    jest.clearAllMocks();
    mockTeacherService.all.mockReturnValue(of([mockTeacher]));

    await TestBed.configureTestingModule({
      imports: [FormComponent],
      providers: [
        provideRouter([{ path: 'sessions', component: FormComponent }]),
        provideNoopAnimations(),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should complete the destroy$ subject on ngOnDestroy', () => {
    const nextSpy = jest.spyOn((component as any).destroy$, 'next');
    const completeSpy = jest.spyOn((component as any).destroy$, 'complete');
    component.ngOnDestroy();
    expect(nextSpy).toHaveBeenCalled();
    expect(completeSpy).toHaveBeenCalled();
  });
});
