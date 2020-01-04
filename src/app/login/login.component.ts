import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../model/user.model';
import { UploadFileService } from '../upload/upload-file.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  //user: User = new User();

  constructor(private router: Router, private uploadService: UploadFileService) {

  }

//   loginUser(): void {
//     this.uploadService.loginUser(this.user)
//         .subscribe( data => {
//           alert("User Logged in successfully.");
//         });

//   };
  ngOnInit() {
  }

}
