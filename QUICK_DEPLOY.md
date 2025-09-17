# 🚀 Railway Deployment Guide

## Deploy to Railway (5 minutes)

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

## 🧪 Test Your Deployed API

Once deployed, test with:

```bash
curl "https://your-app-url/api/v1/next-tracking-number?origin_country_id=MY&destination_country_id=ID&weight=1.234&created_at=2018-11-20T19:29:32Z&customer_id=de619854-b59b-425e-9db4-943979e1bd49&customer_name=RedBox%20Logistics&customer_slug=redbox-logistics"
```

## 📊 Monitor Your API

- **Health Check**: `https://your-app-url/api/v1/next-tracking-number/health`
- **Metrics**: `https://your-app-url/api/v1/actuator/metrics`
- **Railway Dashboard**: Monitor logs, metrics, and performance

## 🎯 Why Railway?

Railway is the perfect choice because:
- ✅ **No configuration** needed
- ✅ **Automatic database** setup
- ✅ **Free tier** with generous limits ($5 credit monthly)
- ✅ **Automatic deployments** from GitHub
- ✅ **Built-in monitoring** and logs
- ✅ **Automatic HTTPS** and custom domains

Just push your code to GitHub and connect it to Railway!
