import { Component, OnInit } from '@angular/core';
import {HostService} from '../services/host.service';
import { AuthService } from '../services/auth.service';
import { User } from '../models/user.model';
@Component({
  selector: 'app-success-upload',
  templateUrl: './success-upload.component.html',
  styleUrls: ['./success-upload.component.css']
})
export class SuccessUploadComponent implements OnInit {

  hostName: string;
  user: User;

  constructor(private hostService: HostService, private authService: AuthService) { }

  ngOnInit() {
    this.hostName = this.hostService.getHostname();
    this.authService.user.subscribe((r) => this.user = r);
  }
}
