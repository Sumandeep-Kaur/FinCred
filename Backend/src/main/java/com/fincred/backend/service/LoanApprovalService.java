package com.fincred.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fincred.backend.dto.LoanApprovalRequestDto;
import com.fincred.backend.dto.LoanApprovalResponseDto;
import com.fincred.backend.exception.CreditScoreMismatchException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoanApprovalService {
    private static final Logger logger = LoggerFactory.getLogger(LoanApprovalService.class);
    
    private final RestTemplate restTemplate;

    @Value("${flask.api.url}")
    private String flaskApiUrl;

    public LoanApprovalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LoanApprovalResponseDto processLoanApplication(LoanApprovalRequestDto request) {
        logger.info("Processing loan application for applicant: {}", request.getApplicantId());

        try {
            // 🔢 Derived fields
            double estimatedDebt = request.getTotalCreditLimit() * (request.getCreditUtilization() / 100.0) * 0.03;
            double totalMonthlyDebt = request.getSelfReportedDebt() + estimatedDebt;
            double monthlyIncome = request.getAnnualIncome() / 12.0;
            double dti = (totalMonthlyDebt + request.getRequestedAmount() * 0.03) / monthlyIncome;

            request.setEstimatedDebt(estimatedDebt);
            request.setTotalMonthlyDebt(totalMonthlyDebt);
            request.setDti(dti);

            // 🛰️ Build request payload
            Map<String, Object> requestBody = Map.ofEntries(
                Map.entry("credit_score", request.getCreditScore()),
                Map.entry("annual_income", request.getAnnualIncome()),
                Map.entry("self_reported_debt", request.getSelfReportedDebt()),
                Map.entry("self_reported_expenses", request.getSelfReportedExpenses()),
                Map.entry("requested_amount", request.getRequestedAmount()),
                Map.entry("age", request.getAge()),
                Map.entry("province", request.getProvince()),
                Map.entry("employment_status", request.getEmploymentStatus()),
                Map.entry("months_employed", request.getMonthsEmployed()),
                Map.entry("total_credit_limit", request.getTotalCreditLimit()),
                Map.entry("credit_utilization", request.getCreditUtilization()),
                Map.entry("num_open_accounts", request.getNumOpenAccounts()),
                Map.entry("num_credit_inquiries", request.getNumCreditInquiries()),
                Map.entry("payment_history", request.getPaymentHistory()),
                Map.entry("monthly_expenses", request.getMonthlyExpenses()),
                Map.entry("estimated_debt", estimatedDebt),
                Map.entry("total_monthly_debt", totalMonthlyDebt),
                Map.entry("dti", dti)
            );

            // 🔗 Make the request to Flask
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(flaskApiUrl, HttpMethod.POST, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            logger.info("Flask API Response: {}", responseBody);

            if (response.getStatusCode().is2xxSuccessful() && responseBody != null) {
                boolean approved = Boolean.TRUE.equals(responseBody.get("is_approved"));
                double approvedAmount = approved
                    ? ((Number) responseBody.get("approved_amount")).doubleValue() : 0.0;
                Double interestRate = approved
                    ? ((Number) responseBody.get("interest_rate")).doubleValue() : null;
                List<String> denialReasons = approved ? new ArrayList<>() : (List<String>) responseBody.get("denial_reasons");
                
                double predictedScore = ((Number) responseBody.get("predicted_credit_score")).doubleValue();
                double approvalProbability = ((Number) responseBody.get("approval_probability")).doubleValue();

                return new LoanApprovalResponseDto(
                    approved,
                    approvedAmount,
                    interestRate,
                    denialReasons,
                    predictedScore,
                    approvalProbability
                );
            }

            throw new RuntimeException("Unexpected error: No valid response from loan approval service.");

        } catch (HttpClientErrorException.BadRequest e) {
            String responseBody = e.getResponseBodyAsString();
            logger.warn("Flask API returned 400 Bad Request: {}", responseBody);

            double predicted = -1;
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(responseBody);

                if (json.has("predicted_credit_score")) {
                    predicted = json.get("predicted_credit_score").asDouble();
                } else if (json.has("predictedCreditScore")) {
                    predicted = json.get("predictedCreditScore").asDouble();
                }

                logger.info("Extracted predicted score: {}", predicted);

            } catch (Exception parseError) {
                logger.error("Could not parse predicted credit score from error response: {}", parseError.getMessage());
            }

            double provided = request.getCreditScore();
            throw new CreditScoreMismatchException("Credit score mismatch", provided, predicted);
            
        } catch (HttpServerErrorException e) {
            logger.error("Flask API error (500): {}", e.getResponseBodyAsString());
            throw new RuntimeException("Loan approval service is currently unavailable. Please try again later.");

        } catch (RestClientResponseException e) {
            logger.error("Unexpected error from Flask API: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Loan approval request failed due to an unexpected error.");

        } catch (Exception e) {
            logger.error("Loan application failed: {}", e.getMessage());
            throw new RuntimeException("Loan approval request failed. Please try again later.");
        }
    }

}
