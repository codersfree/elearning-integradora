import { template } from './LessonItem.template.js';
import LessonVideoForm from './LessonVideoForm.js'; 
import api from '../../utils/apiUtils.js';
import { alertStore } from '../../store/alertStore.js';

export default {
    template: template,
    props: ['lesson', 'lessonIndex', 'moduleId'], // AÑADIDO: moduleId
    components: {
        'lesson-video-form': LessonVideoForm
    },
    // Emitimos 'lesson-deleted' al padre (CurriculumManager)
    emits: ['lesson-deleted', 'lesson-updated'], 
    data() {
        return {
            // Abierto por defecto si no hay video
            isExpanded: !this.lesson.videoPath || false, 
            localLesson: { ...this.lesson },
            isEditingName: false,
            isDeleting: false, // Estado para el spinner de eliminar lección
        };
    },
    methods: {
        toggleExpand() {
            this.isExpanded = !this.isExpanded;
        },
        handleVideoUpdated(updatedLesson) {
            // Actualiza los datos locales (videoPath, duration)
            Object.assign(this.localLesson, updatedLesson);
        },
        
        // --- Lógica de Edición de Nombre ---
        startEditName() {
            this.isEditingName = true;
        },
        async finishEditName() {
            if (!this.localLesson.name.trim()) {
                 alertStore.showMessage('El nombre de la lección no puede estar vacío.', 'danger');
                 return;
            }
            this.isEditingName = false;
            await this.updateLessonDetails('name', this.localLesson.name);
        },
        
        // --- Lógica de Persistencia de Detalles ---
        async updateLessonDetails(key, value) {
             try {
                if (key) this.localLesson[key] = value;

                // DTO de actualización
                const updateDto = {
                    name: this.localLesson.name,
                    description: this.localLesson.description,
                    isPreview: this.localLesson.isPreview,
                };

                // Llamada a la API PUT /api/modules/{moduleId}/lessons/{lessonId}
                await api.put(`/api/modules/${this.moduleId}/lessons/${this.localLesson.id}`, updateDto);
                alertStore.showMessage('Lección actualizada.', 'success');
                
                this.$emit('lesson-updated', this.localLesson); 

            } catch (error) {
                console.error("Error al actualizar detalles de la lección:", error);
                alertStore.showMessage('Error al actualizar la lección.', 'danger');
            }
        },
        
        // --- Lógica de Eliminación de Lección (Completo) ---
        async deleteLesson() {
            if (!confirm(`¿Estás seguro de eliminar la clase "${this.localLesson.name}"?`)) return;

            this.isDeleting = true;
            try {
                // Endpoint: DELETE /api/modules/{moduleId}/lessons/{lessonId}
                // Usamos this.moduleId de las props para asegurar la ruta.
                await api.del(`/api/modules/${this.moduleId}/lessons/${this.localLesson.id}`);
                
                // Emitimos el evento de eliminación para que CurriculumManager la remueva de la lista
                this.$emit('lesson-deleted', this.localLesson.id, this.moduleId);
                alertStore.showMessage('Clase eliminada con éxito.', 'success');

            } catch (error) {
                alertStore.showMessage('Error al eliminar la clase.', 'danger');
                console.error('Error deleting lesson:', error);
            } finally {
                this.isDeleting = false;
            }
        },

        // --- Manejadores de Toggles ---
        handleToggle(key, event) {
            this.updateLessonDetails(key, event.target.checked);
        }
    },
    computed: {
        hasVideo() {
            return this.localLesson.videoPath && this.localLesson.videoPath.length > 0;
        },
        formattedDuration() {
            if (this.localLesson.duration === 0) return '0:00 min';
            const minutes = Math.floor(this.localLesson.duration / 60);
            const seconds = (this.localLesson.duration % 60).toString().padStart(2, '0');
            return `${minutes}:${seconds} min`;
        }
    }
};