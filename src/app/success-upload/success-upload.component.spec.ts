import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SuccessUploadComponent } from './success-upload.component';

describe('SuccessUploadComponent', () => {
  let component: SuccessUploadComponent;
  let fixture: ComponentFixture<SuccessUploadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SuccessUploadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SuccessUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
