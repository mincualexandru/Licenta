import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../model/user.model';

@Injectable({
  providedIn: 'root'
})
export class UploadFileService {

  //SERVER_URL: string = "http://172.20.10.2:8080/";

  SERVER_URL: string = "http://localhost:8080/";

  constructor(private http: HttpClient) { }

  pushFileToStorage(file: File, userName: string): Observable<HttpEvent<{}>> {
    const formdata: FormData = new FormData();

    formdata.append('file', file);
    formdata.append('userName', userName);

    const req = new HttpRequest('POST', `${this.SERVER_URL}${'post'}`, formdata, {
      reportProgress: true,
      responseType: 'text'
    });

    return this.http.request(req);
  }
}
