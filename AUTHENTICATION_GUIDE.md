# 🔐 Login & Authentication Flow Guide

## Overview

The application uses **JWT (JSON Web Tokens)** with **Spring Security** for authentication and authorization.

---

## Complete Authentication Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    LOGIN PROCESS FLOW                          │
└─────────────────────────────────────────────────────────────────┘

1. USER INPUT
   └─> Email: admin@nexus.gr
   └─> Password: 123456

2. FRONTEND (login.html)
   └─> Sends POST /api/auth/login
   └─> Body: {"email": "admin@nexus.gr", "password": "123456"}

3. BACKEND (AuthController)
   ├─> Receives LoginRequest
   ├─> Attempts authentication via AuthenticationManager
   │   ├─> Loads user from database via UserDetailsService
   │   ├─> Compares provided password with stored hash (BCrypt)
   │   └─> If match: Authentication succeeds
   │       If no match: Authentication fails (return 401)
   │
   └─> If authentication successful:
       ├─> Loads UserDetails (includes roles)
       ├─> Generates JWT Token with claims
       └─> Returns {"token": "eyJhbGciOi..."}

4. FRONTEND STORAGE
   └─> localStorage.setItem('nexus_token', token)

5. SUBSEQUENT REQUESTS
   ├─> All requests include: Authorization: Bearer <TOKEN>
   │
   └─> BACKEND processes:
       ├─> JwtAuthFilter intercepts request
       ├─> Extracts token from Authorization header
       ├─> Validates token signature and expiration
       ├─> Extracts user details and roles from token
       └─> Sets SecurityContext for request processing

6. LOGOUT
   └─> localStorage.removeItem('nexus_token')
   └─> Redirect to index.html
```

---

## Key Components

### 1. **AuthController** (`/api/auth`)
**Location:** `src/main/java/com/nexus/realestate/controller/AuthController.java`

```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    // 1. Authenticate user
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(), 
            request.getPassword()
        )
    );
    
    // 2. Load user details
    final UserDetails userDetails = userDetailsService.loadUserByUsername(
        request.getEmail()
    );
    
    // 3. Generate JWT token
    final String jwt = jwtUtil.generateToken(userDetails);
    
    // 4. Return token
    return ResponseEntity.ok(new HashMap<String, String>() {{
        put("token", jwt);
    }});
}

@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    // Create new user account
    authService.registerUser(request);
    return ResponseEntity.ok("Η εγγραφή ολοκληρώθηκε με επιτυχία!");
}
```

### 2. **UserDetailsServiceImpl**
**Location:** `src/main/java/com/nexus/realestate/security/UserDetailsServiceImpl.java`

```java
@Override
public UserDetails loadUserByUsername(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException(
            "Ο χρήστης με email " + email + " δεν βρέθηκε"
        ));
    
    return org.springframework.security.core.userdetails.User
        .withUsername(user.getEmail())
        .password(user.getPasswordHash())  // BCrypt hash
        .roles(user.getRole().name())      // BUYER, OWNER, ADMIN
        .build();
}
```

### 3. **JwtUtil**
**Location:** `src/main/java/com/nexus/realestate/security/JwtUtil.java`

```java
public String generateToken(UserDetails userDetails) {
    // Create claims (payload)
    Map<String, Object> claims = new HashMap<>();
    List<String> roles = userDetails.getAuthorities().stream()
        .map(auth -> auth.getAuthority())
        .toList();
    claims.put("roles", roles);
    
    // Create token
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(userDetails.getUsername())  // email
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))  // 24 hours
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
}
```

### 4. **JwtAuthFilter**
**Location:** `src/main/java/com/nexus/realestate/security/JwtAuthFilter.java`

```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                               FilterChain filterChain) {
    // 1. Extract token from Authorization header
    final String authHeader = request.getHeader("Authorization");
    String username = null;
    String jwt = null;
    
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        jwt = authHeader.substring(7);
        username = jwtUtil.extractUsername(jwt);
    }
    
    // 2. If token exists and user not already authenticated
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        // 3. Validate token
        if (jwtUtil.validateToken(jwt, userDetails)) {
            // 4. Set authentication in SecurityContext
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );
            authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
    
    // 5. Continue filter chain
    filterChain.doFilter(request, response);
}
```

### 5. **SecurityConfig**
**Location:** `src/main/java/com/nexus/realestate/security/SecurityConfig.java`

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // Enable CORS
        .cors(cors -> cors.configurationSource(...))
        
        // Disable CSRF (stateless API)
        .csrf(csrf -> csrf.disable())
        
        // Stateless session management
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        
        // Authorization rules
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()           // Login/Register
            .requestMatchers("/api/properties").permitAll()        // List properties
            .requestMatchers("/api/properties/{id}").permitAll()   // View property
            .requestMatchers("/api/admin/**").hasRole("ADMIN")     // Admin only
            .requestMatchers("/api/preferences/**").hasRole("BUYER") // Buyer only
            .anyRequest().authenticated()
        )
        
        // Add JWT filter
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```

