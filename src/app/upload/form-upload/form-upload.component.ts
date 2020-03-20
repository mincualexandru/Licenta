import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpEventType, HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs/internal/observable/of';
import { catchError, map } from 'rxjs/operators'; 
import { UploadFileService } from '../upload-file.service';
import { User } from 'src/app/model/user.model';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'form-upload',
  templateUrl: './form-upload.component.html',
  styleUrls: ['./form-upload.component.css']
})
export class FormUploadComponent implements OnInit {

  selectedFiles: FileList;
  currentFileUpload: File;
  progress: { percentage: number } = { percentage: 0 };
  selectedFile: File;
  ZIP_TYPE = ".zip";
  RAR_TYPE = ".rar";
  ZIP7_TYPE = ".7zip";
  OTHER_TYPE = "Fisierul este de alt tip";
  typeOfFile: string;
  alreadyTransferred: boolean;
  userName: string;

  constructor(private uploadService: UploadFileService, private shared: AuthService) { 
    shared._value
        .subscribe(r => {
            this.userName = r.username;
        });
  }

  ngOnInit() {
  }

  selectFile(event) {
    this.selectedFiles = event.target.files;
    console.log(event.target.files);
    this.selectedFile = this.selectedFiles.item(0);
    console.log(this.selectedFile.name);
    this.verifyTheTypeOfFile();
    this.alreadyTransferred = false;
  }

  private verifyTheTypeOfFile() {
    if(this.selectedFile.name.includes(this.RAR_TYPE)) {
      this.typeOfFile = 'Fisierul este de tipul arhiva rar';
    } else if(this.selectedFile.name.includes(this.ZIP_TYPE)) {
      this.typeOfFile = 'Fisierul este de tipul arhiva zip';
    } else if(this.selectedFile.name.includes(this.ZIP7_TYPE)) {
      this.typeOfFile = 'Fisierul este de tipul arhiva 7zip';
    } else {
      this.typeOfFile = this.OTHER_TYPE;
    }
  }

  upload() {
    console.log("Numele utilizatorului conectat este " + this.userName);
    this.progress.percentage = 0;
    console.log('A INTRAT AICI');
    this.currentFileUpload = this.selectedFiles.item(0);
    console.log(this.currentFileUpload);
    console.log(this.currentFileUpload.name);
    this.uploadService.pushFileToStorage(this.currentFileUpload, this.userName).pipe(
      catchError((error: HttpErrorResponse) => {
        console.log('A INTRAT AICI 2');
        console.log(error.message);  
        console.log(error.status);
        console.log(error);
        if(error.status === 417) {
          this.alreadyTransferred = true;
        }
        console.log(error.type);
        console.log(error.url);
        return of(`${this.currentFileUpload.name} upload failed.`);  
      }
    )).subscribe((event: any) => {
      console.log(event.type);
      if (event.type === HttpEventType.UploadProgress) {
        this.progress.percentage = Math.round(100 * event.loaded / event.total);
      } else if (event instanceof HttpResponse) {
        console.log('File is completely uploaded!');
      }
    });

    this.selectedFiles = undefined;
  }

}
