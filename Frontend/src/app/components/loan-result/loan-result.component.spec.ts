import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoanResultComponent } from './loan-result.component';

describe('LoanResultComponent', () => {
  let component: LoanResultComponent;
  let fixture: ComponentFixture<LoanResultComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoanResultComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(LoanResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
