# Yield Planner — Installation Guide

Complete setup guide to get the Yield Planner app running on your machine.

---

## Prerequisites

Install the following before starting:

| Tool        | Version    | Download Link                                                       |
|-------------|------------|----------------------------------------------------------------------|
| **Java JDK** | 21+       | https://adoptium.net/temurin/releases/ (Temurin 21 LTS recommended) |
| **Maven**    | 3.9+      | https://maven.apache.org/download.cgi                               |
| **Python**   | 3.10–3.13 | https://www.python.org/downloads/                                    |
| **Node.js**  | 18+       | https://nodejs.org/                                                  |
| **Git**      | any       | https://git-scm.com/downloads                                       |

### Verify installations
```bash
java -version       # Should show 21+
mvn -version         # Should show 3.9+
python --version     # Should show 3.10+
node --version       # Should show 18+
npm --version        # Should show 9+
git --version
```

---

## Step 1: Clone / Extract the Project

```bash
# If from git:
git clone <repo-url>
cd "YEILD planner"

# Or if you received a zip, extract it and cd into the folder.
```

---

## Step 2: Start the ML Service (Python FastAPI — Port 8000)

```bash
cd ml-service

# Create a virtual environment (recommended)
python -m venv .venv

# Activate it:
# Windows PowerShell:
.venv\Scripts\Activate.ps1
# Windows CMD:
.venv\Scripts\activate.bat
# macOS / Linux:
source .venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Start the service
python main.py
```

The ML service starts at **http://localhost:8000**.  
Health check: http://localhost:8000/health

> **Note:** The trained model files are included in `ml-service/model/`.  
> If you want to retrain, open `ml-notebook/crop_yield_prediction.ipynb` in Jupyter and run all cells.  
> Install Jupyter first: `pip install jupyter pandas matplotlib seaborn`

---

## Step 3: Start the Backend (Spring Boot — Port 8080)

Open a **new terminal**:

```bash
cd backend

# Build and run (uses embedded H2 database, no DB setup needed)
mvn spring-boot:run
```

The backend starts at **http://localhost:8080**.

> **First run** downloads Maven dependencies (~5 min on slow internet).  
> The H2 in-memory database is auto-configured — no PostgreSQL needed for development.  
> H2 Console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:file:./data/crop_yield`)

### Troubleshooting Backend

| Problem | Fix |
|---------|-----|
| `mvn` not found | Add Maven `bin/` folder to your system PATH |
| Port 8080 in use | Kill the process: `netstat -ano \| findstr :8080` then `taskkill /PID <pid> /F` (Windows) or `lsof -i :8080` + `kill <pid>` (Mac/Linux) |
| Lombok errors | Ensure you have JDK 21. If using JDK 25+, Lombok 1.18.36+ is required (already configured in pom.xml) |
| Build fails | Try `mvn clean install -DskipTests` first |

---

## Step 4: Start the Frontend (React + Vite — Port 5173)

Open a **new terminal**:

```bash
cd frontend

# Install dependencies
npm install

