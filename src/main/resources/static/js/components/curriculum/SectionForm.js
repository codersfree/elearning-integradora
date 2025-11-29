import { template } from './SectionForm.template.js';
import api from '../../utils/apiUtils.js';

export default {
    template: template,
    props: ['slug', 'nextOrder'],
    emits: ['section-added', 'cancel'], // Añadido evento cancel
    data() {
        return {
            newSectionName: '',
            isSubmitting: false,
        };
    },
    methods: {
        cancel() {
            this.newSectionName = '';
            this.$emit('cancel');
        },
        async handleSubmit() {
            if (!this.newSectionName.trim()) return;

            this.isSubmitting = true;
            try {
                const newSection = await api.post(`/api/courses/${this.slug}/sections`, { 
                    name: this.newSectionName,
                    sortOrder: this.nextOrder 
                });

                // Limpiamos y emitimos éxito
                this.newSectionName = '';
                this.$emit('section-added', newSection);

            } catch (err) {
                console.error("Error al crear sección:", err);
                // Aquí podrías emitir un error o usar alertStore si lo importas
                alert("Error al crear la sección"); 
            } finally {
                this.isSubmitting = false;
            }
        }
    },
};