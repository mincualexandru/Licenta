import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { AppRoutingModule } from './app-routing.module';
import { SharedModule } from './shared/shared.module';
import { MyMaterialModule } from './material.module';
import { DataTransferComponent } from './data-transfer/data-transfer.component';
import { FormUploadComponent } from './upload/form-upload/form-upload.component';
import { LoginComponent } from './login/login.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FormUploadComponent,
    DataTransferComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    SharedModule,
    MyMaterialModule
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
