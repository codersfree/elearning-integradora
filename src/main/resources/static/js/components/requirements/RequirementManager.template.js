// resources/static/js/components/requirements/RequirementManager.template.js

export const template = /* vue-html */ `
<div>
    <div v-if="isLoading" class="d-flex justify-content-center my-5">
        <div class="spinner-border" role="status">
            <span class="visually-hidden">Cargando...</span>
        </div>
    </div>

    <div v-else>

        <form @submit.prevent="updateRequirements">
            <div class="card shadow-sm border-0 rounded-lg">
                <div class="card-body p-4 p-md-5">

                    <h1 class="h3 fw-bold mb-0">Requerimientos del curso</h1>
                    <p class="text-muted mb-0">
                        Define los requerimientos o conocimientos previos que los estudiantes 
                        deben tener para tomar tu curso.
                    </p>
                    <hr class="my-4">
                    
                    <requirement-list 
                        v-model="requirements"
                        :is-deleting="isDeleting"
                        @delete-requirement="handleRequirementDelete"
                    ></requirement-list>

                    <div class="text-end mt-4">
                        <button type="submit" class="btn btn-dark btn-lg px-4"
                                :disabled="isUpdating">
                            <span v-if="!isUpdating">Actualizar Requerimientos</span>
                            <span v-if="isUpdating">
                                <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                                Actualizando...
                            </span>
                        </button>
                    </div>

                </div>
            </div>
        </form>

        <alert-message></alert-message>
        
        <requirement-form 
            :slug="slug" 
            @requirement-added="handleRequirementAdded"
        ></requirement-form>

    </div>
</div>
`;