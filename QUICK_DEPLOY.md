# 🚀 Quick Deploy Guide

## Option 1: Railway (Easiest - 5 minutes)

1. **Go to [railway.app](https://railway.app)**
2. **Sign up with GitHub**
3. **Click "New Project" → "Deploy from GitHub repo"**
4. **Select this repository**
5. **Railway will automatically:**
   - Detect it's a Java project
   - Build with Maven
   - Deploy with PostgreSQL database
   - Give you a public URL

**Your API will be live at:** `https://your-app-name.railway.app/api/v1/next-tracking-number`

## Option 2: Render (Also Easy)

1. **Go to [render.com](https://render.com)**
2. **Sign up with GitHub**
3. **Click "New" → "Web Service"**
4. **Connect this repository**
5. **Configure:**
   - Build Command: `mvn clean package -DskipTests`
   - Start Command: `java -jar target/tracking-number-generator-api-1.0.0.jar`
   - Environment: Java

## Option 3: Google Cloud Run (Most Scalable)

1. **Install Google Cloud CLI:**
   ```bash
   # macOS
   brew install google-cloud-sdk
   
   # Ubuntu
   curl https://sdk.cloud.google.com | bash
   ```

2. **Set up project:**
   ```bash
   gcloud auth login
   gcloud projects create tracking-api-$(date +%s)
   gcloud config set project tracking-api-$(date +%s)
   ```

3. **Deploy:**
   ```bash
   ./deploy.sh
   ```

## Option 4: Fly.io (Free tier available)

1. **Install Fly CLI:**
   ```bash
   # macOS
   brew install flyctl
   
   # Ubuntu
   curl -L https://fly.io/install.sh | sh
   ```

2. **Deploy:**
   ```bash
   fly auth login
   fly launch
   fly deploy
   ```

**Free tier:** 3 shared-cpu-1x 256mb VMs, 160GB outbound data transfer

## Option 5: Heroku (Low-cost - $5/month)

**Note:** Heroku discontinued their free tier in November 2022, but offers affordable Eco dynos.

1. **Install Heroku CLI:**
   ```bash
   # macOS
   brew install heroku/brew/heroku
   
   # Ubuntu
   curl https://cli-assets.heroku.com/install.sh | sh
   ```

2. **Deploy:**
   ```bash
   heroku create your-app-name
   heroku addons:create heroku-postgresql:mini
   git push heroku main
   ```

**Cost:** $5/month for Eco dyno + $5/month for Mini Postgres

## 🧪 Test Your Deployed API

Once deployed, test with:

```bash
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

## 📊 Monitor Your API

- **Health Check**: `https://your-app-url/api/v1/next-tracking-number/health`
- **Metrics**: `https://your-app-url/api/v1/actuator/metrics`

## 🎯 Recommended: Railway

Railway is the easiest option because:
- ✅ No configuration needed
- ✅ Automatic database setup
- ✅ Free tier with generous limits
- ✅ Automatic deployments from GitHub
- ✅ Built-in monitoring

Just push your code to GitHub and connect it to Railway!
