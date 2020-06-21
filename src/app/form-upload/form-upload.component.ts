import { Component, OnInit } from "@angular/core";
import {
  HttpResponse,
  HttpEventType,
  HttpErrorResponse
} from "@angular/common/http";
import { of } from "rxjs/internal/observable/of";
import { catchError, map } from "rxjs/operators";
import { UploadFileService } from "../services/upload-file.service";
import { AuthService } from "../services/auth.service";
import { User } from "../models/user.model";

@Component({
  selector: "app-form-upload",
  templateUrl: "./form-upload.component.html",
  styleUrls: ["./form-upload.component.css"]
})
export class FormUploadComponent implements OnInit {
  selectedFiles: FileList;
  currentFileUpload: File;
  progress: { percentage: number } = { percentage: 0 };
  selectedFile: File;
  ZIP_TYPE = ".zip";
  OTHER_TYPE = "Fisierul este de alt tip";
  typeOfFile: string;
  errorMessageFromServer: string;
  message: string;
  user: User;

  constructor(
    private uploadService: UploadFileService,
    private shared: AuthService
  ) {
    shared.user.subscribe((r) => {
      this.user = r;
    });
  }

  ngOnInit() {
  }

  selectFile(event) {
    this.selectedFiles = event.target.files;
    this.selectedFile = this.selectedFiles.item(0);
    this.verifyTheTypeOfFile();
  }

  private verifyTheTypeOfFile() {
    if (this.selectedFile.name.includes(this.ZIP_TYPE)) {
      this.typeOfFile = "Fisierul este de tipul arhiva zip";
    } else {
      this.typeOfFile = this.OTHER_TYPE;
    }
  }

  upload() {
    this.progress.percentage = 0;
    this.currentFileUpload = this.selectedFiles.item(0);
    this.uploadService
      .pushFileToStorage(this.currentFileUpload, this.user.username)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status === 417) {
            this.errorMessageFromServer = error.message;
          }
          return of(`${this.currentFileUpload.name} upload failed.`);
        })
      )
      .subscribe((event: any) => {
        if (event.type === HttpEventType.UploadProgress) {
          this.progress.percentage = Math.round(
            (100 * event.loaded) / event.total
          );
          this.message = "Ai incarcat cu succes fisierul !";
          setTimeout(() => this.uploadService.transferMessage("success"), 1500);
        }
      });

    this.selectedFiles = undefined;
  }
}
