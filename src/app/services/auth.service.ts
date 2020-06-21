import { Injectable } from "@angular/core";
import { HttpClient, HttpErrorResponse, HttpParams, HttpHeaders } from "@angular/common/http";
import { catchError, tap } from "rxjs/operators";
import { throwError, Subject, BehaviorSubject, Observable } from "rxjs";
import { Router } from "@angular/router";
import { environment } from "../../environments/environment";
import { User } from "../models/user.model";
import { UploadFileService } from "./upload-file.service";

export interface AuthResponseData {
  accountId: number;
  username: string;
  firstName: string;
  lastName: string;
  password: string;
  email: string;
  phoneNumber: string;
  bornDate: string;
  active: boolean;
  gender: string;
  role: string
}


@Injectable({
  providedIn: "root"
})
export class AuthService {
  //SERVER_URL: string = "https://172.20.10.2:8443/";

  SERVER_URL: string = "https://localhost:8443/";

  user = new BehaviorSubject<User>(null);

  constructor(private http: HttpClient, private router: Router, private uploadService: UploadFileService) {
    this.user = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('userData')));
  }

  login(userName: string, password: string) {
    console.log("URL " + this.SERVER_URL);
    let params = new HttpParams()
    .set('userName', userName)
    .set('password', password);
    return this.http
      .post<AuthResponseData>(`${this.SERVER_URL}/${"loginResource"}`, params)
      .pipe(
        tap(respData => {
          this.handleAuthentication(
            respData.accountId,
            respData.username,
            respData.firstName,
            respData.lastName,
            respData.password,
            respData.email,
            respData.phoneNumber,
            respData.bornDate,
            respData.active,
            respData.gender,
            respData.role
          );
        }),
        catchError(this.handleError)
      );
  }

  
  logout() {
    this.router.navigate(["/auth"]);
    localStorage.clear();
    this.uploadService.approvalMessage.next(null);
    this.user.next(null);
  }

  private handleAuthentication(
    accountId: number,
    username: string,
    firstName: string,
    lastName: string,
    password: string,
    email: string,
    phoneNumber: string,
    bornDate: string,
    active: boolean,
    gender: string,
    role: string
  ) {
    const user = new User(
      accountId,
      username,
      firstName,
      lastName,
      password,
      email,
      phoneNumber,
      bornDate,
      active,
      gender,
      role
    );
    localStorage.setItem("userData", JSON.stringify(user));
    this.user.next(user);
  }

  handleError(error) {
    return throwError(error);
}
}
