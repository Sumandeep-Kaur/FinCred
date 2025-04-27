package com.fincred.backend.dto;

import jakarta.validation.constraints.*;

public class LoanApprovalRequestDto {
	@NotBlank(message = "Applicant ID is required")
    private String applicantId;

    @Min(10000)
    private double annualIncome;

    private double selfReportedDebt;

    private double selfReportedExpenses;

    @Min(1000)
    private double requestedAmount;

    @Min(19)
    @Max(99)
    private int age;

    @NotBlank
    private String province;

    @NotBlank
    private String employmentStatus;

    @Min(0)
    private int monthsEmployed;

    @Min(300)
    @Max(900)
    private double creditScore; // Given by user

    private double totalCreditLimit;

    private double creditUtilization;

    private int numOpenAccounts;

    private int numCreditInquiries;

    @NotBlank
    private String paymentHistory;

    private double monthlyExpenses;

    private double estimatedDebt;

    private double totalMonthlyDebt;

    private double dti;


    // Getters and Setters
    public String getApplicantId() { return applicantId; }
    public void setApplicantId(String applicantId) { this.applicantId = applicantId; }
    
    public double getAnnualIncome() { return annualIncome; }
    public void setAnnualIncome(double annualIncome) { this.annualIncome = annualIncome; }
    
    public double getSelfReportedDebt() { return selfReportedDebt; }
    public void setSelfReportedDebt(double selfReportedDebt) { this.selfReportedDebt = selfReportedDebt; }
    
    public double getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(double requestedAmount) { this.requestedAmount = requestedAmount; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    
    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }
    
    public int getMonthsEmployed() { return monthsEmployed; }
    public void setMonthsEmployed(int monthsEmployed) { this.monthsEmployed = monthsEmployed; }
    
    public double getCreditScore() { return creditScore; }
    public void setCreditScore(double creditScore) { this.creditScore = creditScore; }
    
    public double getTotalCreditLimit() { return totalCreditLimit; }
    public void setTotalCreditLimit(double totalCreditLimit) { this.totalCreditLimit = totalCreditLimit; }
    
    public double getCreditUtilization() { return creditUtilization; }
    public void setCreditUtilization(double creditUtilization) { this.creditUtilization = creditUtilization; }
    
    public int getNumOpenAccounts() { return numOpenAccounts; }
    public void setNumOpenAccounts(int numOpenAccounts) { this.numOpenAccounts = numOpenAccounts; }
    
    public int getNumCreditInquiries() { return numCreditInquiries; }
    public void setNumCreditInquiries(int numCreditInquiries) { this.numCreditInquiries = numCreditInquiries; }
    
    public String getPaymentHistory() { return paymentHistory; }
    public void setPaymentHistory(String paymentHistory) { this.paymentHistory = paymentHistory; }
    
    public double getEstimatedDebt() { return estimatedDebt; }
    public void setEstimatedDebt(double estimatedDebt) { this.estimatedDebt = estimatedDebt; }
    
    public double getTotalMonthlyDebt() { return totalMonthlyDebt; }
    public void setTotalMonthlyDebt(double totalMonthlyDebt) { this.totalMonthlyDebt = totalMonthlyDebt; }
    
	public double getSelfReportedExpenses() {
		return selfReportedExpenses;
	}
	public void setSelfReportedExpenses(double selfReportedExpenses) {
		this.selfReportedExpenses = selfReportedExpenses;
	}
	public double getMonthlyExpenses() {
		return monthlyExpenses;
	}
	public void setMonthlyExpenses(double monthlyExpenses) {
		this.monthlyExpenses = monthlyExpenses;
	}
	public double getDti() {
		return dti;
	}
	public void setDti(double dti) {
		this.dti = dti;
	}
}
