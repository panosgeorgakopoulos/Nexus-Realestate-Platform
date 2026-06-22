# 🗄️ Database Connection & Setup Guide

## PostgreSQL Installation & Setup

### Installation (macOS)

#### Option 1: Homebrew (Recommended)
```bash
# Install PostgreSQL
brew install postgresql@15

# Start service
brew services start postgresql@15

# Stop service (when needed)
brew services stop postgresql@15

# Restart service
brew services restart postgresql@15

# Check service status
brew services list
```

#### Option 2: Using Default PostgreSQL
```bash
# Install with Homebrew
brew install postgresql

# Start the database server
postgres -D /usr/local/var/postgres &

# Or as a service
brew services start postgresql
```

---

## Database Creation

### Step 1: Connect to PostgreSQL
```bash
# Connect as the default 'postgres' user
psql -U postgres

# You should see the psql prompt:
# postgres=#
```

### Step 2: Create the Database
```sql
-- Create the database
CREATE DATABASE nexus_db;

-- Verify it was created
\l

-- You should see output like:
-- Name    |  Owner   | Encoding | Collate | Ctype |   Access privileges
-- nexus_db | postgres | UTF8     | C       | C     |
```

### Step 3: Create User (Optional - Already using postgres)
```sql
-- Create a new user (if needed)
CREATE USER nexus_user WITH PASSWORD 'nexus_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE nexus_db TO nexus_user;

-- Exit psql
\q
```

### Step 4: Verify Connection
```bash
# Test connection as postgres user
psql -U postgres -d nexus_db -c "SELECT 1;"

# Expected output:
# ?column?
# ----------
#        1
# (1 row)
```

---

## Application Configuration

### File: `src/main/resources/application.properties`

The application is already configured with:

```properties
# PostgreSQL Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/nexus_db
spring.datasource.username=postgres
spring.datasource.password=GPanos@2005

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=true

# JWT Secret (Base64 encoded)
jwt.secret=9a4f434556513571537638792F423F4528482B4D6251655468576D5A71347437
```

### Configuration Breakdown

| Property | Value | Purpose |
|----------|-------|---------|
| `datasource.url` | `jdbc:postgresql://localhost:5432/nexus_db` | PostgreSQL server URL |
| `datasource.username` | `postgres` | Default PostgreSQL user |
| `datasource.password` | `GPanos@2005` | User password |
| `hibernate.ddl-auto` | `update` | Auto-create/update schema |
| `jpa.show-sql` | `true` | Log SQL statements |

---

## Testing Database Connection

### Method 1: Using psql Command Line
```bash
# Test basic connection
psql -U postgres -d nexus_db -c "SELECT 1;"

# View all databases
psql -U postgres -c "\l"

# View all tables (if any exist)
psql -U postgres -d nexus_db -c "\dt"

# View table schemas
psql -U postgres -d nexus_db -c "\d+ users"
```

### Method 2: Using Java/Spring
The application automatically tests the connection on startup. Check logs for:

```
Initializing connection pool...
Connected to PostgreSQL 12.x...
Creating tables...
Seeding initial data...
Application started successfully on http://localhost:8080
```

### Method 3: Verify Through Application

1. Start the application
2. Open browser: `http://localhost:8080/api/properties`
3. Should return JSON (even if empty)

---

## Connection Troubleshooting

### Issue 1: "Connection refused"

**Symptoms:**
```
org.postgresql.util.PSQLException: Connection refused
```

**Causes:**
- PostgreSQL not running
- Wrong hostname/port
- Firewall blocking connection

**Solutions:**
```bash
# Check if PostgreSQL is running
brew services list | grep postgresql

# Start PostgreSQL
brew services start postgresql

# Verify it's listening on port 5432
lsof -i :5432

# Should show something like:
# COMMAND     PID      USER   FD   TYPE DEVICE SIZE/OFF NODE NAME
# postgres   1234     user    6u  IPv4 12345      0t0  localhost:postgresql
```

### Issue 2: "Database does not exist"

**Symptoms:**
```
org.postgresql.util.PSQLException: ERROR: database "nexus_db" does not exist
```

**Solution:**
```bash
# Create the database
psql -U postgres -c "CREATE DATABASE nexus_db;"

# Verify
psql -U postgres -d nexus_db -c "SELECT 1;"
```

### Issue 3: "Password authentication failed"

**Symptoms:**
```
org.postgresql.util.PSQLException: FATAL: password authentication failed for user "postgres"
```

**Causes:**
- Wrong password in `application.properties`
- PostgreSQL user doesn't have the password set

**Solutions:**
```bash
# Connect with psql (no password if just installed)
psql -U postgres

# Set the password for postgres user
ALTER USER postgres WITH PASSWORD 'GPanos@2005';

# Exit
\q

# Verify new password works
psql -U postgres -W

# When prompted, enter: GPanos@2005
```

