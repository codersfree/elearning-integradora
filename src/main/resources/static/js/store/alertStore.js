// resources/static/js/store/alertStore.js

const { reactive } = Vue;

export const alertStore = reactive({
    message: null,
    messageType: 'success',
    timeoutId: null,

    /**
     * Muestra un mensaje de alerta.
     * @param {string} text - El mensaje a mostrar.
     * @param {string} type - 'success' o 'danger'.
     * @param {number} duration - Duración en milisegundos.
     */
    showMessage(text, type = 'success', duration = 4000) {

        if (this.timeoutId) {
            clearTimeout(this.timeoutId);
        }
        
        this.message = text;
        this.messageType = type;

        if (duration) {
            this.timeoutId = setTimeout(() => this.clearMessage(), duration);
        }
    },

    /**
     * Oculta el mensaje de alerta.
     */
    clearMessage() {
        this.message = null;
        this.timeoutId = null;
    }
});