const Auth = {
    setToken: (token) => {
        localStorage.setItem('nexus_token', token);
    },
    getToken: () => {
        return localStorage.getItem('nexus_token');
    },
    logout: () => {
        localStorage.removeItem('nexus_token');
        window.location.href = '/index.html';
    },
    isLoggedIn: () => {
        return !!localStorage.getItem('nexus_token');
    },
    // Αποκωδικοποίηση του JWT για να δούμε τον ρόλο του χρήστη
    getUserRole: () => {
        const token = localStorage.getItem('nexus_token');
        if (!token) return null;
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            // To Spring Security βάζει τους ρόλους σε ένα array "roles" (ή "authorities")
            return payload.roles ? payload.roles[0].replace('ROLE_', '') : null;
        } catch (e) {
            return null;
        }
    }
};