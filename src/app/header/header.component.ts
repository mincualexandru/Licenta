import { Component, EventEmitter, Output, OnInit, OnDestroy } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: []
})
export class HeaderComponent implements OnInit, OnDestroy{

    isAuthenticated: boolean = false;

    private userSub: Subscription

    constructor(private authService: AuthService) {}

    ngOnInit() {
        this.userSub = this.authService.user.subscribe(
            user => {
                /*Daca nu exista utilizatorul atunci this.isAuthenticated este false, altfel true*/
                this.isAuthenticated = !user ? false : true;
                // sau
                // this.isAuthenticated = !!user
            }
        );
    }

    onLogout() {
        this.authService.logout();
    }
    
    ngOnDestroy() {
        this.userSub.unsubscribe();
    }
}