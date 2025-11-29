import { template } from './CurriculumManager.template.js';
import api from '../../utils/apiUtils.js';
import { alertStore } from '../../store/alertStore.js';
import SectionForm from './SectionForm.js';
import AlertMessage from '../common/AlertMessage.js';

export default {
    template: template,
    props: ['slug'],
    
    components: {
        'alert-message': AlertMessage,
        'section-form': SectionForm
    },
    
    data() {
        return {
            sections: [],
            isLoading: true,
            isDeleting: {},     // Para spinners de eliminar
            isUpdating: false,  // Para spinner de actualizar
            
            // Estado para Edición Individual
            editingId: null,
            editingName: '',

            // Estado para Agregar
            showAddForm: false
        };
    },
    
    methods: {
        async fetchSections() {
            this.isLoading = true;
            try {
                this.sections = await api.get(`/api/courses/${this.slug}/sections`);
            } catch (err) {
                alertStore.showMessage(err.message || 'Error al cargar secciones', 'danger');
            } finally {
                this.isLoading = false;
            }
        },

        // --- Lógica de Edición Individual ---

        startEditing(section) {
            this.editingId = section.id;
            this.editingName = section.name;
        },

        cancelEditing() {
            this.editingId = null;
            this.editingName = '';
        },

        async updateSection(section) {
            if (!this.editingName.trim()) return;

            this.isUpdating = true;
            try {
                // Actualizamos solo esta sección
                await api.put(`/api/courses/${this.slug}/sections/${section.id}`, {
                    name: this.editingName,
                    sortOrder: section.sortOrder
                });

                // Actualizamos la lista local
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

        // --- Lógica de Eliminación ---

        async handleSectionDelete(section) {
            if(!confirm('¿Estás seguro de eliminar la sección "' + section.name + '" y todas sus lecciones?')) return;

            this.isDeleting[section.id] = true;
            try {
                await api.del(`/api/courses/${this.slug}/sections/${section.id}`);
                this.sections = this.sections.filter(s => s.id !== section.id);
                alertStore.showMessage('Sección eliminada.', 'success');
            } catch (err) {
                alertStore.showMessage(err.message || 'Error al eliminar', 'danger');
            } finally {
                delete this.isDeleting[section.id];
            }
        },

        // --- Lógica de Agregar ---

        handleSectionAdded(newSection) {
            this.sections.push(newSection);
            this.showAddForm = false; // Ocultamos el formulario tras agregar
        }
    },
    
    mounted() {
        this.fetchSections();
    },
};