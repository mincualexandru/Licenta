import { Component, OnInit } from '@angular/core';
import { Device } from '../models/device.model';
import { User } from '../models/user.model';
import { AuthService } from '../services/auth.service';
import { DataTransferService } from '../services/data-transfer.service';
import { UploadFileService } from '../services/upload-file.service';
import { HostService } from '../services/host.service';
@Component({
  selector: 'app-data-transfer',
  templateUrl: './data-transfer.component.html',
  styleUrls: ['./data-transfer.component.css']
})
export class DataTransferComponent implements OnInit {

  devices: Device[];
  user: User;
  deviceExist: boolean;
  success: boolean;
  hostName: string;

  constructor(private hostService: HostService, private authService: AuthService, private dataTransferService: DataTransferService, private uploadService: UploadFileService) {
    authService.user.subscribe((r) => {
      this.user = r;
    });
  }

  ngOnInit(): void {
    if (this.user.role == 'ROLE_USER') {
      this.getDevicesFromUser(this.user.username);
    }
    this.uploadService.currentApprovalMessage.subscribe(message => this.success = message == 'success' ? true : false);
    this.hostName = this.hostService.getHostname();
  }

  getDevicesFromUser(username: string): void {
    this.dataTransferService.getDevicesFromUser(username)
      .subscribe((deviceData) => {
        this.devices = deviceData;
        let count = 0;
        for (const iterator of this.devices) {
          if (iterator.name === 'Bratara' || iterator.name === 'Cantar Inteligent') {
            count++;
          }
        }
        if (count >= 1) {
          this.deviceExist = true;
        } else {
          this.deviceExist = false;
        }
      }, (error) => {
        console.log(error);
      });
  }

  onLogout() {
    this.authService.logout();
  }
}
