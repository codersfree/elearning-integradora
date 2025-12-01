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
    props: ['slug'], // ⬅️ Recibe el slug del archivo de inicio
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
                // Usa this.slug para la carga inicial
                const data = await api.get(`/api/courses/${this.slug}/sections`);
                this.sections = data;
            } catch (error) {
                alertStore.showMessage('Error al cargar el currículo. ' + error.message, 'danger');
                console.error('Error fetching curriculum:', error);
            } finally {
                this.isLoading = false;
            }
        },
        
        // --- Manejo de Lecciones y Eliminación (Omitidos, ya implementados) ---
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

        // --- Manejo de Secciones ---
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
    }
};