---

## JWT Token Structure

### Raw Token Example
```
eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIl0sImlhdCI6MTcxODk5MDAwMCwic3ViIjoiYWRtaW5AbmV4dXMuZ3IiLCJleHAiOjE3MTkwNzY0MDB9.aBcDeFgHiJkLmNoPqRsTuVwXyZ
```

### Decoded Structure
```
HEADER (eyJhbGciOiJIUzI1NiJ9)
{
  "alg": "HS256",
  "typ": "JWT"
}

PAYLOAD (eyJyb2xlcyI6WyJST0xFX0FETUlOIl0sImlhdCI6MTcxODk5MDAwMCwic3ViIjoiYWRtaW5AbmV4dXMuZ3IiLCJleHAiOjE3MTkwNzY0MDB9)
{
  "roles": ["ROLE_ADMIN"],
  "iat": 1718990000,           // Issued at
  "sub": "admin@nexus.gr",     // Subject (username/email)
  "exp": 1719076400             // Expiration time
}

SIGNATURE (aBcDeFgHiJkLmNoPqRsTuVwXyZ)
HMAC-SHA256(
  base64(header) + "." + base64(payload),
  secret_key
)
```

---

## Frontend Login Flow

### login.html
```html
<form id="loginForm">
  <input type="email" id="email" required>
  <input type="password" id="password" required>
  <button type="submit">Σύνδεση</button>
</form>

<script src="js/auth.js"></script>
<script src="js/api.js"></script>
<script>
  document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const payload = {
      email: document.getElementById('email').value,
      password: document.getElementById('password').value
    };
    
    try {
      // Call API
      const data = await Api.request('/auth/login', {
        method: 'POST',
        body: JSON.stringify(payload)
      });
      
      if(data.token) {
        // Store token
        Auth.setToken(data.token);
        // Redirect to dashboard
        window.location.href = 'index.html';
      }
    } catch (err) {
      alert('Λάθος email ή κωδικός.');
    }
  });
</script>
```

### auth.js
```javascript
const Auth = {
  // Store token in localStorage
  setToken: (token) => {
    localStorage.setItem('nexus_token', token);
  },
  
  // Retrieve token from localStorage
  getToken: () => {
    return localStorage.getItem('nexus_token');
  },
  
  // Remove token and logout
  logout: () => {
    localStorage.removeItem('nexus_token');
    window.location.href = '/index.html';
  },
  
  // Check if user is logged in
  isLoggedIn: () => {
    return !!localStorage.getItem('nexus_token');
  },
  
  // Extract user role from JWT
  getUserRole: () => {
    const token = localStorage.getItem('nexus_token');
    if (!token) return null;
    
    try {
      // Decode JWT payload (second part, base64)
      const payload = JSON.parse(atob(token.split('.')[1]));
      
      // Extract first role and remove ROLE_ prefix
      return payload.roles ? payload.roles[0].replace('ROLE_', '') : null;
    } catch (e) {
      return null;
    }
  }
};
```

### api.js
```javascript
const API_BASE_URL = 'http://localhost:8080/api';

const Api = {
  async request(endpoint, options = {}) {
    const token = Auth.getToken();
    
    // Add token to Authorization header if exists
    const headers = {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
      ...options.headers
    };
    
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      ...options,
      headers
    });
    
    // Handle 401/403 errors
    if (response.status === 401 || response.status === 403) {
      console.error("Μη εξουσιοδοτημένη πρόσβαση.");
      Auth.logout();
    }
    
    const data = await response.json().catch(() => ({}));
    
    if (!response.ok) {
      throw new Error(data.message || 'Σφάλμα στο API Request');
    }
    
    return data;
  }
};
```

---

## Password Security

### Password Hashing
- **Algorithm:** BCrypt
- **Cost Factor:** 10
- **Stored in:** Database column `password_hash`

### Password Creation (Registration)
```java
User user = new User();
user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
// BCrypt example:
// Plain: "123456"
// Hashed: "$2a$10$YZ1K8vZ5K8vZ5K8vZ5K8..."
```

