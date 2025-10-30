// resources/static/js/components/requirements/RequirementManager.js

import { template } from './RequirementManager.template.js';
import api from '../../utils/apiUtils.js';
import { alertStore } from '../../store/alertStore.js';

import AlertMessage from '../common/AlertMessage.js';
import RequirementForm from './RequirementForm.js';
import RequirementList from './RequirementList.js';

export default {
    template: template,
    props: ['slug'], 
    components: {
        'alert-message': AlertMessage,
        'requirement-form': RequirementForm,
        'requirement-list': RequirementList,
    },
    data() {
        return {
            requirements: [],
            isLoading: true,
            isUpdating: false,
            isDeleting: {},
        };
    },
    methods: {
        async fetchRequirements() {
            this.isLoading = true;
            try {
                // Llama al endpoint de requerimientos
                this.requirements = await api.get(`/api/courses/${this.slug}/requirements`);
            } catch (err) {
                alertStore.showMessage(err.message, 'danger');
            } finally {
                this.isLoading = false;
            }
        },

        async updateRequirements() {
            this.isUpdating = true;
            try {
                // Llama al endpoint de requerimientos
                await api.put(`/api/courses/${this.slug}/requirements`, this.requirements);
                alertStore.showMessage('Requerimientos actualizados correctamente.', 'success');
            } catch (err) {
                alertStore.showMessage(err.message, 'danger');
            } finally {
                this.isUpdating = false;
            }
        },

        async handleRequirementDelete(requirement) {
            this.isDeleting[requirement.id] = true;
            try {
                // Llama al endpoint de requerimientos
                await api.del(`/api/courses/requirements/${requirement.id}`);
                this.requirements = this.requirements.filter(r => r.id !== requirement.id);
                alertStore.showMessage('Requerimiento eliminado correctamente.', 'success');
            } catch (err) {
                alertStore.showMessage(err.message, 'danger');
            } finally {
                delete this.isDeleting[requirement.id];
            }
        },

        handleRequirementAdded(newRequirement) {
            this.requirements.push(newRequirement);
        }
    },
    mounted() {
        this.fetchRequirements();
    },
};