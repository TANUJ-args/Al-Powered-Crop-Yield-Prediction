"""
Crop Yield Prediction – FastAPI ML Microservice
Loads trained model and exposes /predict endpoint.
"""
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
import numpy as np
import joblib
import json
import os
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="Crop Yield Prediction API",
    description="ML microservice for predicting crop yield based on field conditions",
    version="1.0.0"
)

# CORS – allow Spring Boot backend to call this service
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ── Load model artifacts at startup ──────────────────────────────────────────
MODEL_DIR = os.path.join(os.path.dirname(__file__), "model")

model = None
scaler = None
label_encoders = None
metadata = None


def load_model():
    global model, scaler, label_encoders, metadata
    try:
        model = joblib.load(os.path.join(MODEL_DIR, "yield_model.pkl"))
        scaler = joblib.load(os.path.join(MODEL_DIR, "scaler.pkl"))
        label_encoders = joblib.load(os.path.join(MODEL_DIR, "label_encoders.pkl"))
        with open(os.path.join(MODEL_DIR, "model_metadata.json")) as f:
            metadata = json.load(f)
        logger.info(f"Model loaded: {metadata['model_type']} {metadata['model_version']}")
        logger.info(f"Features: {metadata['feature_order']}")
    except FileNotFoundError:
        logger.warning("Model files not found in %s. Run the notebook first to train and export the model.", MODEL_DIR)
        logger.warning("The /predict endpoint will return dummy values until the model is available.")


@app.on_event("startup")
async def startup():
    load_model()


# ── Request / Response schemas ───────────────────────────────────────────────

class PredictRequest(BaseModel):
    crop_type: str = Field(..., example="Rice", description="Crop name")
    state: str = Field(default="Andhra Pradesh", description="State / region")
    season: str = Field(default="Kharif", description="Kharif / Rabi / Zaid")
    soil_type: str = Field(default="Alluvial", description="Soil type")
    area_ha: float = Field(default=2.0, ge=0.1, description="Area in hectares")
    rainfall_mm: float = Field(default=850.0, ge=0, description="Rainfall in mm")
    temp_avg_c: float = Field(default=29.5, description="Average temperature °C")
    humidity_pct: float = Field(default=75.0, ge=0, le=100, description="Humidity %")
    soil_ph: float = Field(default=6.8, ge=0, le=14, description="Soil pH")
    nitrogen_kg: float = Field(default=80.0, ge=0, description="Nitrogen (kg/ha)")
    phosphorus_kg: float = Field(default=40.0, ge=0, description="Phosphorus (kg/ha)")
    potassium_kg: float = Field(default=50.0, ge=0, description="Potassium (kg/ha)")


class PredictResponse(BaseModel):
    predicted_yield: float
    unit: str = "tonnes/hectare"
    model_version: str
    model_type: str


# ── Endpoints ────────────────────────────────────────────────────────────────

@app.get("/health")
async def health():
    return {
        "status": "healthy",
        "model_loaded": model is not None,
        "model_version": metadata["model_version"] if metadata else None,
    }


@app.post("/predict", response_model=PredictResponse)
async def predict(req: PredictRequest):
    if model is None:
        # Return dummy prediction when model is not loaded
        logger.warning("Model not loaded – returning dummy prediction")
        return PredictResponse(
            predicted_yield=round(np.random.uniform(1.5, 5.0), 2),
            unit="tonnes/hectare",
            model_version="dummy",
            model_type="dummy"
        )

    try:
        feature_order = metadata["feature_order"]
        categorical_features = metadata["categorical_features"]

        # Build input dict
        input_dict = req.dict()

        # Encode categoricals
        for col in categorical_features:
            le = label_encoders[col]
            val = input_dict[col]
            if val not in le.classes_:
                raise HTTPException(
                    status_code=400,
                    detail=f"Unknown value '{val}' for '{col}'. Allowed: {list(le.classes_)}"
                )
            input_dict[col] = le.transform([val])[0]

        # Build feature vector
        feature_vector = np.array([[input_dict[f] for f in feature_order]])
        feature_vector_scaled = scaler.transform(feature_vector)

        predicted = model.predict(feature_vector_scaled)[0]
        predicted = round(max(0.0, float(predicted)), 2)

        return PredictResponse(
            predicted_yield=predicted,
            unit="tonnes/hectare",
            model_version=metadata["model_version"],
            model_type=metadata["model_type"]
        )
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Prediction error: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/model-info")
async def model_info():
    if metadata is None:
        return {"status": "Model not loaded"}
    return {
        "model_type": metadata["model_type"],
        "model_version": metadata["model_version"],
        "features": metadata["feature_order"],
        "categorical_features": metadata["categorical_features"],
        "allowed_values": metadata.get("label_encoder_classes", {}),
        "metrics": metadata.get("metrics", {}),
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
