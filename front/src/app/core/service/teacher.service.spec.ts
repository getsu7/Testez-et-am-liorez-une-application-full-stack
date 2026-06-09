import {expect} from '@jest/globals';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Teacher } from '../models/teacher.interface';
import { TeacherService } from './teacher.service';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  const mockTeacher: Teacher = {
    id: 1,
    lastName: 'Smith',
    firstName: 'John',
    createdAt: new Date('2023-01-01'),
    updatedAt: new Date('2023-06-01'),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('all()', () => {
    it('should return an array of teachers via GET', () => {
      service.all().subscribe((teachers) => {
        expect(teachers).toEqual([mockTeacher]);
        expect(teachers.length).toBe(1);
      });
      const req = httpMock.expectOne('api/teacher');
      expect(req.request.method).toBe('GET');
      req.flush([mockTeacher]);
    });
  });

  describe('detail()', () => {
    it('should return a teacher by id via GET', () => {
      service.detail('1').subscribe((teacher) => {
        expect(teacher).toEqual(mockTeacher);
      });
      const req = httpMock.expectOne('api/teacher/1');
      expect(req.request.method).toBe('GET');
      req.flush(mockTeacher);
    });
  });
});
