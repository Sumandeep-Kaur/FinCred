package com.fincred.backend.dto;

public class CreditScoreErrorResponseDto {
    private String error;
    private double providedCreditScore;
    private double predictedCreditScore;

    public CreditScoreErrorResponseDto(String error, double provided, double predicted) {
        this.setError(error);
        this.setProvidedCreditScore(provided);
        this.setPredictedCreditScore(predicted);
    }

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public double getProvidedCreditScore() {
		return providedCreditScore;
	}

	public void setProvidedCreditScore(double providedCreditScore) {
		this.providedCreditScore = providedCreditScore;
	}

	public double getPredictedCreditScore() {
		return predictedCreditScore;
	}

	public void setPredictedCreditScore(double predictedCreditScore) {
		this.predictedCreditScore = predictedCreditScore;
	}

    // Getters/setters or Lombok
}
