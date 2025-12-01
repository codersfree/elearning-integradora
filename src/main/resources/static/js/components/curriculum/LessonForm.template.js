export const template = /* html */ `
<div class="p-3"> 
    <div class="d-flex align-items-center mb-2">
        <label for="new-lesson-title" class="me-3 text-nowrap fw-bold">Nueva clase:</label>
        
        <input type="text" 
               id="new-lesson-title"
               class="form-control" 
               placeholder="Elige un título"
               maxlength="80"
               v-model="newLessonName"
               @keyup.enter="handleSubmit()"
               ref="lessonInput">
        <span class="ms-2 text-secondary text-nowrap" style="font-size: 0.9em;">{{ 80 - newLessonName.length }}</span>
    </div>

    <div class="d-flex justify-content-end gap-2">
        <button type="button" 
                class="btn btn-link text-dark fw-bold px-4"
                @click="cancel">
            Cancelar
        </button>
        
        <button type="button" 
                class="btn btn-dark fw-bold px-4"
                @click="handleSubmit()"
                :disabled="!newLessonName.trim() || isSubmitting">
            <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-1"></span>
            Añadir clase
        </button>
    </div>
</div>
`;