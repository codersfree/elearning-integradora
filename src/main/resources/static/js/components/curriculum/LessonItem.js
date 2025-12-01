import { template } from './LessonItem.template.js';
import LessonVideoForm from './LessonVideoForm.js'; 
import LessonDescriptionForm from './LessonDescriptionForm.js'; 
import api from '../../utils/apiUtils.js';
import { alertStore } from '../../store/alertStore.js';

export default {
    template: template,
    props: ['lesson', 'lessonIndex', 'moduleId'],
    components: {
        'lesson-video-form': LessonVideoForm,
        'lesson-description-form': LessonDescriptionForm 
    },
    emits: ['lesson-deleted', 'lesson-updated'], 
    data() {
        return {
            isExpanded: !this.lesson.videoPath || false, 
            localLesson: { ...this.lesson },
            isEditingName: false,
            isDeleting: false,
            isEditingContent: false, 
            
            showDescriptionForm: false, 
            showResourcesForm: false,
        };
    },
    methods: {
        toggleExpand() {
            this.isExpanded = !this.isExpanded;
        },
        handleVideoUpdated(updatedLesson) {
            Object.assign(this.localLesson, updatedLesson);
            this.isEditingContent = false; 
        },
        
        // --- MANEJO DE DESCRIPCIÓN ---
        toggleDescriptionForm() {
            this.showDescriptionForm = !this.showDescriptionForm;
            this.showResourcesForm = false;
        },

        handleDescriptionUpdated(newDescription) {
            // Actualiza el estado local y cierra el formulario
            this.localLesson.description = newDescription;
            this.showDescriptionForm = false;
        },
        
        // --- Lógica de Edición de Video/Contenido ---
        startContentEditing() {
            this.isEditingContent = true; 
        },
        cancelContentEditing() {
            this.isEditingContent = false;
        },
        
        // --- Lógica de Edición de Nombre y Persistencia (PUT) ---
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
        async updateLessonDetails(key, value) {
             try {
                if (key) this.localLesson[key] = value;

                const updateDto = {
                    name: this.localLesson.name,
                    description: this.localLesson.description,
                    isPreview: this.localLesson.isPreview,
                };

                await api.put(`/api/modules/${this.moduleId}/lessons/${this.localLesson.id}`, updateDto);
                alertStore.showMessage('Lección actualizada.', 'success');
                
                this.$emit('lesson-updated', this.localLesson); 

            } catch (error) {
                console.error("Error al actualizar detalles de la lección:", error);
                alertStore.showMessage('Error al actualizar la lección.', 'danger');
            }
        },
        
        // --- Lógica de Eliminación de Lección ---
        async deleteLesson() {
            if (!confirm(`¿Estás seguro de eliminar la clase "${this.localLesson.name}"?`)) return;

            this.isDeleting = true;
            try {
                await api.del(`/api/modules/${this.moduleId}/lessons/${this.localLesson.id}`);
                
                this.$emit('lesson-deleted', this.localLesson.id, this.moduleId);
                alertStore.showMessage('Clase eliminada con éxito.', 'success');

            } catch (error) {
                alertStore.showMessage('Error al eliminar la clase.', 'danger');
                console.error('Error deleting lesson:', error);
            } finally {
                this.isDeleting = false;
            }
        },

        // Manejadores de Toggles
        handleToggle(key, event) {
            this.updateLessonDetails(key, event.target.checked);
        }
    },
    computed: {
        hasVideo() {
            return this.localLesson.videoPath && this.localLesson.videoPath.length > 0;
        },
        formattedDuration() {
            if (this.localLesson.duration === 0 || !this.localLesson.duration) return '0:00 min';
            const minutes = Math.floor(this.localLesson.duration / 60);
            const seconds = (this.localLesson.duration % 60).toString().padStart(2, '0');
            return `${minutes}:${seconds} min`;
        }
    }
};