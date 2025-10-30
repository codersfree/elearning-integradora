// resources/static/js/components/common/AlertMessage.template.js

export const template = /* html */ `
<div v-if="state.message"
     class="alert alert-dismissible fade show mt-4"
     :class="state.messageType === 'success' ? 'alert-success' : 'alert-danger'"
     role="alert">
    
    <span v-html="state.message"></span>
    
    <button type="button" class="btn-close" @click="state.clearMessage()" aria-label="Close"></button>
</div>
`;