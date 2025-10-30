import { template } from './GoalForm.template.js';
// ✅ 1. Importa el nuevo cliente API
import api from '../../utils/apiUtils.js';

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
                // ✅ 2. ¡Lógica de envío súper limpia!
                const newGoal = await api.post(url, { name: this.newGoalName });

                // Si no hubo error, continuamos...
                this.$emit('goal-added', newGoal);
                this.$emit('show-message', 'Meta creada correctamente.', 'success');
                this.clearForm();

            } catch (err) {
                // ✅ 3. El 'catch' recibe el error ya procesado
                console.error("Error al crear meta:", err);
                this.$emit('show-message', err.message, 'danger');
            } finally {
                this.isSubmitting = false;
            }
        }
    },
};