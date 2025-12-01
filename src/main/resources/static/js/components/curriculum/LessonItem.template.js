export const template = /* html */ `
<div class="p-2 border-bottom">
    
    <div class="d-flex align-items-center justify-content-between py-2">
        
        <div class="d-flex align-items-center flex-grow-1 cursor-pointer" @click="toggleExpand">
            
            <i class="fas fa-grip-vertical text-secondary me-3" style="cursor: grab;"></i>
            
            <div v-if="isEditingName" class="flex-grow-1 me-3">
                <input type="text" 
                       class="form-control form-control-sm" 
                       v-model="localLesson.name"
                       @blur="finishEditName"
                       @keyup.enter="finishEditName"
                       @click.stop> 
            </div>
            
            <div v-else class="flex-grow-1 d-flex align-items-center">
                <span class="fw-bold me-2">Clase {{ lessonIndex + 1 }}:</span>
                <span class="fw-bold me-1">{{ localLesson.name }}</span>
            </div>
        </div>
        
        <div class="d-flex gap-2 align-items-center">
            
            <button v-if="!isEditingName" class="btn btn-link text-secondary p-0" @click.stop="startEditName" title="Editar nombre">
                <i class="fas fa-pencil-alt"></i>
            </button>
            
            <button class="btn btn-link text-danger p-0" @click.stop="deleteLesson" :disabled="isDeleting" title="Eliminar lección">
                <i v-if="!isDeleting" class="fas fa-trash-alt"></i>
                <span v-else class="spinner-border spinner-border-sm"></span>
            </button>

            <button class="btn btn-link text-secondary p-0" 
                    @click.stop="toggleExpand" 
                    title="Expandir/Contraer"
                    style="line-height: 1;">
                <i class="fas fa-chevron-up" 
                   :class="{ 'fa-rotate-180': !isExpanded }" 
                   style="transition: transform 0.2s;"></i>
            </button>
        </div>
    </div>

    <div v-if="isExpanded" class="mt-2 p-3 bg-light rounded">
        
        <lesson-video-form 
            :lesson="localLesson"
            @video-updated="handleVideoUpdated"
            :editing-mode="isEditingContent"
            @cancel-content-edit="cancelContentEditing"
        ></lesson-video-form>
        
        <div v-if="hasVideo && !isEditingContent" class="mt-4">
            
            <lesson-description-form
                :lesson="localLesson"
                :module-id="moduleId" 
                @description-updated="handleDescriptionUpdated"
                class="mb-3"
            ></lesson-description-form>
            
            <div v-if="showResourcesForm" class="border rounded p-3 mb-3 bg-white">
                <h6>Formulario de Recursos</h6>
                <button @click="showResourcesForm = false" class="btn btn-sm btn-secondary">Cerrar</button>
            </div>
            
        </div>
    </div>
</div>
`;