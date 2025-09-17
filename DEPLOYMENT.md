# Deployment Guide

This guide covers Railway deployment for the Tracking Number Generator API.

## 🚀 Railway Deployment (Recommended)

### Quick Deploy

1. **Sign up at [Railway](https://railway.app)**
2. **Connect your GitHub repository**
3. **Deploy automatically**

```bash
# The app will be deployed automatically when you push to GitHub
git add .
git commit -m "Deploy to Railway"
git push origin main
```

**Railway will:**
- Automatically detect it's a Java/Maven project
- Build the application
- Deploy with PostgreSQL database
- Provide a public URL

## 🔧 Environment Variables

Railway automatically sets these environment variables:

```bash
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=postgresql://... (automatically provided)
PORT=8080
```

## 📊 Monitoring

Once deployed, you can monitor your API:

- **Health Check**: `https://your-app-url/api/v1/next-tracking-number/health`
- **Metrics**: `https://your-app-url/api/v1/actuator/metrics`
- **Railway Dashboard**: Monitor logs, metrics, and performance

## 🧪 Testing Your Deployed API

```bash
# Test the deployed API
curl "https://your-app-url/api/v1/next-tracking-number?origin_country_id=MY&destination_country_id=ID&weight=1.234&created_at=2018-11-20T19:29:32Z&customer_id=de619854-b59b-425e-9db4-943979e1bd49&customer_name=RedBox%20Logistics&customer_slug=redbox-logistics"
```

## 🎯 Railway Benefits

Railway is the perfect choice because:
- ✅ **Zero configuration** needed
- ✅ **Automatic database** setup
- ✅ **Generous free tier** ($5 credit monthly)
- ✅ **One-click deployment** from GitHub
- ✅ **Built-in monitoring** and logs
- ✅ **Automatic HTTPS** and custom domains

Your API will be live at: `https://your-app-name.railway.app/api/v1/next-tracking-number`
