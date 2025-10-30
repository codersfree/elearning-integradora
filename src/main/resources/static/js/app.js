// resources/static/js/app.js

// Vue está disponible globalmente gracias al CDN
const { createApp } = Vue;

// Importamos nuestro componente raíz
import GoalManager from './components/GoalManager.js';

// Creamos la instancia de la aplicación
const app = createApp({});

// Registramos el componente raíz
// El HTML lo usa como <goal-manager ...>
app.component('goal-manager', GoalManager);

// Montamos la aplicación en el div #app de nuestro HTML
app.mount('#app');