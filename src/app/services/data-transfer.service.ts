import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders} from '@angular/common/http'
import { Observable } from 'rxjs';
import { map } from  'rxjs/operators';
@Injectable({
    providedIn: 'root'
})
export class DataTransferService {

    //SERVER_URL: string = "http://172.20.10.2:8080/";

    SERVER_URL: string = "http://localhost:8080/";

    constructor(private _httpService: HttpClient){}

    getDevicesFromUser(username: string): Observable<any> {
        return this._httpService.post(`${this.SERVER_URL}${"devices_from_user"}`,username);
    }
}