# Crystal Weather 🌦️
https://roadmap.sh/projects/weather-api-wrapper-service
Crystal Weather is a premium, full-stack weather dashboard designed for high performance and visual excellence. It leverages modern web technologies and architectural best practices to provide a seamless user experience.

## ✨ Project Summary
The application allows users to search for real-time weather information and 5-day forecasts. It features a sophisticated autocomplete search engine, secure OAuth2 social authentication, and optimized data fetching through a multi-layer caching system.

### Key Features
- **Real-time Weather**: Current conditions including feel-like temperature, humidity, wind, and visibility.
- **5-Day Forecast**: Detailed daily breakdown of weather trends.
- **City Autocomplete**: Intelligent search suggestions as you type, powered by OpenWeather Geocoding.
- **Secure Authentication**: Social login (Google/GitHub) with JWTs stored in secure HttpOnly cookies.
- **Premium UI**: Modern glassmorphism design with smooth Framer Motion animations and responsive layouts.

---

## 🏛️ Architectural Wins
- **Reactive API Client**: Built with Spring `WebClient` featuring custom, granular exception handling (`WeatherApiException`) and automated mapping.
- **Hybrid Caching**: Multi-level cache implementation using Redis for external API stability and performance (3-hour TTL for weather, 24-hour for city suggestions).
- **Persistent User Profiles**: Automated user "upsert" logic during OAuth2 flows, storing profile details and login telemetry in PostgreSQL.
- **Clean Separation of Concerns**: Decoupled service layer, reactive client layer, and a dedicated security layer with custom JWT filters.
- **Frontend Performance**: Implemented debouncing for API-heavy search fields to minimize network usage and optimized server/client component boundaries in Next.js.
- **Robust Error Management**: Centralized `@RestControllerAdvice` ensuring a standardized error schema across the entire platform.

---

## 🚀 Getting Started

### Prerequisites
- **Java 17+** (Java 25 recommended)
- **Node.js 18+**
- **Docker** (for Redis/Postgres) or active local instances.

### 1. Backend Setup
1. Navigate to the backend directory: `cd backend`
2. Configure your `application.yaml` with your OpenWeather API key and Database credentials.
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### 2. Frontend Setup
1. Navigate to the frontend directory: `cd frontend`
2. Install dependencies:
   ```bash
   npm install
   ```
3. Set up your `.env.local` (see `.env.local.example`):
   ```env
   NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
   ```
4. Start the development server:
   ```bash
   npm run dev
   ```

### 3. Access the App
Open [http://localhost:3000](http://localhost:3000) in your browser.

---

## 🛠️ Tech Stack
- **Backend**: Spring Boot, JPA (PostgreSQL), Redis, Spring Security OAuth2, JWT.
- **Frontend**: Next.js 15+, React 19, TailwindCSS 4, Framer Motion, Lucide.
- **API**: OpenWeather (Weather & Geocoding).
