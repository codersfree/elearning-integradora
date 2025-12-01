import { template } from './LessonDescriptionForm.template.js';
import { alertStore } from '../../store/alertStore.js';
import api from '../../utils/apiUtils.js';

export default {
    template: template,
    props: ['lesson', 'moduleId'], // ⬅️ AHORA RECIBE moduleId
    emits: ['description-updated', 'cancel'],
    data() {
        return {
            // Inicializa con la descripción actual de la lección
            localDescription: this.lesson.description || '',
            isSaving: false,
        };
    },
    methods: {
        async saveDescription() {
            this.isSaving = true;
            try {
                // DTO de actualización: Incluye todos los campos necesarios para JPA
                const updateDto = {
                    name: this.lesson.name, 
                    description: this.localDescription,
                    isPreview: this.lesson.isPreview,
                    
                    // Incluir campos requeridos por el backend (aunque no se editen)
                    position: this.lesson.position, 
                    duration: this.lesson.duration 
                };

                // CRUCIAL: USAR this.moduleId de la prop para construir la URL
                await api.put(`/api/modules/${this.moduleId}/lessons/${this.lesson.id}`, updateDto);

                alertStore.showMessage('Descripción guardada.', 'success');
                this.$emit('description-updated', this.localDescription);
                
            } catch (error) {
                alertStore.showMessage(`Error al guardar la descripción: ${error.message}`, 'danger');
                console.error("Save Description Error:", error);
            } finally {
                this.isSaving = false;
            }
        },
        cancel() {
            // Revierte la descripción local a la original de la prop y cierra el formulario
            this.localDescription = this.lesson.description || '';
            this.$emit('cancel');
        }
    },
};