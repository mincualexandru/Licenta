import { Component } from '@angular/core';
import { AuthService } from '../auth/auth.service';

@Component({
    selector: 'data-transfer',  
    templateUrl: './data-transfer.component.html',  
    styleUrls: ['./data-transfer.component.css']  
})
export class DataTransferComponent {
    // sub1;
    // constructor(private shared: AuthService) {
    //     shared._value
    //     .subscribe(r => {
    //         this.sub1 = r.userId;
    //         console.log("Id-ul utilizatorului conectat este" +r.userId);
    //         console.log("Numele utilizatorului conectat este" +r.userName);
    //         console.log("Parola utilizatorului conectat este" +r.encrytedPassword);
    //     }); // subscribe here
    //   }
}
