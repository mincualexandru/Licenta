import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UploadFileService {

  //SERVER_URL: string = "https://172.20.10.2:8443/";

  SERVER_URL: string = "https://localhost:8443/";

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
