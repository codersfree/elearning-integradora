import { template } from './SectionList.template.js';

export default {
    template: template,
    props: ['slug'], // Recibimos el slug del curso desde el padre
    data() {
        return {
            sections: [],
            isAdding: false,
            newSectionName: '',
            editingId: null,
            editingName: ''
        }
    },
    mounted() {
        this.fetchSections();
    },
    methods: {
        async fetchSections() {
            try {
                // Ajusta la URL según tu contexto
                const response = await fetch(`/api/courses/${this.slug}/sections`);
                if (response.ok) {
                    this.sections = await response.json();
                }
            } catch (error) {
                console.error("Error cargando secciones:", error);
            }
        },
        async createSection() {
            if (!this.newSectionName.trim()) return;

            try {
                const response = await fetch(`/api/courses/${this.slug}/sections`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name: this.newSectionName, sortOrder: this.sections.length + 1 })
                });

                if (response.ok) {
                    const newSection = await response.json();
                    this.sections.push(newSection);
                    this.newSectionName = '';
                    this.isAdding = false;
                }
            } catch (error) {
                console.error("Error creando sección:", error);
            }
        },
        startEditing(section) {
            this.editingId = section.id;
            this.editingName = section.name;
        },
        cancelEditing() {
            this.editingId = null;
            this.editingName = '';
        },
        cancelAdding() {
            this.isAdding = false;
            this.newSectionName = '';
        },
        async updateSection(section) {
            if (!this.editingName.trim()) return;

            try {
                const response = await fetch(`/api/courses/${this.slug}/sections/${section.id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name: this.editingName })
                });

                if (response.ok) {
                    // Actualizamos localmente
                    section.name = this.editingName;
                    this.cancelEditing();
                }
            } catch (error) {
                console.error("Error actualizando sección:", error);
            }
        },
        async deleteSection(id) {
            if (!confirm("¿Estás seguro de eliminar esta sección y todas sus clases?")) return;

            try {
                const response = await fetch(`/api/courses/${this.slug}/sections/${id}`, {
                    method: 'DELETE'
                });

                if (response.ok) {
                    this.sections = this.sections.filter(s => s.id !== id);
                }
            } catch (error) {
                console.error("Error eliminando sección:", error);
            }
        }
    }
}