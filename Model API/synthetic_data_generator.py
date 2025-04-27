import numpy as np
import pandas as pd

np.random.seed(42)

def generate_synthetic_loc_data(n_samples=5000):
    provinces = ['ON', 'BC', 'AB', 'QC', 'MB', 'SK', 'NS', 'NB', 'NL', 'PE']
    province_weights = [0.38, 0.13, 0.12, 0.23, 0.03, 0.03, 0.03, 0.02, 0.02, 0.01]
    employment_statuses = ['Full-time', 'Part-time', 'Unemployed']
    employment_weights = [0.75, 0.20, 0.05]
    payment_histories = ['On Time', 'Late <30', 'Late 30-60', 'Late >60']
    payment_weights = [0.7, 0.15, 0.1, 0.05]

    data = {
        'applicant_id': [f'APP-{i + 1:05d}' for i in range(n_samples)],
        'age': np.random.randint(19, 101, n_samples),
        'province': np.random.choice(provinces, n_samples, p=province_weights),
        'employment_status': np.random.choice(employment_statuses, n_samples, p=employment_weights),
        'months_employed': np.random.randint(0, 601, n_samples),
        'payment_history': np.random.choice(payment_histories, n_samples, p=payment_weights)
    }

    # Annual income - Log-normal (mean ~$60K, std ~$30K)
    mean, std = 60000, 30000
    phi = np.sqrt(np.log(1 + (std / mean) ** 2))
    mu = np.log(mean) - 0.5 * phi ** 2
    data['annual_income'] = np.random.lognormal(mu, phi, n_samples)

    # Monthly income
    monthly_income = data['annual_income'] / 12

    # Financial fields
    data['self_reported_debt'] = np.random.uniform(0.1, 0.3, n_samples) * monthly_income
    data['self_reported_expenses'] = np.random.uniform(0, 10000, n_samples)
    data['total_credit_limit'] = np.round(np.random.uniform(0.5, 2.0, n_samples) * data['annual_income'] / 10) * 10

    # Modified credit utilization - changed from beta(5,2) to beta(2,3) for more spread
    data['credit_utilization'] = np.random.beta(2, 3, n_samples) * 100

    data['estimated_debt'] = data['total_credit_limit'] * data['credit_utilization'] / 100 * 0.03
    data['monthly_expenses'] = np.random.uniform(0, 10000, n_samples)
    data['requested_amount'] = np.round(np.random.uniform(1000, 50000, n_samples) / 10) * 10

    # Modified distribution of open accounts - more varied
    data['num_open_accounts'] = np.random.negative_binomial(5, 0.4, n_samples)
    data['num_open_accounts'] = np.clip(data['num_open_accounts'], 0, 20)

    # Modified distribution of credit inquiries - more varied
    data['num_credit_inquiries'] = np.random.negative_binomial(2, 0.4, n_samples)
    data['num_credit_inquiries'] = np.clip(data['num_credit_inquiries'], 0, 10)

    # DTI
    total_monthly_debt = data['self_reported_debt'] + data['estimated_debt']
    data['dti'] = (total_monthly_debt + 0.03 * data['requested_amount']) / monthly_income * 100

    df = pd.DataFrame(data)

    # Credit Score Generator calibrated for mean=680, std=100
    def calculate_credit_score(data):
        # Payment History (35%) 
        payment_history_scores = {
            "On Time": 100,
            "Late <30": 55,   
            "Late 30-60": 35,  
            "Late >60": 15     
        }
        payment_history_score = payment_history_scores.get(data["payment_history"], 40)

        # Credit Utilization (30%) 
        # More penalty for high utilization
        if data["credit_utilization"] <= 10:
            credit_utilization_score = 100
        elif data["credit_utilization"] <= 30:
            credit_utilization_score = 90 - ((data["credit_utilization"] - 10) * 0.5)
        elif data["credit_utilization"] <= 50:
            credit_utilization_score = 80 - ((data["credit_utilization"] - 30) * 1.0)
        elif data["credit_utilization"] <= 70:
            credit_utilization_score = 60 - ((data["credit_utilization"] - 50) * 1.5)
        else:
            credit_utilization_score = 30 - ((data["credit_utilization"] - 70) * 0.5)
        credit_utilization_score = max(0, credit_utilization_score)

        # Credit History (15%) 
        # Based on months employed (longer is better)
        if data["months_employed"] >= 240:  # 20 years
            credit_history_score = 100
        elif data["months_employed"] >= 120:  # 10 years
            credit_history_score = 85 + ((data["months_employed"] - 120) / 120) * 15
        elif data["months_employed"] >= 60:  # 5 years
            credit_history_score = 65 + ((data["months_employed"] - 60) / 60) * 20
        elif data["months_employed"] >= 24:  # 2 years
            credit_history_score = 40 + ((data["months_employed"] - 24) / 36) * 25
        else:
            credit_history_score = max(10, (data["months_employed"] / 24) * 40)

        # Credit Mix (10%) 
        # Based on number of open accounts
        if data["num_open_accounts"] >= 5 and data["num_open_accounts"] <= 10:
            credit_mix_score = 90 + (data["num_open_accounts"] - 5) * 2  # Optimal range: 5-10 accounts
        elif data["num_open_accounts"] < 5:
            credit_mix_score = 60 + data["num_open_accounts"] * 6  # Less than optimal
        else:
            credit_mix_score = 90 - (data["num_open_accounts"] - 10) * 5  # Too many accounts
        credit_mix_score = max(10, min(100, credit_mix_score))

        # Credit Inquiries (10%)
        # Fewer inquiries are better
        if data["num_credit_inquiries"] == 0:
            credit_inquiry_score = 100
        elif data["num_credit_inquiries"] == 1:
            credit_inquiry_score = 90
        elif data["num_credit_inquiries"] == 2:
            credit_inquiry_score = 75
        elif data["num_credit_inquiries"] <= 5:
            credit_inquiry_score = 75 - ((data["num_credit_inquiries"] - 2) * 10)
        else:
            credit_inquiry_score = 45 - ((data["num_credit_inquiries"] - 5) * 5)
        credit_inquiry_score = max(0, credit_inquiry_score)

        # Apply updated weights to get more variance
        weighted_score = (
            (0.35 * payment_history_score) +
            (0.30 * credit_utilization_score) +
            (0.15 * credit_history_score) +
            (0.10 * credit_mix_score) +
            (0.10 * credit_inquiry_score)
        )

        # Apply normal-like transformation to the weighted score
        # Parameters calibrated to produce mean ≈680, std ≈100
        mean_shift = 11.7   # Shift the baseline to center around a lower mean
        std_factor = 4.2  # Increase the spread of scores

        # Scale from 0-100 to 300-900 with modified parameters
        min_score = 300
        max_score = 900

        # Apply the normal-like transformation within the scaling
        adjusted_weighted_score = (weighted_score - 50) / 10 * std_factor
        transformed_score = 680 + (adjusted_weighted_score - mean_shift) * 20

        # Clip to the valid range
        final_score = np.clip(transformed_score, min_score, max_score)

        return int(final_score)  # Return the final score rounded to the nearest integer

    # Calculate credit scores using the calibrated formula
    df['credit_score'] = df.apply(calculate_credit_score, axis=1)


    # Approval Logic
    def determine_approval(row):
        if row['credit_score'] < 500 or row['dti'] > 50 or row['credit_utilization'] > 80:
            return 0
        if row['credit_score'] >= 660 and row['dti'] <= 40 and row['payment_history'] == 'On Time':
            return 1
        return np.random.choice([0, 1], p=[0.4, 0.6])

    def calculate_credit_limit(row):
        if row['approved'] == 0:
            return 0

        # Base Limit Calculation
        if row['credit_score'] >= 660:
            base = 0.5
        elif row['credit_score'] >= 500:
            base = 0.25
        else:
            base = 0.1

        limit = base * row['annual_income']

        # DTI Adjustment
        if row['dti'] > 40:
            limit *= 0.5
        elif row['dti'] > 30:
            limit *= 0.75

        # Credit Score Cap
        if row['credit_score'] >= 750:
            cap = 25000
        elif row['credit_score'] >= 660:
            cap = 15000
        elif row['credit_score'] >= 500:
            cap = 10000
        else:
            cap = 5000

        # Employment Bonus
        if row['employment_status'] == 'Full-time' and row['months_employed'] >= 12:
            limit *= 1.1  # Increase by 10%

        # Payment Penalty
        if row['payment_history'] == 'Late >60':
            limit *= 0.5  # Reduce by 50%

        return min(limit, cap)

    def calculate_interest_rate(row):
        if row['approved'] == 0:
            return 0
        rate = 6.75
        if row['credit_score'] >= 750:
            rate -= 1
        elif row['credit_score'] >= 660:
            rate += 0
        elif row['credit_score'] >= 500:
            rate += 2
        else:
            rate += 4
        if row['dti'] > 30:
            rate += 1
        if row['payment_history'] == 'Late >60':
            rate += 2
        return min(max(rate, 3.0), 15.0)

    df['approved'] = df.apply(determine_approval, axis=1)
    df['approved_amount'] = df.apply(calculate_credit_limit, axis=1)
    df['interest_rate'] = df.apply(calculate_interest_rate, axis=1)

    df.to_csv('loc_synthetic_dataset.csv', index=False)
    return df

# Generate and save
if __name__ == "__main__":
    generate_synthetic_loc_data()