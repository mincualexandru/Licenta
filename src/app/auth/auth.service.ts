import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, tap } from 'rxjs/operators';
import { throwError, Subject, BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { User } from '../model/user.model';

export interface AuthResponseData {
    userId: number,
    userName: string,
    encrytedPassword: string,
    name: string,
    gender: string,
    bornDate: string,
    email: string,
    mobile: string
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    //SERVER_URL: string = "http://172.20.10.3:8080/";

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
                    respData.userId,
                    respData.userName,
                    respData.encrytedPassword,
                    respData.name,
                    respData.gender,
                    respData.bornDate,
                    respData.email,
                    respData.mobile
                );
            }));
    }

    logout() {
        this.user.next(null);
        this.router.navigate(['/auth']);
        localStorage.removeItem('userData');
    }

    private handleAuthentication(
        userId: number,
        userName: string,
        encrytedPassword: string,
        name: string,
        gender: string,
        bornDate: string,
        email: string,
        mobile: string
    ) {
        console.log(userId);
        console.log(userName);
        console.log(encrytedPassword);
        console.log(name);
        console.log(gender);
        console.log(bornDate);
        console.log(email);
        console.log(mobile);
            const user = new User(
                userId, 
                userName,
                encrytedPassword,
                name,
                gender,
                bornDate,
                email,
                mobile
            );
            this.user.next(user);
            localStorage.setItem('userData', JSON.stringify(user));
    }

}