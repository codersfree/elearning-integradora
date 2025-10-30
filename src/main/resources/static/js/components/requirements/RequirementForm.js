// resources/static/js/components/requirements/RequirementForm.js

import { template } from './RequirementForm.template.js';
import api from '../../utils/apiUtils.js';
import { alertStore } from '../../store/alertStore.js';

export default {
    template: template,
    props: ['slug'],
    emits: ['requirement-added'],
    data() {
        return {
            newRequirementName: '',
            isSubmitting: false,
        };
    },
    methods: {
        clearForm() {
            this.newRequirementName = '';
        },
        async handleSubmit() {
            if (!this.newRequirementName.trim()) return;

            this.isSubmitting = true;
            try {
                const newRequirement = await api.post(
                    `/api/courses/${this.slug}/requirements`,
                    { name: this.newRequirementName }
                );

                this.$emit('requirement-added', newRequirement);
                alertStore.showMessage('Requerimiento creado correctamente.', 'success');
                this.clearForm();

            } catch (err) {
                console.error("Error al crear requerimiento:", err);
                alertStore.showMessage(err.message, 'danger');
            } finally {
                this.isSubmitting = false;
            }
        }
    },
};