export const template = /*html*/ `
<div>
    <div v-for="(goal, index) in modelValue" :key="goal.id || ('new_' + index)" class="mb-3">
        <div class="input-group">
            <input type="text" class="form-control form-control-lg"
                    :value="goal.name"
                    @input="updateGoalName(index, $event.target.value)"
                    placeholder="Nombre de la meta" required>

            <button @click.prevent="$emit('delete-goal', goal, index)"
                    class="btn btn-outline-danger d-flex align-items-center"
                    type="button"
                    :disabled="isDeleting[goal.id]">
                
                <i class="fas fa-trash" v-if="!isDeleting[goal.id]"></i>
                
                <span v-if="isDeleting[goal.id]"
                        class="spinner-border spinner-border-sm" role="status"
                        aria-hidden="true"></span>
            </button>
        </div>
    </div>
    
    <div v-if="!modelValue.length">
        <p class="text-muted text-center my-3">Aún no has agregado ninguna meta.</p>
    </div>
</div>
`;