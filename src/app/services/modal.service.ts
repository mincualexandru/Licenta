import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material';
import { AlertModalComponent } from '../shared/alert-modal/alert-modal.component';
@Injectable({
  providedIn: 'root'
})
export class ModalService {
  constructor(
    public dialog: MatDialog
  ) { }
  openErrorModal(error: string) {

    const dialogRef = this.dialog.open(AlertModalComponent, {
        width: '400px',
        data: {
          errorMessage: error
        }
      });

   }
}