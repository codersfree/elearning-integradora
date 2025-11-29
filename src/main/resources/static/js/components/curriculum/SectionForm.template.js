export const template = /* html */ `
<div class="card border-0 shadow-sm bg-light rounded">
    <div class="card-body p-4">
        <label class="form-label fw-bold text-dark mb-2">
            Nueva sección
        </label>
        
        <input type="text" 
               class="form-control mb-3 bg-white"
               placeholder="Elija un título"
               v-model="newSectionName"
               @keyup.enter="handleSubmit()"
               autofocus>

        <div class="d-flex justify-content-end gap-2">
            <button type="button" 
                    class="btn btn-danger text-white fw-bold px-4"
                    @click="cancel">
                CANCELAR
            </button>
            
            <button type="button" 
                    class="btn btn-dark fw-bold px-4"
                    @click="handleSubmit()"
                    :disabled="!newSectionName.trim() || isSubmitting">
                <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-1"></span>
                AGREGAR
            </button>
        </div>
    </div>
</div>
`;