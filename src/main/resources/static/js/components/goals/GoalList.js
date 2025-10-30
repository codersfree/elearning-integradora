// resources/static/js/components/goals/GoalList.js
import { template } from './GoalList.template.js';

export default {
    template: template,
    props: {
        modelValue: {
            type: Array,
            required: true
        },
        isDeleting: {
            type: Object,
            required: true
        }
    },
    
    emits: ['update:modelValue', 'delete-goal'],
    
    methods: {
        updateGoalName(index, newName) {
            const updatedGoals = [...this.modelValue];
            updatedGoals[index].name = newName;
            this.$emit('update:modelValue', updatedGoals);
        }
    },
};