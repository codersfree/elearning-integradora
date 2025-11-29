export const template = /* html */ `
<div class="container-fluid p-0">
    
    <!-- Header -->
    <div class="mb-4">
        <h2 class="h4 fw-bold text-dark">Lecciones del curso</h2>
    </div>

    <!-- Spinner General -->
    <div v-if="isLoading" class="d-flex justify-content-center my-5">
        <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Cargando...</span>
        </div>
    </div>

    <div v-else>
        
        <!-- LISTA DE SECCIONES -->
        <div v-for="(section, index) in sections" :key="section.id" class="mb-3">
            
            <!-- MODO VISUALIZACIÓN -->
            <div v-if="editingId !== section.id" 
                 class="d-flex align-items-center justify-content-between p-3 bg-light rounded border border-0 shadow-sm">
                
                <div class="d-flex align-items-center flex-grow-1">
                    <!-- Botón de Orden (Drag Handle) -->
                    <div class="text-secondary me-3" style="cursor: grab;" title="Arrastrar para ordenar">
                        <i class="fas fa-grip-vertical fa-lg"></i>
                    </div>

                    <!-- Texto de la Sección -->
                    <div>
                        <span class="fw-bold text-secondary me-1">Sección {{ index + 1 }}:</span>
                        <span class="fw-bold text-dark">{{ section.name }}</span>
                    </div>
                </div>

                <!-- Botones de Acción (Editar / Eliminar) -->
                <div class="d-flex gap-2">
                    <button class="btn btn-link text-secondary p-0" @click="startEditing(section)" title="Editar">
                        <i class="fas fa-pencil-alt"></i>
                    </button>
                    
                    <button class="btn btn-link text-secondary p-0" @click="handleSectionDelete(section)" 
                            :disabled="isDeleting[section.id]" title="Eliminar">
                        <i v-if="!isDeleting[section.id]" class="fas fa-trash-alt"></i>
                        <span v-else class="spinner-border spinner-border-sm" role="status"></span>
                    </button>
                </div>
            </div>

            <!-- MODO EDICIÓN (Replica tu captura image_ce2586.png) -->
            <div v-else class="p-4 bg-light rounded border border-0 shadow-sm">
                <div class="d-flex align-items-center mb-3">
                    <span class="fw-bold text-secondary me-3 text-nowrap">Sección {{ index + 1 }}:</span>
                    <input type="text" 
                           class="form-control bg-white" 
                           v-model="editingName" 
                           @keyup.enter="updateSection(section)"
                           ref="editInput"
                           autoFocus>
                </div>
                
                <div class="d-flex justify-content-end gap-2">
                    <button class="btn btn-danger text-white fw-bold btn-sm px-3" 
                            @click="cancelEditing">
                        Cancelar
                    </button>
                    <button class="btn btn-dark fw-bold btn-sm px-3" 
                            @click="updateSection(section)"
                            :disabled="!editingName.trim() || isUpdating">
                        <span v-if="isUpdating" class="spinner-border spinner-border-sm me-1"></span>
                        Actualizar
                    </button>
                </div>
            </div>

        </div>

        <!-- Botón para mostrar formulario de Agregar (El "+" flotante) -->
        <div v-if="!showAddForm" class="mt-3">
             <button class="btn btn-light text-primary fw-bold shadow-sm px-3" 
                     @click="showAddForm = true">
                <i class="fas fa-plus"></i>
             </button>
        </div>

        <!-- FORMULARIO NUEVA SECCIÓN (Al final) -->
        <div v-if="showAddForm" class="mt-4">
            <section-form 
                :slug="slug" 
                :next-order="sections.length + 1"
                @section-added="handleSectionAdded"
                @cancel="showAddForm = false"
            ></section-form>
        </div>

        <alert-message class="mt-3"></alert-message>
    </div>
</div>
`;