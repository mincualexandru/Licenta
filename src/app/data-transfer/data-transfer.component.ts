import { Component, OnInit } from '@angular/core';
import { Device } from '../model/device.model';
import { User } from '../model/user.model';
import { AuthService } from '../services/auth.service';
import { DataTransferService } from '../services/data-transfer.service';
@Component({
    selector: 'data-transfer',  
    templateUrl: './data-transfer.component.html',  
    styleUrls: ['./data-transfer.component.css']  
})
export class DataTransferComponent implements OnInit{
    
    devices: Device[];
    user: User;

    constructor(private shared: AuthService,private dataTransferService: DataTransferService) {
        shared._user.subscribe((r) => {
          this.user = r;
        });
    }

    ngOnInit(): void {
        if(this.user.role == 'ROLE_USER') {
          this.getDevicesFromUser(this.user.username);
        }
    }

    getDevicesFromUser(username: string): void {
        this.dataTransferService.getDevicesFromUser(username)
          .subscribe((deviceData) => {
            this.devices = deviceData;
          }, (error) => {
            console.log(error);
          });
      }
}
