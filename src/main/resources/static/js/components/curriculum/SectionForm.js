import { template } from './SectionForm.template.js';
import api from '../../utils/apiUtils.js';
import { alertStore } from '../../store/alertStore.js'; 

export default {
    template: template,
    props: ['slug', 'nextOrder'], // ⬅️ Debe recibir la prop como 'slug'
    emits: ['section-added', 'cancel'], 
    
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
                // Utiliza this.slug para construir el endpoint
                const newSection = await api.post(`/api/courses/${this.slug}/sections`, { 
                    name: this.newSectionName,
                    sortOrder: this.nextOrder 
                });

                this.newSectionName = '';
                this.$emit('section-added', newSection);
                alertStore.showMessage(`Sección "${newSection.name}" agregada.`, 'success'); 

            } catch (err) {
                console.error("Error al crear sección:", err);
                alertStore.showMessage(err.message || 'Error al crear la sección. Verifique la validación.', 'danger');
                
            } finally {
                this.isSubmitting = false;
            }
        }
    },
};