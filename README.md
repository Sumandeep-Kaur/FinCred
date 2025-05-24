# FinCred - Line of Credit Eligibility Check Application

## Overview

**Fincred** is a web application that helps users check their eligibility for a personal loan or line of credit.  
Instead of applying through slow, manual processes, users can fill in their financial details and instantly find out:
- Whether they are eligible
- The maximum amount they could get
- The estimated interest rate
- The probability of approval
- Any reasons for denial (if applicable)
- A validation check on their entered credit score

This project uses synthetic data, multiple machine learning models, and a mix of Java Spring Boot (backend), Python (ML API), and Angular (frontend) to deliver fast, accurate results — all without needing external credit bureau connections.

---

## 📂 Folder Structure
```
/Frontend → Angular frontend for user interaction
/Backend → Java Spring Boot backend connecting frontend and ML API
/Model API → Python Flask API serving ML models for predictions
```

## 🔑 Key Features

### ✅ Credit Score Validation
Since no free external API was available for credit score verification, a synthetic validation system was implemented:
- A **synthetic credit score generator** was built based on FICO principles (payment history, credit utilization, account age, credit mix, inquiries).
- A **separate ML regression model** predicts the credit score from other applicant features.
- If the difference between the **user-entered credit score** and **model-predicted score** exceeds ±30 points, the system flags a mismatch and returns an error, rejecting the request.
This ensures the data remains realistic and trustworthy without needing access to real-world bureau data.

### 🛠️ Multi-Model Machine Learning

The system uses **four specialized models**:
- **Credit Score Prediction** → regression model
- **Eligibility Prediction** → classification model
- **Approved Amount Prediction** → regression model
- **Interest Rate Prediction** → regression model

These models run independently but together deliver a complete eligibility profile.

### 🏗️ Synthetic Data Generation
To simulate realistic user inputs:
- Credit scores (mean ≈ 680, std ≈ 100)
- Income distributions, debt ratios, expense levels, credit utilization rates
- Injected noise (missing values, mild outliers) to mimic real-world inconsistencies

---

## ⚙️ Technologies Used

| Layer        | Technologies                     |
|-------------|----------------------------------|
| Backend     | Java Spring Boot                 |
| ML Models   | Python 3, Flask, Scikit-learn, Pandas, NumPy, Joblib |
| Frontend    | Angular                          |

---

## 🔌 API Usage

### Endpoint
POST /predict

### Example Request
```
{
    "age": 35,
    "province": "ON",
    "employment_status": "Full-time",
    "months_employed": 48,
    "annual_income": 80000,
    "self_reported_debt": 2100,
    "self_reported_expenses": 2000,
    "total_credit_limit": 25000,
    "credit_utilization": 40,
    "num_open_accounts": 3,
    "num_credit_inquiries": 2,
    "monthly_expenses": 3000,
    "dti": 42.75,
    "payment_history": "On Time",
    "requested_amount": 15000,
    "estimated_debt": 300,
    "provided_credit_score": 750
}
```

### Example Response (Eligible)
```
{
    "predicted_credit_score": 706.43,
    "approval_probability": 0.7092,
    "is_eligible": true,
    "approved_amount": 15160.54,
    "interest_rate": 7.81,
    "denial_reasons": []
}
```

### Example Response (Not eligible)
```
{
    "predicted_credit_score": 706.43,
    "approval_probability": 0.2010,
    "is_eligible": false,
    "approved_amount": 0,
    "interest_rate": 0,
    "denial_reasons": ["High Debt-to-income ratio"]
}
```

### Example Response (Credit Score Mismatch)
```
{
    "error": "Credit score mismatch",
    "provided_credit_score": 750,
    "predicted_credit_score": 710.25
}
````

--- 

## 📊 Model Performance

| Model Task                 | Type           | Metric                      | Result           |
|----------------------------|---------------|----------------------------|------------------|
| Credit Score Prediction    | Regression     | Mean Absolute Error (MAE)  | 1.39 points      |
| Eligibility Prediction     | Classification | Accuracy                   | ~90%            |
| Approved Amount Prediction | Regression     | Mean Absolute Error (MAE)  | ~$905           |
| Interest Rate Prediction   | Regression     | Mean Absolute Error (MAE)  | ~0.17%          |

---

### 📝 Conclusion
The Fincred Credit Eligibility Check Application demonstrates how modern machine learning and financial modeling can empower users to understand their borrowing power in seconds — fairly, transparently, and scalably. It provides explainable, data-driven decisions and lays the foundation for production-grade financial automation.


