#!/bin/bash

# Deployment script for Tracking Number Generator API
# This script builds and deploys the application to Google Cloud Run

set -e

echo "🚀 Starting deployment of Tracking Number Generator API..."

# Check if gcloud is installed
if ! command -v gcloud &> /dev/null; then
    echo "❌ gcloud CLI is not installed. Please install it first."
    echo "Visit: https://cloud.google.com/sdk/docs/install"
    exit 1
fi

# Check if user is authenticated
if ! gcloud auth list --filter=status:ACTIVE --format="value(account)" | grep -q .; then
    echo "❌ Not authenticated with gcloud. Please run 'gcloud auth login' first."
    exit 1
fi

# Set project ID (replace with your actual project ID)
PROJECT_ID=${GOOGLE_CLOUD_PROJECT:-"your-project-id"}

if [ "$PROJECT_ID" = "your-project-id" ]; then
    echo "❌ Please set GOOGLE_CLOUD_PROJECT environment variable or update the script with your project ID."
    exit 1
fi

echo "📦 Building application..."
mvn clean package -DskipTests

echo "🐳 Building Docker image..."
docker build -t gcr.io/$PROJECT_ID/tracking-number-generator-api .

echo "📤 Pushing image to Google Container Registry..."
docker push gcr.io/$PROJECT_ID/tracking-number-generator-api

echo "🚀 Deploying to Cloud Run..."
gcloud run deploy tracking-number-generator-api \
    --image gcr.io/$PROJECT_ID/tracking-number-generator-api \
    --region us-central1 \
    --platform managed \
    --allow-unauthenticated \
    --port 8080 \
    --memory 512Mi \
    --cpu 1 \
    --min-instances 0 \
    --max-instances 10 \
    --set-env-vars SPRING_PROFILES_ACTIVE=prod

echo "✅ Deployment completed!"
echo "🌐 Your API is now available at:"
gcloud run services describe tracking-number-generator-api --region=us-central1 --format="value(status.url)"
