package com.fincred.backend.dto;

import java.util.List;

public class LoanApprovalResponseDto {
	private boolean isApproved;
    private double approvedAmount;
    private Double interestRate;
    private List<String> denialReasons;

    private double predictedCreditScore;
    private double approvalProbability;

    // constructor
    public LoanApprovalResponseDto(boolean isApproved, double approvedAmount, Double interestRate, List<String> denialReasons,
                                   double predictedCreditScore, double approvalProbability) {
        this.setApproved(isApproved);
        this.setApprovedAmount(approvedAmount);
        this.setInterestRate(interestRate);
        this.setDenialReasons(denialReasons);
        this.setPredictedCreditScore(predictedCreditScore);
        this.setApprovalProbability(approvalProbability);
    }

	public boolean isApproved() {
		return isApproved;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public double getApprovedAmount() {
		return approvedAmount;
	}

	public void setApprovedAmount(double approvedAmount) {
		this.approvedAmount = approvedAmount;
	}

	public Double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	public double getPredictedCreditScore() {
		return predictedCreditScore;
	}

	public void setPredictedCreditScore(double predictedCreditScore) {
		this.predictedCreditScore = predictedCreditScore;
	}

	public double getApprovalProbability() {
		return approvalProbability;
	}

	public void setApprovalProbability(double approvalProbability) {
		this.approvalProbability = approvalProbability;
	}

	public List<String> getDenialReasons() {
		return denialReasons;
	}

	public void setDenialReasons(List<String> denialReasons) {
		this.denialReasons = denialReasons;
	}
}
