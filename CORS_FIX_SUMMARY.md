# CORS Fix Summary

## Problem
Production deployment was showing CORS errors:
```
Access to fetch at 'https://weather-api-h88a.onrender.com/auth/me' 
from origin 'https://weather-api-brown-eight.vercel.app' 
has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present
```

**Symptoms:**
- ✅ `Vary: Origin` header present (Spring was CORS-aware)
- ❌ `Access-Control-Allow-Origin` header missing (CORS not applied)
- ❌ `Access-Control-Allow-Credentials` header missing

## Root Cause
Spring Security was **not using** the CORS configuration. The old setup had:
1. A separate `CorsConfig.java` implementing `WebMvcConfigurer`
2. `Customizer.withDefaults()` in SecurityConfig (which looks for a bean but doesn't guarantee it's used)
3. No explicit `CorsConfigurationSource` bean for Spring Security

**Result:** Spring Security intercepted requests but didn't apply CORS headers.

## The Fix

### ✅ What We Did
1. **Consolidated CORS into SecurityConfig** - Defined a `@Bean CorsConfigurationSource` that Spring Security explicitly uses
2. **Removed ambiguity** - Deleted the old `CorsConfig.java` to prevent conflicts
3. **Explicit wiring** - Changed from `.cors(Customizer.withDefaults())` to `.cors(cors -> cors.configurationSource(corsConfigurationSource()))`
4. **OPTIONS request handling** - Added early return in `JwtAuthenticationFilter` for preflight requests

### 📝 Files Changed

#### `backend/src/main/java/com/rdbackend/weather_api/security/SecurityConfig.java`
- Added `@Value("${app.frontend-url}")` injection
- Created `corsConfigurationSource()` bean with explicit CORS rules
- Wired it directly into the SecurityFilterChain

#### `backend/src/main/java/com/rdbackend/weather_api/security/JwtAuthenticationFilter.java`
- Added OPTIONS request bypass to prevent JWT filter from interfering with CORS preflight

#### `backend/src/main/java/com/rdbackend/weather_api/config/CorsConfig.java`
- **DELETED** - No longer needed, prevents conflicts

## Environment Variables Required

### On Render (Backend)
```
FRONTEND_URL=https://weather-api-brown-eight.vercel.app
```

Or for multiple origins (production + preview):
```
FRONTEND_URL=https://weather-api-brown-eight.vercel.app,https://preview.vercel.app
```

### On Vercel (Frontend)
```
NEXT_PUBLIC_API_BASE_URL=https://weather-api-h88a.onrender.com
```

## Deployment Steps

1. **Commit and push:**
   ```bash
   git add .
   git commit -m "fix: canonical CORS configuration for Spring Security"
   git push
   ```

2. **Verify Render environment variable** - Ensure `FRONTEND_URL` matches your Vercel deployment URL exactly

3. **Wait for auto-deploy** on Render

4. **Test** - The CORS error should be resolved

## Why This Works

Spring Security's filter chain runs **before** MVC. When you use:
```java
.cors(cors -> cors.configurationSource(corsConfigurationSource()))
```

You're telling Security to:
1. Use the exact `CorsConfigurationSource` bean you defined
2. Apply CORS headers at the Security layer (before any other filters)
3. Handle OPTIONS preflight requests properly

This is the **canonical Spring Security way** and eliminates all ambiguity.

## Verification

After deployment, check the browser Network tab:
- ✅ `Access-Control-Allow-Origin: https://weather-api-brown-eight.vercel.app`
- ✅ `Access-Control-Allow-Credentials: true`
- ✅ `Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS`

---

**Date Fixed:** 2026-02-05  
**Status:** ✅ Ready for deployment
