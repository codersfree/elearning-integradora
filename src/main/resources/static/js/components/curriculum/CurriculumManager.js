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
            sections: [], // Almacena toda la estructura del currículo
            isLoading: false,
            showAddForm: false, // Formulario para añadir sección
            showAddLessonForm: {}, // Formulario para añadir lección por módulo
            
            // Estado para edición de sección
            editingId: null,
            editingName: '',
            isUpdating: false,
            isDeleting: {},
        };
    },
    mounted() {
        this.fetchCurriculum();
    },
    methods: {
        async fetchCurriculum() {
            this.isLoading = true;
            try {
                // Endpoint: /api/courses/{slug}/sections
                const data = await api.get(`/api/courses/${this.slug}/sections`);
                this.sections = data;
            } catch (error) {
                alertStore.showMessage('Error al cargar el currículo.', 'danger');
                console.error('Error fetching curriculum:', error);
            } finally {
                this.isLoading = false;
            }
        },
        
        // --- Manejo de Lecciones ---
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
        
        /**
         * Maneja la eliminación de una lección, removiéndola del estado `sections`.
         * @param {number} lessonId - El ID de la lección eliminada.
         * @param {number} moduleId - El ID del módulo padre.
         */
        handleLessonDeleted(lessonId, moduleId) {
            // 1. Encontrar el índice del módulo padre
            const moduleIndex = this.sections.findIndex(s => s.id === moduleId);
            
            if (moduleIndex !== -1) {
                const module = this.sections[moduleIndex];
                
                // 2. Filtrar la lección de la lista de lecciones del módulo
                module.lessons = module.lessons.filter(l => l.id !== lessonId);
                
                // 3. Recalcular los índices de posición (opcional pero recomendado)
                module.lessons.forEach((lesson, index) => {
                    lesson.position = index + 1;
                });
            }
        },

        // --- Manejo de Secciones (Añadir) ---
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

        // ... Otros métodos de manejo de secciones (update, delete) irían aquí.
    }
};