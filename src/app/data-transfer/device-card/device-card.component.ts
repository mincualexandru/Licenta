import { Component, OnInit, Input } from '@angular/core';
import { Device } from '../../model/device.model'
@Component({
  selector: 'app-device-card',
  templateUrl: './device-card.component.html',
  styleUrls: ['./device-card.component.css']
})
export class DeviceCardComponent implements OnInit {

  @Input() device: Device;
  image: string;

  constructor() { }

  ngOnInit() {
    if(this.device.name === 'Bratara') {
      this.image = '/assets/img/band.jpg';
    } else if(this.device.name === 'Cantar Inteligent') {
      this.image = '/assets/img/scale.jpg';
    }
  }

}
