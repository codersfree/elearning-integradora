// resources/static/js/pages/requirements.js

const { createApp } = Vue;

// Importa el componente raíz de REQUERIMIENTOS
import RequirementManager from '../components/requirements/RequirementManager.js';

const appElement = document.getElementById('app');

if (appElement) {
    const slug = appElement.dataset.slug;
    const app = createApp(RequirementManager, {
        slug: slug 
    });

    app.mount('#app');
}