# Deployment Guide

This guide covers multiple free deployment options for the Tracking Number Generator API.

## 🚀 Quick Deploy Options

### Option 1: Railway (Recommended)

1. **Sign up at [Railway](https://railway.app)**
2. **Connect your GitHub repository**
3. **Deploy automatically**

```bash
# The app will be deployed automatically when you push to GitHub
git add .
git commit -m "Add Railway deployment configuration"
git push origin main
```

**Railway will:**
- Automatically detect it's a Java/Maven project
- Build the application
- Deploy with PostgreSQL database
- Provide a public URL

### Option 2: Render

1. **Sign up at [Render](https://render.com)**
2. **Create a new Web Service**
3. **Connect your GitHub repository**
4. **Configure:**
   - Build Command: `mvn clean package -DskipTests`
   - Start Command: `java -jar target/tracking-number-generator-api-1.0.0.jar`
   - Environment: Java

### Option 3: Google Cloud Run

1. **Install Google Cloud CLI**
2. **Run the deployment script:**

```bash
# Make sure you have Google Cloud CLI installed
gcloud auth login
gcloud config set project YOUR_PROJECT_ID

# Deploy using our script
./deploy.sh
```

### Option 4: Heroku (Low-cost - $5/month)

**Note:** Heroku discontinued their free tier in November 2022, but offers affordable Eco dynos at $5/month.

1. **Install Heroku CLI**
2. **Create Heroku app:**

```bash
heroku create your-app-name
heroku addons:create heroku-postgresql:mini
git push heroku main
```

**Cost:** $5/month for Eco dyno + $5/month for Mini Postgres

## 🔧 Environment Variables

Set these environment variables in your deployment platform:

```bash
SPRING_PROFILES_ACTIVE=prod
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
DATABASE_URL=your_database_url
PORT=8080
```

## 📊 Monitoring

Once deployed, you can monitor your API:

- **Health Check**: `https://your-app-url/api/v1/next-tracking-number/health`
- **Metrics**: `https://your-app-url/api/v1/actuator/metrics`
- **Prometheus**: `https://your-app-url/api/v1/actuator/prometheus`

## 🧪 Testing Your Deployed API

```bash
# Test the deployed API
curl "https://your-app-url/api/v1/next-tracking-number" \
  -G \
  -d "origin_country_id=MY" \
  -d "destination_country_id=ID" \
  -d "weight=1.234" \
  -d "created_at=2018-11-20T19:29:32Z" \
  -d "customer_id=de619854-b59b-425e-9db4-943979e1bd49" \
  -d "customer_name=RedBox Logistics" \
  -d "customer_slug=redbox-logistics"
```

## 🎯 Recommended: Railway Deployment

Railway is the easiest option:

1. Go to [railway.app](https://railway.app)
2. Sign up with GitHub
3. Click "New Project" → "Deploy from GitHub repo"
4. Select your repository
5. Railway will automatically:
   - Detect it's a Java project
   - Build with Maven
   - Deploy with PostgreSQL
   - Give you a public URL

Your API will be live at: `https://your-app-name.railway.app/api/v1/next-tracking-number`
