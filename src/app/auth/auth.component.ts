import { Component, ComponentFactoryResolver, ViewChild, OnDestroy } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Observable, Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { AlertComponent } from '../shared/alert/alert.component';
import { PlaceHolderDirective } from '../shared/placeholder.directive';
import { AuthService, AuthResponseData } from '../services/auth.service';

@Component({
    selector: 'app-auth',
    templateUrl: './auth.component.html'
})
export class AuthComponent implements OnDestroy{

    @ViewChild(PlaceHolderDirective, {static: false}) alertHost: PlaceHolderDirective;
    isLoginMode = true;
    isLoading = false;
    error:string = null;
    private closeSub: Subscription

    constructor(private authSerivce: AuthService, private router: Router, private componentFactoryResolver: ComponentFactoryResolver) {}

    onSwitchMode() {
        this.isLoginMode = !this.isLoginMode;
    }

    onSubmit(form: NgForm) {
        if(!form.valid){
            return;
        }
        const userName = form.value.username;
        const password = form.value.password;

        let authObs: Observable<AuthResponseData>;

        this.isLoading = true;
        if(this.isLoginMode){
            authObs = this.authSerivce.login(userName);
        }

        authObs.subscribe(responseData => {
            this.isLoading = false;
            this.router.navigate(['/data-transfer']);
        }, 
        errorMessage => {
            this.error = errorMessage;
            this.showErrorAlert(errorMessage);
            this.isLoading = false;
        });

        form.reset();
    }

    onHandleError() {
        this.error = null;
    }

    private showErrorAlert(message: string) {
       const alertComponentFactory =  this.componentFactoryResolver.resolveComponentFactory(AlertComponent);
       const hostViewContainerRef = this.alertHost.viewContainerRef;
       hostViewContainerRef.clear();

       const componentRef = hostViewContainerRef.createComponent(alertComponentFactory);
       componentRef.instance.message = message;
       this.closeSub = componentRef.instance.close.subscribe(() => {
            this.closeSub.unsubscribe();
            hostViewContainerRef.clear();
       });
    }

    ngOnDestroy() {
        if(this.closeSub){
            this.closeSub.unsubscribe();
        }
    }
}