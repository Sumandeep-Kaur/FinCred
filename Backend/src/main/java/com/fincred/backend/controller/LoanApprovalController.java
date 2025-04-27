package com.fincred.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fincred.backend.dto.LoanApprovalRequestDto;
import com.fincred.backend.dto.LoanApprovalResponseDto;
import com.fincred.backend.exception.CreditScoreMismatchException;
import com.fincred.backend.service.LoanApprovalService;

import jakarta.validation.Valid;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/loan")
@CrossOrigin(origins = "*") // Enable CORS
public class LoanApprovalController {
    private static final Logger logger = LoggerFactory.getLogger(LoanApprovalController.class);
   
    private final LoanApprovalService loanApprovalService;
    
    public LoanApprovalController(LoanApprovalService loanApprovalService) {
        this.loanApprovalService = loanApprovalService;
    }
    
    @PostMapping("/apply")
    public ResponseEntity<?> processApplication(@Valid @RequestBody LoanApprovalRequestDto request) {
        logger.info("Received loan application request for applicant ID: {}", request.getApplicantId());

        try {
            LoanApprovalResponseDto response = loanApprovalService.processLoanApplication(request);
            logger.info("Processed LOC application for applicant: {}, approved: {}",
                    request.getApplicantId(), response.isApproved());
            return ResponseEntity.ok(response);

        } catch (CreditScoreMismatchException e) {
            logger.warn("Credit score mismatch: {}", e.getMessage());
            Map<String, Object> mismatchResponse = Map.of(
                "error", "Credit score mismatch",
                "providedCreditScore", e.getProvidedScore(),
                "predictedCreditScore", e.getPredictedScore()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mismatchResponse);

        } catch (RuntimeException e) {
            logger.error("Loan processing error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of("error", "Loan processing failed: " + e.getMessage())
            );
        }
    }


    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("LOC Approval Service is running");
    }
}