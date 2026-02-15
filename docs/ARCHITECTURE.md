# Yield Planner – System Architecture

## High-Level Flow

```
React App (Farmer UI)
  → Spring Boot API (Auth, CRUD, Business Logic)
    → PostgreSQL (Users, Farms, Plots, Predictions, Recommendations)
    → Python ML Service (Crop Yield Prediction – FastAPI)
    → Weather API (OpenWeather – real-time weather data)
```

## Tech Stack

| Layer        | Technology                                 |
|--------------|--------------------------------------------|
| Frontend     | React + Axios + React Router               |
| Backend API  | Spring Boot (REST) + Spring Security + JWT |
| Data Access  | Spring Data JPA / JDBC + PostgreSQL        |
| ML Service   | Python (FastAPI) + scikit-learn             |
| External API | OpenWeather API                            |

## Database Schema (PostgreSQL – `crop_yield` database)

### Tables

#### `users`
| Column        | Type         | Notes                    |
|---------------|--------------|--------------------------|
| id            | BIGSERIAL PK |                          |
| name          | VARCHAR(100) |                          |
| email         | VARCHAR(150) | UNIQUE                   |
| phone         | VARCHAR(15)  |                          |
| role          | VARCHAR(20)  | FARMER / ADMIN           |
| password_hash | VARCHAR(255) | BCrypt                   |
| created_at    | TIMESTAMP    | DEFAULT NOW()            |

#### `farms`
| Column    | Type           | Notes               |
|-----------|----------------|----------------------|
| id        | BIGSERIAL PK   |                      |
| user_id   | BIGINT FK      | → users.id           |
| name      | VARCHAR(100)   |                      |
| location  | VARCHAR(200)   |                      |
| latitude  | DOUBLE         |                      |
| longitude | DOUBLE         |                      |
| region    | VARCHAR(100)   | State / District     |
| soil_type | VARCHAR(50)    | e.g., Alluvial, Clay |
| created_at| TIMESTAMP      |                      |

#### `plots`
| Column      | Type         | Notes                   |
|-------------|--------------|-------------------------|
| id          | BIGSERIAL PK |                         |
| farm_id     | BIGINT FK    | → farms.id              |
| crop_type   | VARCHAR(50)  | Rice, Wheat, Maize etc. |
| area        | DOUBLE       | In hectares             |
| season      | VARCHAR(20)  | Kharif / Rabi / Zaid    |
| sowing_date | DATE         |                         |
| created_at  | TIMESTAMP    |                         |

#### `field_conditions`
| Column        | Type         | Notes              |
|---------------|--------------|---------------------|
| id            | BIGSERIAL PK |                     |
| plot_id       | BIGINT FK    | → plots.id          |
| date          | DATE         |                     |
| rainfall      | DOUBLE       | mm                  |
| temp_avg      | DOUBLE       | °C                  |
| humidity      | DOUBLE       | %                   |
| soil_moisture | DOUBLE       | %                   |
| soil_ph       | DOUBLE       |                     |
| nitrogen      | DOUBLE       | kg/ha               |
| phosphorus    | DOUBLE       | kg/ha               |
| potassium     | DOUBLE       | kg/ha               |

#### `predictions`
| Column              | Type         | Notes                   |
|---------------------|--------------|-------------------------|
| id                  | BIGSERIAL PK |                         |
| plot_id             | BIGINT FK    | → plots.id              |
| predicted_yield     | DOUBLE       | tonnes/hectare          |
| prediction_date     | TIMESTAMP    |                         |
| model_version       | VARCHAR(50)  |                         |
| input_snapshot_json | TEXT         | JSON of inputs used     |

#### `recommendations`
| Column       | Type         | Notes                          |
|--------------|--------------|--------------------------------|
| id           | BIGSERIAL PK |                                |
| plot_id      | BIGINT FK    | → plots.id                     |
| type         | VARCHAR(30)  | IRRIGATION / FERTILIZER / PEST |
| text         | TEXT         |                                |
| date         | DATE         |                                |
| rule_source  | VARCHAR(50)  | RULE_BASED / ML_MODEL          |

## API Endpoints

### Auth
- `POST /auth/register` – Register new user
- `POST /auth/login` – Login, returns JWT

### Farms
- `POST /api/farms` – Create farm
- `GET  /api/farms` – List user's farms
- `GET  /api/farms/{id}` – Get farm details

### Plots
- `POST /api/plots` – Create plot
- `GET  /api/plots?farmId=...` – List plots for a farm
- `GET  /api/plots/{id}` – Get plot details

### Field Conditions
- `POST /api/plots/{id}/conditions` – Add field conditions
- `GET  /api/plots/{id}/conditions` – Get conditions history

### Predictions
- `POST /api/plots/{id}/predict-yield` – Run yield prediction
- `GET  /api/plots/{id}/predictions` – Get prediction history

### Recommendations
- `GET  /api/plots/{id}/recommendations` – Get recommendations

### Weather
- `GET  /api/weather/current?lat=..&lon=..` – Get current weather

## Phases

### Phase 1 (CRUD + Rule-Based)
- Spring Boot REST API with JWT auth
- PostgreSQL schema with all entities
- React UI with Login, Dashboard, Farm/Plot CRUD
- Dummy / rule-based yield predictions and recommendations

### Phase 2 (ML + Live Weather)
- Trained ML model (RandomForest / Linear Regression)
- FastAPI service exposing `/predict` endpoint
- Spring Boot calls ML service for real predictions
- OpenWeather integration for live weather data
- Recommendations engine with weather-aware rules
