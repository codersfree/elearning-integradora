export const template = /* html */ `
<div class="border rounded p-3 mb-3 bg-white">
    <textarea class="form-control mb-3" 
              rows="4" 
              placeholder="Escribe aquí la descripción de la lección (opcional)."
              v-model="localDescription"></textarea>
    
    <div class="d-flex justify-content-end gap-2">
        <button type="button" class="btn btn-sm btn-secondary" @click="cancel" :disabled="isSaving">
            Cancelar
        </button>
        <button type="button" class="btn btn-sm btn-primary" @click="saveDescription" :disabled="isSaving">
            <span v-if="isSaving" class="spinner-border spinner-border-sm me-1"></span>
            Guardar Descripción
        </button>
    </div>
</div>
`;