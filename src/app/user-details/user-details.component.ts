import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { DataTransferService } from '../services/data-transfer.service';
import { User } from '../models/user.model';

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.css']
})
export class UserDetailsComponent implements OnInit {

  user: User;

  constructor(private shared: AuthService, private dataTransferService: DataTransferService) {
    shared.user.subscribe((r) => {
      this.user = r;
    });
  }

  ngOnInit() {
  }

}
