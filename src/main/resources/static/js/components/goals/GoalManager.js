// resources/static/js/components/goals/GoalManager.js

import { template } from './GoalManager.template.js';
import api from '../../utils/apiUtils.js';

// Importa el store
import { alertStore } from '../../store/alertStore.js';

import AlertMessage from '../common/AlertMessage.js';
import GoalForm from './GoalForm.js';
import GoalList from './GoalList.js';

export default {
    template: template,
    props: ['slug'], 
    
    components: {
        'alert-message': AlertMessage,
        'goal-form': GoalForm,
        'goal-list': GoalList,
    },
    
    data() {
        return {
            goals: [],
            isLoading: true,
            isUpdating: false,
            isDeleting: {},
        };
    },
    
    methods: {

        async fetchGoals() {
            this.isLoading = true;
            try {
                this.goals = await api.get(`/api/courses/${this.slug}/goals`);
            } catch (err) {
                alertStore.showMessage(err.message, 'danger');
            } finally {
                this.isLoading = false;
            }
        },

        async updateGoals() {
            this.isUpdating = true;
            try {
                await api.put(`/api/courses/${this.slug}/goals`, this.goals);
                // Llama al store global en caso de éxito
                alertStore.showMessage('Metas actualizadas correctamente.', 'success');
            } catch (err) {
                // Llama al store global en caso de error
                alertStore.showMessage(err.message, 'danger');
            } finally {
                this.isUpdating = false;
            }
        },

        async handleGoalDelete(goal) {
            this.isDeleting[goal.id] = true;
            try {
                await api.del(`/api/courses/goals/${goal.id}`);
                this.goals = this.goals.filter(g => g.id !== goal.id);
                // Llama al store global en caso de éxito
                alertStore.showMessage('Meta eliminada correctamente.', 'success');
            } catch (err) {
                // Llama al store global en caso de error
                alertStore.showMessage(err.message, 'danger');
            } finally {
                delete this.isDeleting[goal.id];
            }
        },

        handleGoalAdded(newGoal) {
            this.goals.push(newGoal);
        }
    },
    mounted() {
        this.fetchGoals();
    },
};