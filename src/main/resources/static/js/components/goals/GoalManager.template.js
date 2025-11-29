// resources/static/js/components/goals/GoalManager.template.js

export const template = /* html */ `
<div>
    <div v-if="isLoading" class="d-flex justify-content-center my-5">
        <div class="spinner-border" role="status">
            <span class="visually-hidden">Cargando...</span>
        </div>
    </div>

    <div v-else>

        <form @submit.prevent="updateGoals">
            <div class="card shadow-sm border-0 rounded-lg">
                <div class="card-body p-4 p-md-5">

                    <h1 class="h3 fw-bold mb-0">
                        Llega a tus estudiantes
                    </h1>
                    <p class="text-muted mb-0">
                        Las metas que escribas aquí ayudarán a los estudiantes a decidir si
                        tu curso es el adecuado para ellos.
                    </p>
                    <hr class="my-4">
                    
                    <goal-list 
                        v-model="goals"
                        :is-deleting="isDeleting"
                        @delete-goal="handleGoalDelete"
                    ></goal-list>

                    <div class="text-end mt-4">
                        <button type="submit" class="btn btn-dark btn-lg px-4"
                                :disabled="isUpdating">
                            <span v-if="!isUpdating">Actualizar Metas</span>
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
        
        <goal-form 
            :slug="slug" 
            @goal-added="handleGoalAdded"
        ></goal-form>

    </div>
</div>
`;