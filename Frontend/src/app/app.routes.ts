import { Routes } from '@angular/router';
import { LoanApplicationFormComponent } from './components/loan-application-form/loan-application-form.component';
import { LoanResultComponent } from './components/loan-result/loan-result.component';

export const routes: Routes = [
    { path: '', component: LoanApplicationFormComponent },
    { path: 'result', component: LoanResultComponent }
];