### Issue 4: "Port already in use"

**Symptoms:**
```
Address already in use: bind
```

**Causes:**
- Another instance of PostgreSQL running
- Another application using port 5432

**Solutions:**
```bash
# Find process using port 5432
lsof -i :5432

# Kill the process (if needed)
kill -9 <PID>

# Or change the port in application.properties
# (not recommended - use default 5432)
```

### Issue 5: "No suitable driver found"

**Symptoms:**
```
java.sql.SQLException: No suitable driver found for jdbc:postgresql://localhost:5432/nexus_db
```

**Causes:**
- PostgreSQL JDBC driver not in classpath
- Build not completed

**Solutions:**
```bash
# Rebuild the project
./mvnw clean install -DskipTests

# Verify postgresql dependency exists in pom.xml
./mvnw dependency:tree | grep postgresql
```

---

## Advanced PostgreSQL Commands

### Useful psql Commands

```bash
# Connect to specific database
psql -U postgres -d nexus_db

# List all databases
\l

# List all tables
\dt

# Show table structure
\d+ <table_name>
# Example:
\d+ users

# Execute SQL command
psql -U postgres -d nexus_db -c "SELECT * FROM users LIMIT 5;"

# Export database
pg_dump -U postgres -d nexus_db > backup.sql

# Import database
psql -U postgres -d nexus_db < backup.sql

# Show current user
SELECT current_user;

# Show current database
SELECT current_database();

# List all users
\du

# Change password for current user
\password
```

---

## Connection Pool Settings

The application uses HikariCP for connection pooling. Default settings in Hibernate:

```properties
# Connection pool configuration (default values)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.auto-commit=true
```

To modify, add to `application.properties`:
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

---

## Monitoring Database Connection

### View Connection Logs

The application logs database connections. Start with:
```bash
./mvnw spring-boot:run
```

Watch for these messages:
```
INFO  : Initializing connection pool...
INFO  : HikariPool-1 - Starting...
INFO  : HikariPool-1 - Start completed.
INFO  : Database initialization started
INFO  : Database initialization completed
```

### Check PostgreSQL Logs

```bash
# PostgreSQL logs location (varies by installation)
# Homebrew: /usr/local/var/log/postgres.log

tail -f /usr/local/var/log/postgres.log

# Or check PostgreSQL logs directory
tail -f /usr/local/var/log/postgres/
```

---

## Security Best Practices

### Current Setup (Development)
```properties
spring.datasource.password=GPanos@2005
```

### For Production
1. **Use Environment Variables**
   ```bash
   export DB_PASSWORD=your_secure_password
   ```
   
   ```properties
   spring.datasource.password=${DB_PASSWORD}
   ```

2. **Use Secrets Manager**
   - AWS Secrets Manager
   - Azure Key Vault
   - HashiCorp Vault

3. **Use SSL Connection**
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/nexus_db?ssl=true&sslmode=require
   ```

4. **Create Limited Permission User**
   ```sql
   -- Create application user
   CREATE USER nexus_app WITH PASSWORD 'secure_password';
   
   -- Grant only necessary permissions
   GRANT CONNECT ON DATABASE nexus_db TO nexus_app;
   GRANT USAGE ON SCHEMA public TO nexus_app;
   GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO nexus_app;
   ```

---

## Performance Tuning

### Increase Connection Pool Size
```properties
spring.datasource.hikari.maximum-pool-size=30
```

### Enable SQL Query Logging (Debug)
```properties
# Log SQL statements
logging.level.org.hibernate.SQL=DEBUG

# Log SQL parameters
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Connection String with Parameters
```properties
# Add connection parameters for performance
spring.datasource.url=jdbc:postgresql://localhost:5432/nexus_db?currentSchema=public&prepareThreshold=5

# Connection timeout
spring.datasource.hikari.connection-timeout=30000

# Idle timeout (5 minutes)
spring.datasource.hikari.idle-timeout=300000
```

---

## Quick Reference: Complete Setup

```bash
# 1. Install PostgreSQL
brew install postgresql@15

# 2. Start PostgreSQL
brew services start postgresql@15

# 3. Create database
psql -U postgres -c "CREATE DATABASE nexus_db;"

# 4. Verify connection
psql -U postgres -d nexus_db -c "SELECT 1;"

# 5. Navigate to project
cd /Users/panosgeorgakopoulos/IdeaProjects/realestate

# 6. Build
./mvnw clean install -DskipTests

# 7. Run
./mvnw spring-boot:run

# 8. Access
# http://localhost:8080/nexus-frontend/login.html
# admin@nexus.gr / 123456
```

---

## Additional Resources

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Hibernate Configuration](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html)
- [HikariCP Configuration](https://github.com/brettwooldridge/HikariCP/wiki/Configuration)

---

**Status:** ✅ Ready to connect  
**Last Updated:** June 22, 2026

