import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { User } from '../../core/models/user.interface';
import { SessionService } from '../../core/service/session.service';
import { UserService } from '../../core/service/user.service';
import { MaterialModule } from "../../shared/material.module";
import { CommonModule } from "@angular/common";
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-me',
  imports: [CommonModule, MaterialModule],
  templateUrl: './me.component.html',
  styleUrls: ['./me.component.scss']
})
export class MeComponent implements OnInit, OnDestroy {
  private readonly router = inject(Router);
  private readonly sessionService = inject(SessionService);
  private readonly matSnackBar = inject(MatSnackBar);
  private readonly userService = inject(UserService);
  public user: User | undefined;

  private readonly destroy$ = new Subject<void>();

  ngOnInit(): void {
    this.userService
      .getById(this.sessionService.sessionInformation!.id.toString())
      .pipe(takeUntil(this.destroy$))
      .subscribe((user: User) => this.user = user);
  }

  public back(): void {
    globalThis.history.back();
  }

   public delete(): void {
     this.userService
       .delete(this.sessionService.sessionInformation!.id.toString())
       .pipe(takeUntil(this.destroy$))
       .subscribe(() => {
         this.matSnackBar.open("Your account has been deleted !", 'Close', { duration: 3000 });
         this.sessionService.logOut();
         this.router.navigate(['/']);
       })
   }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

}
