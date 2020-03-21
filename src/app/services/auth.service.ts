import { Injectable } from "@angular/core";
import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { catchError, tap } from "rxjs/operators";
import { throwError, Subject, BehaviorSubject } from "rxjs";
import { Router } from "@angular/router";
import { environment } from "../../environments/environment";
import { User } from "../model/user.model";

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
  //SERVER_URL: string = "http://172.20.10.2:8080/";

  SERVER_URL: string = "http://localhost:8080/";

  user = new BehaviorSubject<User>(null);

  _user = this.user.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  login(userName: string) {
    return this.http
      .post<AuthResponseData>(`${this.SERVER_URL}/${"loginResource"}`, userName)
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
        })
      );
  }

  logout() {
    this.user.next(null);
    this.router.navigate(["/auth"]);
    localStorage.removeItem("userData");
    localStorage.removeItem("roleData");
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
    this.user.next(user);
    localStorage.setItem("userData", JSON.stringify(user));
  }
}
