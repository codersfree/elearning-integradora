export const template = /* html */ `
<div class="section-list">
    
    <!-- Encabezado de la lista -->
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="mb-0">Secciones del curso</h4>
    </div>

    <!-- Lista de Secciones -->
    <div class="list-group shadow-sm mb-4">
        
        <!-- Item de Sección (Iteración) -->
        <div v-for="(section, index) in sections" :key="section.id" class="list-group-item p-3 border-start-0 border-end-0">
            <div class="d-flex align-items-center justify-content-between">
                
                <!-- Modo Visualización -->
                <div v-if="editingId !== section.id" class="d-flex align-items-center flex-grow-1">
                    <i class="fas fa-bars text-muted me-3" style="cursor: grab;"></i> <!-- Icono Drag (simulado) -->
                    <div>
                        <span class="fw-bold text-muted small me-2">Sección {{ index + 1 }}:</span>
                        <span class="fw-bold text-dark">{{ section.name }}</span>
                    </div>
                </div>

                <!-- Modo Edición -->
                <div v-else class="d-flex flex-grow-1 gap-2">
                    <input 
                        type="text" 
                        class="form-control" 
                        v-model="editingName" 
                        @keyup.enter="updateSection(section)"
                        ref="editInput"
                    >
                    <button class="btn btn-success btn-sm" @click="updateSection(section)">
                        <i class="fas fa-save"></i>
                    </button>
                    <button class="btn btn-secondary btn-sm" @click="cancelEditing">
                        <i class="fas fa-times"></i>
                    </button>
                </div>

                <!-- Botones de Acción (Solo si no se edita) -->
                <div v-if="editingId !== section.id" class="ms-3">
                    <button class="btn btn-link text-primary p-1" @click="startEditing(section)">
                        <i class="fas fa-pencil-alt"></i>
                    </button>
                    <button class="btn btn-link text-danger p-1" @click="deleteSection(section.id)">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
            
            <!-- Aquí iría el componente hijo <LessonList :module-id="section.id" /> en el futuro -->
        </div>
    </div>

    <!-- Formulario Nueva Sección -->
    <div v-if="!isAdding" class="d-grid">
        <button class="btn btn-outline-primary fw-bold py-2" @click="isAdding = true">
            <i class="fas fa-plus me-1"></i> Nueva Sección
        </button>
    </div>

    <div v-else class="card bg-light border-primary shadow-sm">
        <div class="card-body">
            <h6 class="card-title">Nueva Sección</h6>
            <div class="input-group mb-3">
                <input 
                    type="text" 
                    class="form-control" 
                    placeholder="Escribe el nombre de la sección..." 
                    v-model="newSectionName"
                    @keyup.enter="createSection"
                >
                <button class="btn btn-primary" type="button" @click="createSection">Agregar</button>
                <button class="btn btn-outline-secondary" type="button" @click="cancelAdding">Cancelar</button>
            </div>
        </div>
    </div>

</div>
`;