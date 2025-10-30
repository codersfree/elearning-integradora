// resources/static/js/components/requirements/RequirementList.js

import { template } from './RequirementList.template.js';

export default {
    template: template,
    props: {
        modelValue: { type: Array, required: true },
        isDeleting: { type: Object, required: true }
    },
    emits: ['update:modelValue', 'delete-requirement'],
    
    methods: {
        updateRequirementName(index, newName) {
            const updatedRequirements = [...this.modelValue];
            updatedRequirements[index].name = newName;
            this.$emit('update:modelValue', updatedRequirements);
        }
    },
};