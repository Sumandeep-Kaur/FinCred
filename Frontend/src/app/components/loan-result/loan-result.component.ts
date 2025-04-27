import { NgClass, NgFor, NgIf, PercentPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { HeaderComponent } from "../header/header.component";

@Component({
  selector: 'app-loan-result',
  standalone: true,
  imports: [NgIf, NgFor, RouterLink, PercentPipe, NgClass],
  templateUrl: './loan-result.component.html',
  styleUrl: './loan-result.component.css'
})
export class LoanResultComponent implements OnInit {
  loanResponse: any = null;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loanResponse = history.state;

    if (!this.loanResponse || Object.keys(this.loanResponse).length === 0) {
      this.router.navigate(['/apply']);
    } else {
      console.log('Loan response:', this.loanResponse);
    }
  }
}
