import { template } from './AlertMessage.template.js';
import { alertStore } from '../../store/alertStore.js';

export default {
    template: template,
    
    data() {
        return {
            state: alertStore
        };
    }
};