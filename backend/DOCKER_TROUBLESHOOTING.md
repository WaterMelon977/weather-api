# Crystal Weather Backend - Docker Build Instructions

## The Issue
You're experiencing persistent `EOF` errors when Docker tries to download base images from the registry. This is a network/connectivity issue, not a problem with the Dockerfile itself.

## Immediate Solutions

### Option 1: Restart Docker Desktop (Most Effective)
1. Completely quit Docker Desktop
2. Wait 10 seconds
3. Restart Docker Desktop
4. Wait for it to fully start
5. Try the build again

### Option 2: Clear Docker Cache
```bash
docker system prune -a
```
Then try building again.

### Option 3: Change Docker Registry Mirror
1. Open Docker Desktop Settings
2. Go to "Docker Engine"
3. Add a mirror registry (if you're in a region with connectivity issues)

### Option 4: Use Pre-built Base Images
Pull the images manually first:
```bash
docker pull maven:3.9-eclipse-temurin-21-alpine
docker pull eclipse-temurin:21-jre-alpine
```
Then run the build.

### Option 5: Build Locally Without Docker
Since you have Java installed locally, you can build the JAR and run it directly:
```bash
./mvnw clean package -DskipTests
java -jar target/weather-api-0.0.1-SNAPSHOT.jar
```

## Current Dockerfile (Java 21 LTS - Alpine)
The Dockerfile has been updated to use:
- Java 21 LTS (more stable than 25)
- Alpine Linux (smaller images, faster downloads)
- Official Maven image for build stage

This should work once the network issue is resolved.

## Deployment Alternative
If Docker continues to be problematic, consider:
1. Building the JAR locally with Maven
2. Deploying the JAR directly to your hosting platform
3. Most platforms (Heroku, Railway, Render) can run Spring Boot JARs without Docker
