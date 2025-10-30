// resources/static/js/components/goals/GoalForm.js
import { template } from './GoalForm.template.js';

export default {
    template: template,
    props: ['slug'],
    emits: ['goal-added', 'show-message'],
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
            const url = `/api/courses/${this.slug}/goals`;

            try {
                const response = await fetch(url, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name: this.newGoalName }),
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    let errorMessage = 'No se pudo crear la meta.';
                    if (errorData.errors && Array.isArray(errorData.errors)) {
                        errorMessage = errorData.errors.map(err => err.defaultMessage).join(', ');
                    } else if (errorData.message) {
                        errorMessage = errorData.message;
                    }
                    throw new Error(errorMessage);
                }

                const newGoal = await response.json();
                this.$emit('goal-added', newGoal);
                this.$emit('show-message', 'Meta creada correctamente.', 'success');
                this.clearForm();

            } catch (err) {
                console.error("Error al crear meta:", err);
                this.$emit('show-message', err.message, 'danger');
            } finally {
                this.isSubmitting = false;
            }
        }
    },
};