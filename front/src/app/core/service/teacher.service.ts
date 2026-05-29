import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Teacher } from '../models/teacher.interface';

@Injectable({
  providedIn: 'root'
})
export class TeacherService {

  private readonly httpClient = inject(HttpClient);
  private readonly pathService = 'api/teacher';


  public all(): Observable<Teacher[]> {
    return this.httpClient.get<Teacher[]>(this.pathService);
  }

  public detail(id: string): Observable<Teacher> {
    return this.httpClient.get<Teacher>(`${this.pathService}/${id}`);
  }
}
