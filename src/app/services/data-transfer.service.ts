import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders} from '@angular/common/http'
import { Observable } from 'rxjs';
import { map } from  'rxjs/operators';
import { Device } from '../models/device.model';
@Injectable({
    providedIn: 'root'
})
export class DataTransferService {
    
    devices: Device[] = new Array<Device>();

    //SERVER_URL: string = "https://172.20.10.2:8443/";

    SERVER_URL: string = "https://localhost:8443/";

    constructor(private _httpService: HttpClient){
    }

    getDevicesFromUser(username: string): Observable<Device[]> {
        return this._httpService.post<Device[]>(`${this.SERVER_URL}${"devices_from_user"}`,username);
    }

    getDevice(deviceId: number) {
        return this._httpService.get<Device>(`${this.SERVER_URL}${"get_device/"}`+deviceId);
    }

    numberOfMeasurements(accountId: number, deviceId: number) {
        return this._httpService.get<number>(`${this.SERVER_URL}${"get_number_of_measurements/"}`+accountId+"/"+deviceId);
    }

    getMeasurementType() {
        return this._httpService.get<any[]>(`${this.SERVER_URL}${"get_types_of_measurements"}`);
    }
}