# Start dev server
npm run dev
```

The frontend starts at **http://localhost:5173** (or 5174 if 5173 is busy).

---

## Step 5: Use the App

1. Open **http://localhost:5173** in your browser
2. **Register** a new account (name, email, password)
3. **Login** with your credentials
4. **Add a Farm** from the dashboard
5. **Add Plots** to your farm (select crop, soil type, area)
6. **Record Field Conditions** (rainfall, temperature, NPK values, etc.)
7. **Get Yield Predictions** — powered by the ML model
8. **View Recommendations** — irrigation, fertilizer, pest management tips
9. **Toggle Language** — switch between English and Telugu using the navbar button

---

## Project Structure

```
YEILD planner/
├── ml-notebook/          # Jupyter notebook for ML experimentation
│   └── crop_yield_prediction.ipynb
├── ml-service/           # Python FastAPI ML microservice (port 8000)
│   ├── main.py
│   ├── requirements.txt
│   └── model/            # Trained model files (.pkl)
├── backend/              # Java Spring Boot API (port 8080)
│   ├── pom.xml
│   └── src/main/java/com/yieldplanner/
│       ├── entity/       # JPA entities (User, Farm, Plot, etc.)
│       ├── repository/   # Spring Data repositories
│       ├── dto/          # Request/Response DTOs
│       ├── service/      # Business logic
│       ├── controller/   # REST controllers
│       ├── security/     # JWT auth (JwtUtil, JwtAuthFilter)
│       └── config/       # Security, WebClient, CORS config
├── frontend/             # React + Vite UI (port 5173)
│   ├── package.json
│   └── src/
│       ├── pages/        # Login, Register, Dashboard, Farm, Plot pages
│       ├── services/     # Axios API layer
│       ├── context/      # Auth context (JWT)
│       └── i18n/         # English + Telugu translations
├── docs/                 # Architecture documentation
├── .gitignore
├── INSTALL.md            # This file
└── README.md
```

---

## API Endpoints Quick Reference

### Auth (no token needed)
| Method | Endpoint         | Description       |
|--------|-----------------|-------------------|
| POST   | `/auth/register` | Register new user |
| POST   | `/auth/login`    | Login, get JWT    |

### Farms (JWT required)
| Method | Endpoint         | Description     |
|--------|-----------------|-----------------|
| GET    | `/api/farms`     | List your farms |
| POST   | `/api/farms`     | Create a farm   |
| GET    | `/api/farms/{id}`| Get farm detail |
| DELETE | `/api/farms/{id}`| Delete a farm   |

### Plots (JWT required)
| Method | Endpoint                              | Description           |
|--------|--------------------------------------|-----------------------|
| GET    | `/api/plots/farm/{farmId}`           | List plots in a farm  |
| POST   | `/api/plots`                         | Create a plot         |
| POST   | `/api/plots/{id}/conditions`         | Add field conditions  |
| POST   | `/api/plots/{id}/predict`            | Get yield prediction  |
| GET    | `/api/plots/{id}/recommendations`    | Get recommendations   |

### Weather (JWT required)
| Method | Endpoint                        | Description          |
|--------|---------------------------------|----------------------|
| GET    | `/api/weather/current?city=...` | Current weather data |

---

## Ports Summary

| Service          | Port | URL                        |
|------------------|------|----------------------------|
| ML Service       | 8000 | http://localhost:8000       |
| Spring Boot API  | 8080 | http://localhost:8080       |
| React Frontend   | 5173 | http://localhost:5173       |
| H2 Console       | 8080 | http://localhost:8080/h2-console |

---

## Environment Variables (Optional)

| Variable             | Default                  | Description                     |
|----------------------|--------------------------|---------------------------------|
| `OPENWEATHER_API_KEY`| (none)                   | OpenWeatherMap API key for live weather |
| `JWT_SECRET`         | (auto-generated)         | JWT signing key                 |
| `SPRING_PROFILES_ACTIVE` | (default = H2)      | Set to `prod` for PostgreSQL    |

---

## Quick Start (All-in-One)

Open **3 terminals** and run:

**Terminal 1 — ML Service:**
```bash
cd ml-service && pip install -r requirements.txt && python main.py
```

**Terminal 2 — Backend:**
```bash
cd backend && mvn spring-boot:run
```

**Terminal 3 — Frontend:**
```bash
cd frontend && npm install && npm run dev
```

Then open **http://localhost:5173** in your browser.

---

## FAQ

**Q: Do I need PostgreSQL?**  
A: No. The app uses H2 (embedded file database) by default. PostgreSQL config is in `application.properties` but commented out.

**Q: Do I need to run the Jupyter notebook?**  
A: No. The trained model files are already included. Only run the notebook if you want to retrain or experiment.

**Q: How do I get real weather data?**  
A: Sign up at [OpenWeatherMap](https://openweathermap.org/api), get a free API key, and set it as `OPENWEATHER_API_KEY` environment variable. Without it, the app returns mock weather data.

**Q: Can I deploy this?**  
A: Yes. Build the frontend (`npm run build`), package the backend (`mvn package`), and deploy the ML service with uvicorn. See `docs/ARCHITECTURE.md` for deployment guidance.
