// resources/static/js/pages/goals.js

const { createApp } = Vue;

// Importa el componente raíz de OBJETIVOS
import GoalManager from '../components/goals/GoalManager.js';

const appElement = document.getElementById('app');

if (appElement) {
    const slug = appElement.dataset.slug;
    const app = createApp(GoalManager, {
        slug: slug 
    });

    app.mount('#app');
}