import { template } from './CurriculumManager.template.js';
import api from '../../utils/apiUtils.js';
import { alertStore } from '../../store/alertStore.js';

// --- Importación de Componentes Anidados (Ajusta según tu proyecto) ---
import SectionForm from './SectionForm.js';
import LessonForm from './LessonForm.js';
import LessonItem from './LessonItem.js'; 
import AlertMessage from '../common/AlertMessage.js';

export default {
    template: template,
    props: ['slug'],
    components: {
        'section-form': SectionForm,
        'lesson-form': LessonForm,
        'lesson-item': LessonItem, 
        'alert-message': AlertMessage,
    },
    data() {
        return {
            sections: [],
            isLoading: false,
            showAddForm: false,
            showAddLessonForm: {}, 
            
            // ESTADOS DE EDICIÓN DE SECCIÓN REQUERIDOS POR EL TEMPLATE
            editingId: null,
            editingName: '',
            isUpdating: false,
            isDeleting: {}, // Usado para spinners de eliminación
        };
    },
    mounted() {
        this.fetchCurriculum();
    },
    methods: {
        async fetchCurriculum() {
            this.isLoading = true;
            try {
                const data = await api.get(`/api/courses/${this.slug}/sections`);
                this.sections = data;
            } catch (error) {
                alertStore.showMessage('Error al cargar el currículo. ' + error.message, 'danger');
                console.error('Error fetching curriculum:', error);
            } finally {
                this.isLoading = false;
            }
        },
        
        // ... (Manejo de Lecciones y Adición/Eliminación de Lecciones - sin cambios) ...
        startAddingLesson(moduleId) {
            this.showAddLessonForm = { [moduleId]: true };
        },
        cancelAddingLesson(moduleId) {
            this.showAddLessonForm = { [moduleId]: false };
        },
        handleLessonAdded(newLesson, moduleId) {
            const moduleIndex = this.sections.findIndex(s => s.id === moduleId);
            if (moduleIndex !== -1) {
                if (!this.sections[moduleIndex].lessons) {
                    this.sections[moduleIndex].lessons = [];
                }
                this.sections[moduleIndex].lessons.push(newLesson);
            }
            this.cancelAddingLesson(moduleId);
        },
        handleLessonDeleted(lessonId, moduleId) {
            const moduleIndex = this.sections.findIndex(s => s.id === moduleId);
            if (moduleIndex !== -1) {
                const module = this.sections[moduleIndex];
                module.lessons = module.lessons.filter(l => l.id !== lessonId);
                module.lessons.forEach((lesson, index) => {
                    lesson.position = index + 1;
                });
            }
        },

        // --- MANEJO DE SECCIONES (CRUD RESTAURADO) ---

        showAddSectionForm() {
            this.showAddForm = true;
        },

        cancelAddSection() {
            this.showAddForm = false;
        },
        
        handleSectionAdded(newSection) {
            this.sections.push(newSection);
            this.showAddForm = false;
            alertStore.showMessage(`Sección "${newSection.name}" creada.`, 'success');
        },

        // 🚨 MÉTODO 1: INICIAR EDICIÓN INLINE
        startEditing(section) {
            this.editingId = section.id;
            this.editingName = section.name;
        },

        // 🚨 MÉTODO 2: CANCELAR EDICIÓN
        cancelEditing() {
            this.editingId = null;
            this.editingName = '';
        },

        // 🚨 MÉTODO 3: ACTUALIZAR SECCIÓN (PUT)
        async updateSection(section) {
            if (!this.editingName.trim()) return;

            this.isUpdating = true;
            try {
                // Endpoint: PUT /api/courses/{slug}/sections/{moduleId}
                await api.put(`/api/courses/${this.slug}/sections/${section.id}`, {
                    name: this.editingName,
                    sortOrder: section.sortOrder 
                });

                // Actualizar la lista local
                section.name = this.editingName;
                this.cancelEditing();
                alertStore.showMessage('Sección actualizada correctamente.', 'success');
            } catch (err) {
                console.error(err);
                alertStore.showMessage('Error al actualizar sección.', 'danger');
            } finally {
                this.isUpdating = false;
            }
        },

        // 🚨 MÉTODO 4: ELIMINAR SECCIÓN (DELETE)
        async deleteSection(section) {
            if(!confirm(`¿Estás seguro de eliminar la sección "${section.name}" y todo su contenido?`)) return;

            this.isDeleting[section.id] = true;
            try {
                // Endpoint: DELETE /api/courses/{slug}/sections/{moduleId}
                await api.del(`/api/courses/${this.slug}/sections/${section.id}`);
                
                // Remover de la lista local
                this.sections = this.sections.filter(s => s.id !== section.id);
                alertStore.showMessage('Sección eliminada.', 'success');
            } catch (err) {
                alertStore.showMessage(err.message || 'Error al eliminar la sección', 'danger');
            } finally {
                delete this.isDeleting[section.id];
            }
        },
    }
};