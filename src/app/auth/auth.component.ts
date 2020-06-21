import { Component, ComponentFactoryResolver, ViewChild, OnDestroy } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Observable, Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService, AuthResponseData } from '../services/auth.service';
import { ModalService } from '../services/modal.service';

@Component({
    selector: 'app-auth',
    templateUrl: './auth.component.html',
    styleUrls: ['./auth.component.css']
})
export class AuthComponent{
    isLoading = false;
    error:string = null;

    constructor(private modalService: ModalService, private authSerivce: AuthService, private router: Router, private componentFactoryResolver: ComponentFactoryResolver) {}

    onSubmit(form: NgForm) {
        if(!form.valid){
            return;
        }
        const userName = form.value.username;
        const password = form.value.password;

        let authObs: Observable<AuthResponseData>;

        this.isLoading = true;
        authObs = this.authSerivce.login(userName,password);
        
        authObs
        .subscribe(responseData => {
            this.isLoading = false;
            this.router.navigate(['/data-transfer']);
        }, 
        errorMessage => {
            switch (errorMessage.status) {
                case 0:
                    this.error = "Conexiune refuzata";
                    break;
                case 408:
                    this.error = "Timp expirat";
                    break;
                default:
                    this.error = "Cont inexistent";
                    break;
            }
            console.log(errorMessage);
            this.modalService.openErrorModal(this.error);
            this.isLoading = false;
        });

        form.reset();
    }

    onHandleError() {
        this.error = null;
    }
}