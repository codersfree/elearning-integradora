import { template } from './LessonDescriptionForm.template.js';
import { alertStore } from '../../store/alertStore.js';
import api from '../../utils/apiUtils.js';

export default {
    template: template,
    props: ['lesson', 'moduleId'],
    emits: ['description-updated'], // Ya no emite 'cancel' al padre LessonItem
    data() {
        return {
            localDescription: this.lesson.description || '',
            isSaving: false,
            isEditing: false, // Estado interno para alternar entre texto y textarea
        };
    },
    methods: {
        startEditing() {
            this.isEditing = true;
        },

        async saveDescription() {
            this.isSaving = true;
            try {
                const updateDto = {
                    name: this.lesson.name, 
                    description: this.localDescription,
                    isPreview: this.lesson.isPreview,
                    position: this.lesson.position, 
                    duration: this.lesson.duration 
                };

                // Llama al PUT API usando el moduleId
                await api.put(`/api/modules/${this.moduleId}/lessons/${this.lesson.id}`, updateDto);

                alertStore.showMessage('Descripción guardada.', 'success');
                this.$emit('description-updated', this.localDescription); // Informa al padre LessonItem
                this.isEditing = false; // Cierra el editor
                
            } catch (error) {
                alertStore.showMessage('Error al guardar la descripción.', 'danger');
                console.error("Save Description Error:", error);
            } finally {
                this.isSaving = false;
            }
        },
        
        cancelEditing() {
            // Revierte el texto del textarea al valor original de la prop y cierra el editor
            this.localDescription = this.lesson.description || '';
            this.isEditing = false;
        }
    },
    computed: {
        hasDescription() {
            return this.lesson.description && this.lesson.description.trim().length > 0;
        },
        // Mantenemos la descripción local para que el textarea funcione
        // Pero usamos la descripción de la prop para la vista de solo lectura
    }
};