### Password Verification (Login)
```java
// AuthenticationManager internally uses BCryptPasswordEncoder
// to verify that provided password matches the hash
authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(
        request.getEmail(),           // username
        request.getPassword()         // plain password (auto-hashed and compared)
    )
);
```

---

## Token Expiration

### Expiration Settings
```java
// JwtUtil.java
private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;  // 24 hours

// Token expires 24 hours after creation
setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
```

### Expired Token Handling
- Expired tokens are rejected during validation
- Frontend receives 401 Unauthorized response
- User is redirected to login page

### Refresh Token (Not Implemented)
To add token refresh capability:
```java
// Could add:
@PostMapping("/auth/refresh")
public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) {
    // Validate token
    // Generate new token
    // Return new token
}
```

---

## Error Handling

### Login Errors

| Error | HTTP Status | Cause | Solution |
|-------|-----------|-------|----------|
| Bad Credentials | 401 | Wrong password | Use correct password |
| User Not Found | 401 | Email doesn't exist | Register first or use valid email |
| Invalid Token | 401 | Token expired or tampered | Login again |
| Missing Auth Header | 401 | No token sent | Include Authorization header |
| Malformed Token | 401 | Token format invalid | Ensure "Bearer " prefix |

### Response Examples

**Successful Login:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIl0sInN1YiI6ImFkbWluQG5leHVzLmdyIn0.aBcDefGhIjKlMnOpQrStUvWxYz"
}
```

**Login Error:**
```json
{
  "message": "Λάθος email ή κωδικός!"
}
```

**Unauthorized Access:**
```json
{
  "error": "Unauthorized",
  "message": "Access Denied"
}
```

---

## Security Best Practices Implemented

✅ **Password Security**
- Passwords hashed with BCrypt
- Passwords never logged or returned in API responses
- `@JsonIgnore` on password_hash field

✅ **Token Security**
- JWT signed with HS256 and secret key
- Token includes expiration (24 hours)
- Tokens validated on every request

✅ **CORS Protection**
- CORS configured for development
- All headers allowed in CORS
- Can be restricted for production

✅ **CSRF Protection**
- CSRF disabled (stateless API)
- Safe for token-based authentication

✅ **Authentication**
- Stateless session management
- Every request validates token
- User roles enforced

✅ **Authorization**
- Role-based access control (RBAC)
- Different endpoints for different roles
- Admin, Owner, Buyer roles

---

## Testing Authentication

### Using cURL

```bash
# 1. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@nexus.gr","password":"123456"}'

# Response:
# {"token":"eyJhbGciOiJIUzI1NiJ9..."}

# 2. Use token for protected endpoint
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X GET http://localhost:8080/api/admin/properties \
  -H "Authorization: Bearer $TOKEN"

# 3. Access without token (should fail)
curl -X GET http://localhost:8080/api/admin/properties
# Response: 401 Unauthorized
```

### Using Postman

1. **Create Login Request**
   - Method: POST
   - URL: http://localhost:8080/api/auth/login
   - Body (JSON):
     ```json
     {
       "email": "admin@nexus.gr",
       "password": "123456"
     }
     ```

2. **Save Token**
   - Copy token from response
   - Use as environment variable: `{{token}}`

3. **Use Token in Requests**
   - Header: `Authorization`
   - Value: `Bearer {{token}}`

---

## Troubleshooting Login Issues

### Issue 1: "User Not Found"
```
UsernameNotFoundException: Ο χρήστης με email admin@nexus.gr δεν βρέθηκε
```
**Cause:** User doesn't exist in database
**Solution:**
- Register first or use seeded credentials
- Ensure database was populated by DataSeeder

### Issue 2: "Bad Credentials"
```
BadCredentialsException: Bad credentials
```
**Cause:** Wrong password
**Solution:**
- Verify password is correct
- Default test password: `123456`

### Issue 3: "Invalid Token"
```
ExpiredJwtException: JWT claims should have expired time
```
**Cause:** Token expired (24 hours)
**Solution:**
- Login again to get new token

### Issue 4: "CORS Error"
```
Access to XMLHttpRequest has been blocked by CORS policy
```
**Cause:** Browser CORS restriction
**Solution:**
- Ensure CORS is properly configured in SecurityConfig
- Already configured - should not occur

---

## Token Decode Tool

To decode JWT tokens, use: https://jwt.io

1. Paste your token
2. See the decoded structure
3. Verify payload contains expected claims
4. Note the expiration time

---

**Status:** ✅ Complete  
**Last Updated:** June 22, 2026

