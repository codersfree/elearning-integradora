import { template } from './GoalManager.template.js';

import GoalForm from './GoalForm.js';
import GoalList from './GoalList.js';

export default {
    template: template,
    
    props: ['slug'], 

    components: {
        'goal-form': GoalForm,
        'goal-list': GoalList,
    },
    data() {
        return {
            goals: [],

            //Estados de carga
            isLoading: true,
            isUpdating: false,
            isDeleting: {},

            // Mensajes
            message: null,
            messageType: 'success',
        };
    },
    methods: {
        showMessage(text, type = 'success', duration = 3000) {
            this.message = text;
            this.messageType = type;
            if (duration) {
                setTimeout(() => this.message = null, duration);
            }
        },
        clearMessage() {
            this.message = null;
        },

        // --- Lógica de API ---
        async fetchGoals() {
            // Estado de carga
            this.isLoading = true;

            // Lógica para obtener las metas
            try {
                const response = await fetch(`/api/courses/${this.slug}/goals`);
                if (!response.ok) throw new Error('Error al cargar las metas.');
                this.goals = await response.json();
            } catch (err) {
                this.showMessage(err.message, 'danger');
            } finally {
                this.isLoading = false;
            }
        },

        async updateGoals() {
            // Estado de carga
            this.isUpdating = true;

            // Lógica para actualizar las metas
            try {
                const response = await fetch(`/api/courses/${this.slug}/goals`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(this.goals),
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    let errorMessage = 'No se pudo guardar la actualización.';
                    if (errorData.errors && Array.isArray(errorData.errors)) {
                        errorMessage = errorData.errors.map(err => err.defaultMessage).join(', ');
                    } else if (errorData.message) {
                        errorMessage = errorData.message;
                    }
                    throw new Error(errorMessage);
                }
                
                this.showMessage('Metas actualizadas correctamente.', 'success');

            } catch (err) {
                this.showMessage(err.message, 'danger');
            } finally {
                this.isUpdating = false;
            }
        },

        async handleGoalDelete(goal) {
            // Estado de carga
            this.isDeleting[goal.id] = true;

            // Lógica para eliminar la meta
            try {
                const response = await fetch(`/api/courses/goals/${goal.id}`, {
                    method: 'DELETE',
                });
                if (!response.ok) throw new Error('No se pudo eliminar la meta.');
                this.goals = this.goals.filter(g => g.id !== goal.id);
                this.showMessage('Meta eliminada correctamente.', 'success');
            } catch (err) {
                this.showMessage(err.message, 'danger');
            } finally {
                delete this.isDeleting[goal.id];
            }
        },

        handleGoalAdded(newGoal) {
            this.goals.push(newGoal);
        }
    },
    mounted() {        
        // Inicia la carga de datos
        this.fetchGoals();
    },
};