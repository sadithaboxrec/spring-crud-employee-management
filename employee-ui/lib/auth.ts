// lib/auth.ts

// Save tokens after login
export function saveTokens(accessToken: string, refreshToken: string) {
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);
}

// Get the access token
export function getAccessToken(): string | null {
    return localStorage.getItem("accessToken");
}

// Get the refresh token
export function getRefreshToken(): string | null {
    return localStorage.getItem("refreshToken");
}

// Remove tokens on logout
export function clearTokens() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
}

// Decode the JWT and get the payload 
export function decodeToken(token: string): { sub: string; iat: number; exp: number } | null {
    try {
        const payload = token.split(".")[1];             
        const decoded = atob(payload);                   
        return JSON.parse(decoded);
    } catch {
        return null;
    }
}

// Get the username from the token
export function getUsername(): string | null {
    const token = getAccessToken();
    if (!token) return null;
    const decoded = decodeToken(token);
    return decoded?.sub ?? null;            // sub is the username field
}

// Get the user role from the token
export function getUserRole(): string | null {
    const token = getAccessToken();
    if (!token) return null;
    const decoded = decodeToken(token) as any;
    return decoded?.role ?? null;             
}

// Check if the user is logged in (token exists and not expired)
export function isLoggedIn(): boolean {
    const token = getAccessToken();
    if (!token) return false;
    const decoded = decodeToken(token);
    if (!decoded) return false;
    const now = Math.floor(Date.now() / 1000);        
    return decoded.exp > now;                          
}