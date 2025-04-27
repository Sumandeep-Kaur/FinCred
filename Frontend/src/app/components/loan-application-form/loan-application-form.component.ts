import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgIf, NgFor } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
import { LoanService } from '../../services/loan.service';
import { HttpErrorResponse } from '@angular/common/http';
import { animate, style, transition, trigger } from '@angular/animations';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-loan-application-form',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf, NgFor],
  templateUrl: './loan-application-form.component.html',
  styleUrl: './loan-application-form.component.css'
})
export class LoanApplicationFormComponent implements OnInit {
  applicationForm!: FormGroup;
  formSubmitted = false;
  currentStep = 1;
  isSubmitting = false;
  errorMessage = '';

  private router = inject(Router);
  private loanService = inject(LoanService);
  private toastr = inject(ToastrService);
  
  provinces = [
    { code: 'AB', name: 'Alberta' },
    { code: 'BC', name: 'British Columbia' },
    { code: 'MB', name: 'Manitoba' },
    { code: 'NB', name: 'New Brunswick' },
    { code: 'NL', name: 'Newfoundland and Labrador' },
    { code: 'NS', name: 'Nova Scotia' },
    { code: 'NT', name: 'Northwest Territories' },
    { code: 'NU', name: 'Nunavut' },
    { code: 'ON', name: 'Ontario' },
    { code: 'PE', name: 'Prince Edward Island' },
    { code: 'QC', name: 'Quebec' },
    { code: 'SK', name: 'Saskatchewan' },
    { code: 'YT', name: 'Yukon' }
  ];

  constructor(
    private fb: FormBuilder
  ) { }

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.applicationForm = this.fb.group({
      // Personal Information
      age: ['', [Validators.required, Validators.min(19), Validators.max(100)]],
      province: ['', Validators.required],
      employmentStatus: ['', Validators.required],
      monthsEmployed: ['', [Validators.required, Validators.min(0), Validators.max(600)]],
      
      // Financial Information
      annualIncome: ['', [Validators.required, Validators.min(20000), Validators.max(200000)]],
      creditScore: ['', [Validators.required, Validators.min(300), Validators.max(900)]],
      selfReportedDebt: ['', [Validators.required, Validators.min(0), Validators.max(10000)]],
      selfReportedExpenses: ['', [Validators.required, Validators.min(0), Validators.max(10000)]],
      creditUtilization: ['', [Validators.required, Validators.min(0), Validators.max(100)]],
      totalCreditLimit: ['', [Validators.required, Validators.min(0), Validators.max(50000)]],
      
      // Credit Information
      numOpenAccounts: ['', [Validators.required, Validators.min(0), Validators.max(20)]],
      numCreditInquiries: ['', [Validators.required, Validators.min(0), Validators.max(10)]],
      paymentHistory: ['', Validators.required],
      monthlyExpenses: ['', [Validators.required, Validators.min(0), Validators.max(10000)]],
      requestedAmount: ['', [Validators.required, Validators.min(1000), Validators.max(50000)]],
      terms: [false, Validators.requiredTrue]
    });
  }

  get f() {
    return this.applicationForm.controls;
  }

  nextStep(): void {
    const currentControls = this.getControlsForCurrentStep();
    
    // Mark current step fields as touched
    Object.keys(currentControls).forEach(key => {
      this.applicationForm.get(key)?.markAsTouched();
    });
    
    // Check if current step is valid
    if (this.isCurrentStepValid()) {
      this.currentStep++;
      window.scrollTo(0, 0);
    }
  }

  prevStep(): void {
    this.currentStep--;
    window.scrollTo(0, 0);
  }

  isCurrentStepValid(): boolean {
    const currentControls = this.getControlsForCurrentStep();
    
    return Object.keys(currentControls).every(key => {
      return !this.applicationForm.get(key)?.invalid;
    });
  }

  getControlsForCurrentStep(): any {
    if (this.currentStep === 1) {
      return {
        age: true,
        province: true,
        employmentStatus: true,
        monthsEmployed: true
      };
    } else if (this.currentStep === 2) {
      return {
        annualIncome: true,
        creditScore: true,
        selfReportedDebt: true,
        selfReportedExpenses: true,
        creditUtilization: true,
        totalCreditLimit: true
      };
    } else {
      return {
        numOpenAccounts: true,
        numCreditInquiries: true,
        paymentHistory: true,
        monthlyExpenses: true,
        requestedAmount: true,
        terms: true
      };
    }
  }

  submitApplication() {
    if (this.applicationForm.valid) {
      this.isSubmitting = true;
  
      const applicationData = {
        ...this.applicationForm.value,
        applicant_id: 'APP' + Math.floor(Math.random() * 1000000)
      };

      const requestedAmount = this.applicationForm.value.requestedAmount || 0;
  
      setTimeout(() => {
        console.log(applicationData);
        this.loanService.applyForLoan(applicationData).subscribe({
          next: (response) => {
            this.errorMessage = '';
            console.log(response);
            this.router.navigate(['/result'], { 
              state: { 
                ...response, 
                requestedAmount: requestedAmount 
              } 
            });
            this.isSubmitting = false;
          },
          error: (err: any) => {  // ✅ Catch all errors, not just HttpErrorResponse
  
            // ✅ If it's an Error object thrown from service
            if (err instanceof Error) {
              this.errorMessage = err.message;
            }
  
            // ✅ If backend directly returns JSON error structure
            else if (typeof err.error === 'object' && err.error?.error) {
              this.errorMessage = err.error.error;
            }
  
            // ✅ Fallback
            else {
              this.errorMessage = 'Something went wrong. Please try again later.';
            }

            this.toastr.error(this.errorMessage, 'Error', {
              messageClass: 'toast-message-with-icon'
            });
  
            this.isSubmitting = false;
            window.scrollTo({ top: 0, behavior: 'smooth' });
          }
        });
      }, 2000);
    }
  } 
}