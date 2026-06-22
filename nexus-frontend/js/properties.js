// js/properties.js

document.addEventListener('DOMContentLoaded', () => {
    setupNavigation();
    loadProperties(); // Φορτώνει όλα τα εγκεκριμένα με το που ανοίγει η σελίδα

    // Event Listener για τη Smart Search Bar
    document.getElementById('searchBtn').addEventListener('click', () => {
        const query = document.getElementById('searchInput').value;
        if(query.trim() !== '') {
            // Καλεί το endpoint δυναμικής αναζήτησης (το query θα περάσει από τον NLP Parser στη Java)
            loadProperties(`/search?query=${encodeURIComponent(query)}`);
        } else {
            loadProperties();
        }
    });
});

function setupNavigation() {
    const nav = document.getElementById('nav-dynamic');
    const buyerActions = document.getElementById('buyerActions');

    if (Auth.isLoggedIn()) {
        nav.innerHTML = `<li><a href="index.html" onclick="Auth.logout()">Αποσύνδεση</a></li>`;

        if (Auth.getUserRole() === 'BUYER') {
            // Αν είναι αγοραστής, του δίνουμε το κουμπί για τα AI Προτεινόμενα
            buyerActions.innerHTML = `
                <button id="btnRecommended" class="btn-primary" style="background-color: #10b981;">
                    ✨ Δείτε τα AI Προτεινόμενα για εσάς
                </button>
            `;
            document.getElementById('btnRecommended').addEventListener('click', loadRecommended);
        }
    } else {
        nav.innerHTML = `<li><a href="login.html">Σύνδεση</a></li>`;
    }
}

// Φόρτωση κανονικών ή αναζητημένων ακινήτων (Public)
async function loadProperties(endpointExt = '') {
    try {
        const properties = await Api.request(`/properties${endpointExt}`);
        renderGrid(properties, false);
    } catch (e) { console.error('Σφάλμα φόρτωσης ακινήτων:', e); }
}

// Φόρτωση ΜΟΝΟ των προτεινόμενων από το Matching Engine (Auth: BUYER)
async function loadRecommended() {
    try {
        // Υπενθύμιση: το /recommended επιστρέφει λίστα από αντικείμενα "Match" (έχει score και property)
        const matches = await Api.request('/properties/recommended');
        renderGrid(matches, true);
    } catch (e) {
        alert('Πρέπει να δημιουργήσετε Προφίλ Αναγκών πρώτα!');
        window.location.href = 'preferences.html';
    }
}

// Δημιουργία των HTML καρτών
function renderGrid(dataArray, isMatchData) {
    const grid = document.getElementById('propertiesGrid');
    grid.innerHTML = '';

    if(dataArray.length === 0) {
        grid.innerHTML = '<p style="grid-column: 1/-1; text-align: center; font-size: 1.2rem;">Δεν βρέθηκαν ακίνητα με αυτά τα κριτήρια.</p>';
        return;
    }

    dataArray.forEach(item => {
        // Αν καλέσαμε το /recommended, τα δεδομένα είναι τυλιγμένα στο αντικείμενο Match
        const property = isMatchData ? item.property : item;
        const score = isMatchData ? item.score : null;

        const card = document.createElement('div');
        card.className = 'property-card';

        // Λογική για το Badge (Βάσει του Οδηγού)
        let badgeHtml = '';
        if (score !== null) {
            let badgeClass = 'badge-poor';
            let badgeText = 'Χαμηλή Αντιστοίχιση';

            if (score >= 80) { badgeClass = 'badge-excellent'; badgeText = 'Εξαιρετική Αντιστοίχιση'; }
            else if (score >= 65) { badgeClass = 'badge-good'; badgeText = 'Καλή Αντιστοίχιση'; }
            else if (score >= 50) { badgeClass = 'badge-fair'; badgeText = 'Μέτρια Αντιστοίχιση'; }

            badgeHtml = `<div class="match-badge ${badgeClass}">${score}% - ${badgeText}</div>`;
        }

        card.innerHTML = `
            ${badgeHtml}
            <div class="property-image">
                <span>[Φωτογραφία Ακινήτου]</span>
            </div>
            <div class="property-info">
                <div class="property-price">€${property.price.toLocaleString('el-GR')}</div>
                <h3 style="margin-bottom: 0.5rem; color: #374151;">${property.title}</h3>
                <p style="color: #6b7280; margin-bottom: 1rem;">📍 ${property.areaName || 'Άγνωστη Περιοχή'} | 🏠 ${property.sqm} τ.μ.</p>
                <div style="display: flex; gap: 1rem; color: #4b5563; font-size: 0.9rem; margin-bottom: 1.5rem;">
                    <span>🛏️ ${property.rooms} Υ/Δ</span>
                    <span>🏢 Όροφος: ${property.floor || '-'}</span>
                </div>
                <a href="property-detail.html?id=${property.id}" class="btn-primary" style="display: block; text-align: center;">Προβολή Λεπτομερειών</a>
            </div>
        `;
        grid.appendChild(card);
    });
}