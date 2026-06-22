const API_BASE_URL = 'http://localhost:8080/api';

const Api = {
    async request(endpoint, options = {}) {
        const token = Auth.getToken();
        const headers = {
            'Content-Type': 'application/json',
            ...(token && { 'Authorization': `Bearer ${token}` }), // Προσθήκη JWT αν υπάρχει
            ...options.headers
        };

        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            ...options,
            headers
        });

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