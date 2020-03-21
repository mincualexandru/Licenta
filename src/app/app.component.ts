import { Component, OnInit } from '@angular/core';
import { LoggingService } from './logging.service';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  
  constructor(private authService: AuthService, private loggingService: LoggingService){

  }
  ngOnInit() {
    //this.authService.autoLogin();
    this.loggingService.printLog('Hello from AppComponent ngOnInit');
  }
}
