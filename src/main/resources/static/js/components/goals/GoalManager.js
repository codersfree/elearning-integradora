// resources/static/js/components/goals/GoalManager.js

import { template } from './GoalManager.template.js';
// ✅ Importa el nuevo cliente API
import api from '../../utils/apiUtils.js';

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
            isLoading: true,
            isUpdating: false,
            isDeleting: {},
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

        // --- Lógica de API Refactorizada ---
        async fetchGoals() {
            this.isLoading = true;
            try {
                // ✅ Lógica de GET limpia
                this.goals = await api.get(`/api/courses/${this.slug}/goals`);
            } catch (err) {
                this.showMessage(err.message, 'danger');
            } finally {
                this.isLoading = false;
            }
        },

        async updateGoals() {
            this.isUpdating = true;
            try {
                // ✅ Lógica de PUT limpia
                await api.put(`/api/courses/${this.slug}/goals`, this.goals);
                this.showMessage('Metas actualizadas correctamente.', 'success');
            } catch (err) {
                this.showMessage(err.message, 'danger');
            } finally {
                this.isUpdating = false;
            }
        },

        async handleGoalDelete(goal) {
            this.isDeleting[goal.id] = true;
            try {
                // ✅ Lógica de DELETE limpia
                await api.del(`/api/courses/goals/${goal.id}`);
                
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
        this.fetchGoals();
    },
};