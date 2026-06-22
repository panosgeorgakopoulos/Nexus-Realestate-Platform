document.addEventListener('DOMContentLoaded', () => {
    initMap();
});

async function initMap() {
    // 1. Δημιουργία Χάρτη (Κέντρο Αθήνα, Zoom 12)
    const map = L.map('nexusMap').setView([37.9838, 23.7275], 12);

    // 2. Προσθήκη του Base Layer (OpenStreetMap)
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    try {
        // 3. Φόρτωση Markers Ακινήτων (Μόνο εγκεκριμένα)
        const properties = await Api.request('/properties');

        properties.forEach(prop => {
            if (prop.lat && prop.lng) {
                // Δημιουργία Marker
                const marker = L.marker([prop.lat, prop.lng]).addTo(map);

                // Δημιουργία Popup (Τίτλος, Τιμή και Link)
                const popupContent = `
                    <h3>€${prop.price.toLocaleString('el-GR')}</h3>
                    <p>${prop.title}<br>${prop.sqm} τ.μ. | ${prop.rooms} Υ/Δ</p>
                    <a href="property-detail.html?id=${prop.id}" class="btn-primary" style="display:inline-block; padding: 5px 10px; font-size:0.8rem;">Δες το ακίνητο</a>
                `;
                marker.bindPopup(popupContent);
            }
        });

        // 4. Φόρτωση Δεδομένων Heatmap (Ζήτηση/Views)
        const heatmapData = await Api.request('/heatmap');

        // Το Leaflet.heat χρειάζεται έναν πίνακα: [[lat, lng, intensity], ...]
        const heatPoints = heatmapData.map(point => [
            point.lat,
            point.lng,
            point.intensity * 10 // Πολλαπλασιάζουμε για να φαίνεται πιο έντονα στον χάρτη
        ]);

        // Προσθήκη του Heatmap Overlay
        if (heatPoints.length > 0) {
            L.heatLayer(heatPoints, {
                radius: 35,
                blur: 20,
                maxZoom: 15,
                gradient: {0.4: 'blue', 0.6: 'lime', 0.8: 'yellow', 1.0: 'red'}
            }).addTo(map);
        }

    } catch (error) {
        console.error("Σφάλμα κατά τη φόρτωση δεδομένων χάρτη:", error);
    }
}