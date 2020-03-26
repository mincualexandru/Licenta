import { Component, EventEmitter, Output, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { AuthService } from '../services/auth.service';

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
                this.isAuthenticated = !user ? false : true;
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