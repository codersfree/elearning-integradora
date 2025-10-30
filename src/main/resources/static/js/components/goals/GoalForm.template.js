// resources/static/js/components/goals/GoalForm.template.js

export const template = /* html */ `
<div class="card shadow-sm border-0 rounded-lg mt-4 bg-light">
    <div class="card-body p-4 p-md-5">
        <label for="newGoalInput" class="form-label fw-bold">
            Nueva meta
        </label>
        <div class="input-group mb-3">
            <input id="newGoalInput" type="text" class="form-control form-control-lg"
                placeholder="Ingrese el nombre de la meta"
                v-model="newGoalName"
                @keydown.enter.prevent="handleSubmit()">
        </div>
        <div class="text-end">
            <button type="button" class="btn btn-danger btn-lg px-4 me-2"
                    @click="clearForm">Cancelar</button>
            <button type="button" class="btn btn-dark btn-lg px-4"
                    @click.prevent="handleSubmit()"
                    :disabled="newGoalName.trim() === '' || isSubmitting">
                <span v-if="!isSubmitting">Agregar</span>
                <span v-if="isSubmitting">
                    <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                    Agregando...
                </span>
            </button>
        </div>
    </div>
</div>
`;
