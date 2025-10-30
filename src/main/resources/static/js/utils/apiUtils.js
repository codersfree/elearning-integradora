// resources/static/js/utils/apiUtils.js

/**
 * Función central para todas las peticiones API.
 * Encapsula fetch, headers, y el manejo de errores.
 * @param {string} url - El endpoint de la API
 * @param {object} options - Opciones de Fetch (method, body, etc.)
 * @returns {Promise<any>} - El JSON de la respuesta
 */
async function apiRequest(url, options = {}) {
    
    // 1. Configurar headers por defecto
    const defaultHeaders = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    };

    const config = {
        ...options,
        headers: {
            ...defaultHeaders,
            ...options.headers,
        },
    };

    // 2. Realizar la petición
    const response = await fetch(url, config);

    // 3. Procesar el error (tu lógica extraída)
    if (!response.ok) {
        let errorMessage = `Error: ${response.status} ${response.statusText}`;
        try {
            // Intenta parsear el error de Spring Boot
            const errorData = await response.json();
            if (errorData.errors && Array.isArray(errorData.errors)) {
                errorMessage = errorData.errors.map(err => err.defaultMessage).join(', ');
            } else if (errorData.message) {
                errorMessage = errorData.message;
            }
        } catch (e) {
            // El cuerpo del error no era JSON, nos quedamos con el mensaje de arriba
            console.warn('No se pudo parsear el JSON del error de la API.', e);
        }
        // Lanzar el error para que el 'catch' del componente lo reciba
        throw new Error(errorMessage);
    }

    // 4. Devolver la respuesta (si la hay)
    if (response.status === 204) { // 204 No Content
        return null; // El DELETE no devuelve cuerpo
    }
    
    // Si todo fue bien, devuelve el JSON
    return response.json();
}

// 5. Crear y exportar los "atajos" (helpers) que usarán tus componentes
export const api = {
    get: (url) => apiRequest(url, { method: 'GET' }),
    
    post: (url, data) => apiRequest(url, { 
        method: 'POST', 
        body: JSON.stringify(data) 
    }),
    
    put: (url, data) => apiRequest(url, { 
        method: 'PUT', 
        body: JSON.stringify(data) 
    }),
    
    // Usamos 'del' porque 'delete' es una palabra reservada en JS
    del: (url) => apiRequest(url, { method: 'DELETE' }),
};

// Exportación por defecto para importaciones más fáciles
export default api;