import { expect } from '@jest/globals';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { provideRouter } from '@angular/router';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { User } from '../../core/models/user.interface';
import { SessionService } from '../../core/service/session.service';
import { UserService } from '../../core/service/user.service';
import { MeComponent } from './me.component';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let router: Router;

  const mockUser: User = {
    id: 1,
    email: 'test@test.com',
    lastName: 'Doe',
    firstName: 'Jane',
    admin: false,
    password: 'secret',
    createdAt: new Date('2023-01-01'),
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
    logOut: jest.fn(),
  };

  const mockUserService = {
    getById: jest.fn().mockReturnValue(of(mockUser)),
    delete: jest.fn().mockReturnValue(of(null)),
  };

  beforeEach(async () => {
    jest.clearAllMocks();
    mockUserService.getById.mockReturnValue(of(mockUser));
    mockUserService.delete.mockReturnValue(of(null));

    await TestBed.configureTestingModule({
      imports: [MeComponent],
      providers: [
        provideRouter([]),
        provideNoopAnimations(),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit()', () => {
    it('should load user data on init', () => {
      expect(mockUserService.getById).toHaveBeenCalledWith('1');
      expect(component.user).toEqual(mockUser);
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
    it('should delete the user account, log out, and navigate to root', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      component.delete();
      expect(mockUserService.delete).toHaveBeenCalledWith('1');
      expect(mockSessionService.logOut).toHaveBeenCalled();
      expect(navigateSpy).toHaveBeenCalledWith(['/']);
    });
  });
});
