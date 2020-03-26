import { Component, OnInit, Input } from "@angular/core";
import { Device } from "../../models/device.model";
import { User } from "../../models/user.model";
import { Router } from "@angular/router";
import { DataTransferService } from "../../services/data-transfer.service";
import { ThemePalette } from '@angular/material/core';
import { ProgressSpinnerMode } from '@angular/material/progress-spinner';
@Component({
  selector: "app-device-card",
  templateUrl: "./device-card.component.html",
  styleUrls: ["./device-card.component.css"]
})
export class DeviceCardComponent implements OnInit {
  @Input() device: Device;
  @Input() user: User;
  image: string;
  numberOfMeasurements: number;
  measurementTypesForBand = new Array<String>();
  measurementTypesForScale = new Array<String>();
  value: number;

  constructor(
    private router: Router,
    private dataTransferService: DataTransferService
  ) { }

  ngOnInit() {
    if (this.device.name === "Bratara") {
      this.image = "/assets/img/band.jpg";
    } else if (this.device.name === "Cantar Inteligent") {
      this.image = "/assets/img/scale.jpg";
    }

    this.dataTransferService
      .numberOfMeasurements(this.user.accountId, this.device.deviceId)
      .subscribe(number => {
        this.numberOfMeasurements = number;
        this.value = ((number/2) - number/10);
      });

    this.dataTransferService.getMeasurementType().subscribe(measurementType => {
      measurementType.forEach(element => {
        this.formatArrays(element)
      });
    });
  }

  formatArrays(element: string) {
    switch (element) {
      case "HKQuantityTypeIdentifierBodyMass":
        this.measurementTypesForScale.push("Masa Corporala");
        break;
      case "HKQuantityTypeIdentifierBodyMassIndex":
        this.measurementTypesForScale.push("Index Masa Corporala");
        break;
      case "HKQuantityTypeIdentifierBodyFatPercentage":
        this.measurementTypesForScale.push("Procent Masa Corporala");
        break;
      case "HKQuantityTypeIdentifierLeanBodyMass":
        this.measurementTypesForScale.push("Masa Corporala Slaba");
        break;
      case "HKQuantityTypeIdentifierHeartRate":
        this.measurementTypesForBand.push("Puls Inima");
        break;
      case "HKQuantityTypeIdentifierStepCount":
        this.measurementTypesForBand.push("Pasii Facuti");
        break;
      case "HKCategoryTypeIdentifierSleepAnalysis":
        this.measurementTypesForBand.push("Analiza Somn");
        break;
      case "HKQuantityTypeIdentifierActiveEnergyBurned":
        this.measurementTypesForBand.push("Energie Activa Arsa");
        break;
      default:
        break;
    }
  }
}
