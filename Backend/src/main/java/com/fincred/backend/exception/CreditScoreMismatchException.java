package com.fincred.backend.exception;

public class CreditScoreMismatchException extends RuntimeException {
    private final double providedScore;
    private final double predictedScore;

    public CreditScoreMismatchException(String message, double provided, double predicted) {
        super(message);
        this.providedScore = provided;
        this.predictedScore = predicted;
    }

    public double getProvidedScore() { return providedScore; }
    public double getPredictedScore() { return predictedScore; }
}