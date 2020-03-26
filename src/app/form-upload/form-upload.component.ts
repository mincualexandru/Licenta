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

const differentFile = "Fisierul pe care il incarci trebuie sa aiba denumirea 'export.zip'";
const errorFile = "A aparut o eroare ! Fisierul nu a fost incarcat ! ";
@Component({
  selector: "form-upload",
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
  alreadyTransferred: boolean;
  userName: string;
  errorMessageFromServer: string;
  message: string;

  constructor(
    private uploadService: UploadFileService,
    private shared: AuthService
  ) {
    shared._user.subscribe((r) => {
      this.userName = r.username;
    });
  }

  ngOnInit() {
  }

  selectFile(event) {
    this.selectedFiles = event.target.files;
    this.selectedFile = this.selectedFiles.item(0);
    this.verifyTheTypeOfFile();
    this.alreadyTransferred = false;
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
      .pushFileToStorage(this.currentFileUpload, this.userName)
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
        } else if (event.body === differentFile) {
          this.message = differentFile;
        } else if(event.body === errorFile) {
          this.message = errorFile;
        }
      });

    this.selectedFiles = undefined;
  }
}
