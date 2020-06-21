import { Component, OnInit } from "@angular/core";
import { AuthService } from "../services/auth.service";
import { User } from "../models/user.model";
import { RouterStateSnapshot, Router } from "@angular/router";
@Component({
  selector: "app-not-found",
  templateUrl: "./not-found.component.html",
  styleUrls: ["./not-found.component.css"],
})
export class NotFoundComponent implements OnInit {
  user: User;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.authService.user.subscribe((r) => {
      this.user = r;
    });

    this.user
      ? setTimeout(() => this.router.navigate(["/data-transfer"]), 5000)
      : setTimeout(() => this.router.navigate(["/auth"]), 2500);
  }
}
