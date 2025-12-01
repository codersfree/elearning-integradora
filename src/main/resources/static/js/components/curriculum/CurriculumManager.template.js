export const template = /* html */ `
<div class="container-fluid p-0">
    
    <alert-message></alert-message>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold">Administrador del Currículo</h2>
    </div>

    <div v-if="isLoading" class="text-center p-5">
        <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Cargando...</span>
        </div>
    </div>

    <div v-else>
        
        <div v-for="(section, index) in sections" :key="section.id" class="mb-4 border rounded shadow-sm"> 
            
            <div class="p-3 bg-light d-flex justify-content-between align-items-center border-bottom">
                <div class="d-flex align-items-center">
                    <i class="fas fa-bars text-secondary me-3" style="cursor: grab;"></i>
                    <h5 class="mb-0 fw-bold">{{ section.name }}</h5>
                </div>
                <div>
                    <button class="btn btn-sm btn-outline-primary me-2" @click="startAddingLesson(section.id)">
                        <i class="fas fa-plus me-1"></i> Añadir Clase
                    </button>
                </div>
            </div>
            
            <div class="p-3"> 
                
                <div v-if="section.lessons && section.lessons.length > 0">
                    <lesson-item
                        v-for="(lesson, lessonIndex) in section.lessons"
                        :key="lesson.id"
                        :lesson="lesson"
                        :lesson-index="lessonIndex"
                        :module-id="section.id" 
                        @lesson-deleted="handleLessonDeleted"  
                    ></lesson-item>
                </div>
                <div v-else class="text-center text-secondary py-3">
                    Aún no hay clases en esta sección.
                </div>
                
                <lesson-form
                    v-if="showAddLessonForm[section.id]"
                    :module-id="section.id"
                    :lessons-count="section.lessons ? section.lessons.length : 0"
                    @lesson-added="handleLessonAdded($event, section.id)"
                    @cancel="cancelAddingLesson(section.id)"
                ></lesson-form>
            </div>

        </div> 
        
        <div class="text-center my-4">
            <button v-if="!showAddForm" class="btn btn-outline-secondary" @click="showAddSectionForm">
                <i class="fas fa-folder-plus me-1"></i> Añadir Módulo/Sección
            </button>
        </div>

        <section-form
            v-if="showAddForm"
            :slug="slug"  :next-order="sections.length + 1"
            @section-added="handleSectionAdded"
            @cancel="cancelAddSection"
        ></section-form>
    </div>
</div>
`;