// resources/static/js/utils/apiUtils.js

/**
 * Función central para todas las peticiones API.
 * Encapsula fetch, headers, y el manejo de errores.
 * @param {string} url - El endpoint de la API
 * @param {object} options - Opciones de Fetch (method, body, etc.)
 * @returns {Promise<any>} - El JSON de la respuesta
 */
async function apiRequest(url, options = {}) {
    
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

    const response = await fetch(url, config);

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
            console.warn('No se pudo parsear el JSON del error de la API.', e);
        }
        

        throw new Error(errorMessage);
    }

    if (response.status === 204) { 
        return null;
    }
        
    return response.json();
}

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
    
    del: (url) => apiRequest(url, { method: 'DELETE' }),
};

export default api;