import numpy as np
from flask import Flask, request, jsonify
import joblib
import pandas as pd
from loc_model import ImprovedLineOfCreditApprovalModel

# Load the trained model
model = joblib.load('loc_model.pkl')

app = Flask(__name__)

@app.route('/')
def home():
    return "📡 LOC Approval API is online."

@app.route('/predict', methods=['POST'])
def predict():
    try:
        data = request.get_json(force=True)

        # Preprocess applicant input
        applicant_df = pd.DataFrame([data])

        # Label encode categorical variables
        categorical_cols = ['province', 'employment_status', 'payment_history']
        for col in categorical_cols:
            if col in applicant_df.columns:
                le = model.le_dict[col]
                applicant_df[col] = le.transform(applicant_df[col].astype(str))

        # Fill any missing model features
        for feature in model.features:
            if feature not in applicant_df.columns:
                applicant_df[feature] = np.median(model.data[feature])

        # Transform input
        X_input = model.scaler.transform(
            model.imputer.transform(applicant_df[model.features])
        )

        # Credit score mismatch check
        if 'credit_score' in data:
            predicted_score = model.credit_score_model.predict(X_input)[0]
            if abs(data['credit_score'] - predicted_score) > 30:
                return jsonify({
                    "error": "Credit score mismatch",
                    "provided_credit_score": data['credit_score'],
                    "predicted_credit_score": round(predicted_score, 2)
                }), 400

        # If no mismatch or not provided, proceed
        result = model.predict_loan_eligibility(data)
        return jsonify(result)

    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == '__main__':
    app.run(debug=True)
