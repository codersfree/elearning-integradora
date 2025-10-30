// resources/static/js/components/GoalForm.js

export default {
    props: ['slug'],
    emits: ['goal-added', 'show-message'], // Emitirá 'show-message' al padre
    data() {
        return {
            newGoalName: '',
            isSubmitting: false,
        };
    },
    methods: {
        clearForm() {
            this.newGoalName = '';
        },
        async handleSubmit() {
            if (!this.newGoalName.trim()) return;

            this.isSubmitting = true;
            const url = `/api/courses/${this.slug}/goals`;
            
            try {
                const response = await fetch(url, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name: this.newGoalName }),
                });

                if (!response.ok) {
                    // Leemos el cuerpo del error (JSON)
                    const errorData = await response.json(); 
                    let errorMessage = 'No se pudo crear la meta.';

                    // Comprobamos si es un error de validación de Spring Boot
                    if (errorData.errors && Array.isArray(errorData.errors)) {
                        errorMessage = errorData.errors.map(err => err.defaultMessage).join(', ');
                    } else if (errorData.message) {
                        errorMessage = errorData.message;
                    }
                    
                    throw new Error(errorMessage);
                }

                const newGoal = await response.json();
                this.$emit('goal-added', newGoal); // Enviar meta al padre
                this.$emit('show-message', 'Meta creada correctamente.', 'success');
                this.clearForm();

            } catch (err) {
                console.error("Error al crear meta:", err);
                // El 'catch' ahora recibe el mensaje de error detallado
                this.$emit('show-message', err.message, 'danger');
            } finally {
                this.isSubmitting = false;
            }
        }
    },
    template: `
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
    `
};