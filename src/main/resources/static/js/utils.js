// public/js/utils.js

/**
 * Obtiene los encabezados CSRF de las metaetiquetas del DOM
 * para las peticiones a Spring Security.
 */
export function getCsrfHeaders() {
    const token = document.querySelector("meta[name='_csrf']").getAttribute("content");
    const header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
    const headers = {
        'Content-Type': 'application/json',
    };
    headers[header] = token;
    return headers;
}