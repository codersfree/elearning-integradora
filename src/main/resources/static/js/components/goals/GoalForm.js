// resources/static/js/components/goals/GoalForm.js

import { template } from './GoalForm.template.js';
import api from '../../utils/apiUtils.js';

// Importa el store
import { alertStore } from '../../store/alertStore.js';

export default {
    template: template,
    props: ['slug'],
    emits: ['goal-added'], 
    data() {
        return {
            newGoalName: '',
            isSubmitting: false,
        };
    },
    methods: {
        clearForm() {
            this.newGoalName = '';
        },
        async handleSubmit() {
            if (!this.newGoalName.trim()) return;

            this.isSubmitting = true;
            try {
                const newGoal = await api.post(`/api/courses/${this.slug}/goals`, { 
                    name: this.newGoalName 
                });

                this.$emit('goal-added', newGoal);
                // Llama al store global en caso de éxito
                alertStore.showMessage('Meta creada correctamente.', 'success');
                this.clearForm();

            } catch (err) {
                console.error("Error al crear meta:", err);
                // Llama al store global en caso de error
                alertStore.showMessage(err.message, 'danger');
            } finally {
                this.isSubmitting = false;
            }
        }
    },
};