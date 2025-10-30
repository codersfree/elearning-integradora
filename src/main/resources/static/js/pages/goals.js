// resources/static/js/pages/goals.js

const { createApp } = Vue;

// ✅ NUEVA RUTA: Importa desde la subcarpeta
import GoalManager from '../components/goals/GoalManager.js';

// Encuentra el punto de montaje
const appElement = document.getElementById('app');

if (appElement) {
    // Lee el slug que Thymeleaf puso en el HTML
    const slug = appElement.dataset.slug;

    // Crea la aplicación, pasándole el slug como "prop"
    // Esto es mucho más limpio que leerlo desde mounted()
    const app = createApp(GoalManager, {
        slug: slug 
    });

    // Monta la aplicación
    app.mount('#app');
}