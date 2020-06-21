import { Component, OnInit, Input } from "@angular/core";
import { Device } from "../../models/device.model";
import { User } from "../../models/user.model";
import { Router } from "@angular/router";
import { DataTransferService } from "../../services/data-transfer.service";
import { ThemePalette } from '@angular/material/core';
import { ProgressSpinnerMode } from '@angular/material/progress-spinner';
@Component({
  selector: "app-device-card-item",
  templateUrl: "./device-card-item.component.html",
  styleUrls: ["./device-card-item.component.css"]
})
export class DeviceCardItemComponent implements OnInit {
  @Input() device: Device;
  @Input() user: User;
  image: string;
  numberOfMeasurements: number;
  measurementTypesForFitBuddy = new Array<String>();
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
    } else if(this.device.name === 'Fit Buddy') {
      this.image = "/assets/img/4e56fc0b-28d1-4bd4-98c6-697e5a8e4721_rw_1200.gif";
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
        this.measurementTypesForBand.push("Pasii Parcursi");
        break;
      case "HKCategoryTypeIdentifierSleepAnalysis":
        this.measurementTypesForBand.push("Analiza Somn");
        break;
      case "HKQuantityTypeIdentifierActiveEnergyBurned":
        this.measurementTypesForBand.push("Energie Activa Arsa");
        this.measurementTypesForFitBuddy.push("Energie Activa Arsa");
        break;
      case "HKQuantityTypeIdentifierActiveEnergyAccumulated":
        this.measurementTypesForFitBuddy.push("Energie Activa Acumulata");
        break;
      default:
        break;
    }
  }
}
