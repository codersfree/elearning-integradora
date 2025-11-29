const { createApp } = Vue;

import CurriculumManager from '../components/curriculum/CurriculumManager.js';

const appElement = document.getElementById('app');

if (appElement) {
    const slug = appElement.dataset.slug;
    const app = createApp(CurriculumManager, {
        slug: slug 
    });

    app.mount('#app');
}