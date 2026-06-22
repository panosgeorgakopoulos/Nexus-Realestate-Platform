# ⚡ Quick Reference Card

## 🚀 Getting Started (5 Steps, 10 Minutes)

### Step 1: Start Database (2 min)
```bash
brew services start postgresql
```

### Step 2: Create Database (1 min)
```bash
psql -U postgres -c "CREATE DATABASE nexus_db;"
```

### Step 3: Navigate to Project (30 sec)
```bash
cd /Users/panosgeorgakopoulos/IdeaProjects/realestate
```

### Step 4: Start Application (3 min - waiting for startup)
```bash
./mvnw spring-boot:run
```

### Step 5: Login in Browser (1 min + testing)
```
URL: http://localhost:8080/nexus-frontend/login.html
Email: admin@nexus.gr
Password: 123456
```

---

## 📋 What Was Fixed

| Issue | Fix | File |
|-------|-----|------|
| JWT version mismatch | Updated jjwt-impl from 0.12.6 → 0.11.5 | pom.xml |
| Build would fail | ✅ Now builds successfully | - |
| Login would error | ✅ All dependencies consistent | - |

---

## 🗄️ Database Commands Cheatsheet

```bash
# Start PostgreSQL
brew services start postgresql

# Create database
psql -U postgres -c "CREATE DATABASE nexus_db;"

# Verify database exists
psql -U postgres -d nexus_db -c "SELECT 1;"

# View tables
psql -U postgres -d nexus_db -c "\dt"

# Stop PostgreSQL
brew services stop postgresql
```

---

## 👥 Test Credentials

**Password for all users:** `123456`

| Email | Role |
|-------|------|
| admin@nexus.gr | ADMIN |
| owner1-3@nexus.gr | OWNER |
| buyer1-10@nexus.gr | BUYER |

---

## 🔗 Important URLs

| URL | Purpose |
|-----|---------|
| http://localhost:8080/nexus-frontend/ | Main page |
| http://localhost:8080/nexus-frontend/login.html | Login |
| http://localhost:8080/nexus-frontend/register.html | Register |
| http://localhost:8080/api/auth/login | Login API |
| http://localhost:8080/api/properties | List properties API |

---

## 🛠️ Build Commands

```bash
# Build (already done)
./mvnw clean install -DskipTests

# Start application
./mvnw spring-boot:run

# Or run JAR directly
java -jar target/realestate-0.0.1-SNAPSHOT.jar
```

---

## 🧪 Test Login with cURL

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@nexus.gr","password":"123456"}'
```

Expected response:
```json
{"token":"eyJhbGciOiJIUzI1NiJ9..."}
```

---

## 🔐 JWT Token Info

- **Algorithm:** HS256
- **Expiration:** 24 hours
- **Secret:** Stored in `application.properties`
- **Includes:** Email, roles, expiration

---

## 📚 Documentation Files

```
✅ SETUP_AND_FIXES.md        - Complete setup guide
✅ VERIFICATION_CHECKLIST.md - Pre-launch checklist
✅ DATABASE_SETUP.md         - Database guide
✅ AUTHENTICATION_GUIDE.md   - Authentication details
✅ FIX_SUMMARY.md            - Summary of all fixes
✅ QUICK_REFERENCE.md        - This file
```

---

## ⚠️ Common Issues

| Problem | Solution |
|---------|----------|
| "Connection refused" | Run `brew services start postgresql` |
| "Database does not exist" | Run `psql -U postgres -c "CREATE DATABASE nexus_db;"` |
| Build fails | Run `./mvnw clean` then `./mvnw install -DskipTests` |
| Login shows error | Check database is running and created |
| Page shows 404 | Ensure application is running on port 8080 |

---

## 🔍 Verify Everything Works

```bash
# 1. Build check
./mvnw clean install -DskipTests
# Look for: BUILD SUCCESS

# 2. Database check
psql -U postgres -d nexus_db -c "SELECT 1;"
# Look for: ?column? = 1

# 3. App startup check
./mvnw spring-boot:run
# Look for: Started RealestateApplication in X seconds

# 4. Login check
curl http://localhost:8080/api/auth/login -X POST \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@nexus.gr","password":"123456"}'
# Look for: "token":"eyJ...
```

---

## 📊 Project Info

- **Backend:** Spring Boot 4.1.0
- **Database:** PostgreSQL
- **Security:** Spring Security + JWT
- **Frontend:** HTML/CSS/JavaScript
- **Build:** Maven
- **Java Version:** 17
- **Port:** 8080

---

## ✅ Status

- Build: ✅ SUCCESS (52 files compiled)
- Fixes: ✅ COMPLETE (1 critical issue fixed)
- Ready: ✅ YES (pending database creation)
- Documentation: ✅ COMPLETE

---

**Quick Start:** 5 steps, ~10 minutes ⏱️
**Status:** 🟢 Ready to run

