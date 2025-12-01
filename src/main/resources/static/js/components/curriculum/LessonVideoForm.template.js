export const template = /* html */ `
<div class="p-3 border rounded bg-white">
    
    <div v-if="!hasVideo || editingMode">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h6 class="fw-bold mb-0">{{ editingMode ? 'Reemplazar Video' : 'Cargar Video' }}</h6>
            <h6 class="fw-bold mb-0 text-secondary">Añadir de la biblioteca</h6>
        </div>
        
        <div class="row align-items-center">
            
            <div class="col-8">
                <input type="file" 
                       ref="videoFile" 
                       class="form-control"
                       @change="handleFileSelect" 
                       accept="video/*"
                       :disabled="isUploading">
            </div>
            
            <div class="col-4 text-end d-flex justify-content-end gap-2">
                
                <button v-if="editingMode" class="btn btn-secondary w-100" @click="cancelEdit" :disabled="isUploading">
                    Cancelar
                </button>
                
                <button class="btn btn-primary w-100" @click="uploadFile" :disabled="!selectedFile || isUploading">
                    <span v-if="isUploading" class="spinner-border spinner-border-sm me-1"></span>
                    <span v-else>Subir Video</span>
                </button>

            </div>
        </div>

        <small v-if="selectedFile" class="text-success d-block mt-2">
            Archivo listo para subir: {{ selectedFileName }}
        </small>
        
        <small class="text-muted mt-2 d-block">
            Nota: Todos los archivos deben tener al menos 720p y pesar menos de 4 GB.
        </small>
    </div>

    <div v-else>
        <div class="d-flex justify-content-between align-items-center mb-3">
            
            <div class="d-flex align-items-start flex-grow-1">
                
                <div style="width: 100px; height: 50px; border: 1px solid #ccc; overflow: hidden;" class="me-3 rounded flex-shrink-0">
                    <img v-if="lesson.imagePath" 
                         :src="'/uploads/' + lesson.imagePath" 
                         alt="Miniatura de video" 
                         style="width: 100%; height: 100%; object-fit: cover;">
                    <div v-else style="width: 100px; height: 50px; background-color: #eee;"></div>
                </div>

                <div>
                    <p class="mb-0 fw-bold">{{ videoFileName }}</p>
                    <small class="text-secondary">{{ formattedDuration }} min</small>
                </div>
            </div>
            
            <div class="d-flex flex-column align-items-end gap-1">
                
                <button class="btn btn-link text-danger btn-sm p-0" @click.prevent="deleteVideo" :disabled="isUploading">
                    <i v-if="!isUploading" class="fas fa-trash-alt me-1"></i>
                    <span v-else class="spinner-border spinner-border-sm"></span>
                    Eliminar
                </button>
            </div>
        </div>
        
        </div>
</div>
`;