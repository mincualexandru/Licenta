import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, tap } from 'rxjs/operators';
import { throwError, Subject, BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { User } from '../model/user.model';

export interface AuthResponseData {
    accountId: number,
    username: string,
    firstName: string,
    lastName: string,
    password: string,
    email: string,
    phoneNumber: string,
    bornDate: string,
    active: boolean,
    gender: string
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    //SERVER_URL: string = "http://172.20.10.2:8080/";

    SERVER_URL: string = "http://localhost:8080/";

    user = new BehaviorSubject<User>(null);

    _value = this.user.asObservable();

    constructor(private http: HttpClient, private router: Router){}

    login(userName: string){
        console.log(userName);
        console.log('A intrat in login service');
        return this.http
        .post<AuthResponseData>(`${this.SERVER_URL}/${'loginResource'}`, userName)
        .pipe(tap(respData => {
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
                    respData.gender
                );
            }));
    }

    logout() {
        this.user.next(null);
        this.router.navigate(['/auth']);
        localStorage.removeItem('userData');
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
        gender: string
    ) {
        console.log(accountId);
        console.log(username);
        console.log(firstName);
        console.log(lastName);
        console.log(password);
        console.log(email);
        console.log(phoneNumber);
        console.log(bornDate);
        console.log(active);
        console.log(gender);
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
                gender
            );
            this.user.next(user);
            localStorage.setItem('userData', JSON.stringify(user));
    }

}