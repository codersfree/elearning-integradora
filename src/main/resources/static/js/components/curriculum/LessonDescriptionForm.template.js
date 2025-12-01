export const template = /* html */ `
<div>
    <div v-if="isEditing" class="border rounded p-3 mb-3 bg-white">
        <h6 class="fw-bold mb-2">Descripción de clase:</h6>
        <textarea class="form-control mb-3" 
            rows="4" 
            placeholder="Escribe aquí la descripción de la lección (opcional)."
            v-model="localDescription">
        </textarea>
        
        <div class="d-flex justify-content-end gap-2">
            <button type="button" class="btn btn-sm btn-secondary" @click="cancelEditing" :disabled="isSaving">
                Cancelar
            </button>
            <button type="button" class="btn btn-sm btn-primary" @click="saveDescription" :disabled="isSaving || !localDescription.trim()">
                <span v-if="isSaving" class="spinner-border spinner-border-sm me-1"></span>
                Guardar
            </button>
        </div>
    </div>
    
    <div v-else>
        
        <div v-if="hasDescription" class="border rounded p-3 mb-3 bg-white">
            <h6 class="fw-bold mb-1">Descripción:</h6>
            <div class="text-secondary" style="white-space: pre-wrap;" v-text="lesson.description"></div>
            
            <button class="btn btn-link text-primary fw-bold p-0 mt-2" @click.prevent="startEditing">
                Editar Descripción
            </button>
        </div>

        <div v-else class="mb-3">
            <button class="btn btn-link text-primary fw-bold p-0" @click.prevent="startEditing">
                + Descripción
            </button>
        </div>
    </div>
</div>
`;