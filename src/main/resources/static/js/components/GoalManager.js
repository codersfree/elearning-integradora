// resources/static/js/components/GoalManager.js

import GoalForm from './GoalForm.js';
import GoalList from './GoalList.js';

export default {
    components: {
        'goal-form': GoalForm,
        'goal-list': GoalList,
    },
    data() {
        return {
            slug: null,
            goals: [],
            
            // --- Estado de carga ---
            isLoading: true,     // Carga inicial de la página
            isUpdating: false,   // Spinner del botón "Actualizar Metas"
            isDeleting: {},      // Objeto para spinners de borrado individuales

            // --- Estado de mensajes ---
            message: null,
            messageType: 'success', // 'success' o 'danger'
        };
    },
    methods: {
        // --- Lógica de Mensajes ---
        showMessage(text, type = 'success', duration = 3000) {
            this.message = text;
            this.messageType = type;
            if (duration) {
                setTimeout(() => this.message = null, duration);
            }
        },
        clearMessage() {
            this.message = null;
        },

        // --- Lógica de API (Métodos Principales) ---
        async fetchGoals() {
            this.isLoading = true;
            try {
                const response = await fetch(`/api/courses/${this.slug}/goals`);
                if (!response.ok) throw new Error('Error al cargar las metas.');
                this.goals = await response.json();
            } catch (err) {
                this.showMessage(err.message, 'danger');
            } finally {
                this.isLoading = false;
            }
        },

        // Se llama desde el botón "Actualizar Metas"
        async updateGoals() {
            this.isUpdating = true;
            try {
                const response = await fetch(`/api/courses/${this.slug}/goals`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(this.goals),
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    let errorMessage = 'No se pudo guardar la actualización.';

                    if (errorData.errors && Array.isArray(errorData.errors)) {
                        errorMessage = errorData.errors.map(err => err.defaultMessage).join(', ');
                    } else if (errorData.message) {
                        errorMessage = errorData.message;
                    }
                    
                    throw new Error(errorMessage);
                }
                
                this.showMessage('Metas actualizadas correctamente.', 'success');
            } catch (err) {
                this.showMessage(err.message, 'danger');
            } finally {
                this.isUpdating = false;
            }
        },

        // Se llama desde el evento @delete-goal de GoalList
        async handleGoalDelete(goal) {
            // Marcamos esta meta como "borrándose"
            this.isDeleting[goal.id] = true;

            try {
                const response = await fetch(`/api/courses/goals/${goal.id}`, {
                    method: 'DELETE',
                });
                if (!response.ok) throw new Error('No se pudo eliminar la meta.');

                // Quitar la meta de la lista local
                this.goals = this.goals.filter(g => g.id !== goal.id);
                this.showMessage('Meta eliminada correctamente.', 'success');
                
            } catch (err) {
                this.showMessage(err.message, 'danger');
            } finally {
                // Quitamos la marca "borrándose"
                delete this.isDeleting[goal.id];
            }
        },

        // Se llama desde el evento @goal-added de GoalForm
        handleGoalAdded(newGoal) {
            this.goals.push(newGoal);
        }
    },
    mounted() {
        // Lee el slug del atributo 'data-slug' del HTML
        this.slug = this.$el.dataset.slug;
        
        console.log('El slug leído desde data-slug es:', this.slug);
        
        // Inicia la carga de datos
        this.fetchGoals();
    },
    template: `
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

                            <h1 class="h3 fw-bold mb-0">Llega a tus estudiantes</h1>
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

                <div v-if="message"
                     class="alert alert-dismissible fade show mt-4"
                     :class="messageType === 'success' ? 'alert-success' : 'alert-danger'"
                     role="alert">
                    <span v-html="message"></span>
                    <button type="button" class="btn-close" @click="clearMessage" aria-label="Close"></button>
                </div>
                
                <goal-form 
                    :slug="slug" 
                    @goal-added="handleGoalAdded"
                    @show-message="showMessage"
                ></goal-form>

            </div>
        </div>
    `
};