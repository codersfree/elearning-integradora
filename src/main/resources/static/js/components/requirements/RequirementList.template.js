// resources/static/js/components/requirements/RequirementList.template.js

export const template = /* html */ `
<div>
    <div v-for="(requirement, index) in modelValue" :key="requirement.id || ('new_' + index)" class="mb-3">
        <div class="input-group">
            <input type="text" class="form-control form-control-lg"
                   :value="requirement.name"
                   @input="updateRequirementName(index, $event.target.value)"
                   placeholder="Nombre del requerimiento" required>

            <button @click.prevent="$emit('delete-requirement', requirement, index)"
                    class="btn btn-outline-danger d-flex align-items-center"
                    type="button"
                    :disabled="isDeleting[requirement.id]">
                
                <i class="fas fa-trash" v-if="!isDeleting[requirement.id]"></i>
                <span v-if="isDeleting[requirement.id]"
                      class="spinner-border spinner-border-sm" role="status"
                      aria-hidden="true"></span>
            </button>
        </div>
    </div>
    
    <div v-if="!modelValue.length">
        <p class="text-muted text-center my-3">Aún no has agregado ningún requerimiento.</p>
    </div>
</div>
`;