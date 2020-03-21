import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { AppRoutingModule } from './app-routing.module';
import { SharedModule } from './shared/shared.module';
import { MyMaterialModule } from './material.module';
import { DataTransferComponent } from './data-transfer/data-transfer.component';
import { LoginComponent } from './login/login.component';
import { FormUploadComponent } from './form-upload/form-upload.component';
import { DeviceCardComponent } from './data-transfer/device-card/device-card.component';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    DataTransferComponent,
    LoginComponent,
    FormUploadComponent,
    DeviceCardComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    SharedModule,
    MyMaterialModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
