import { template } from './LessonForm.template.js';
import api from '../../utils/apiUtils.js';
import { alertStore } from '../../store/alertStore.js';

export default {
    template: template,
    props: ['moduleId'],
    emits: ['lesson-added', 'cancel-lesson'],
    data() {
        return {
            newLessonName: '',
            isSubmitting: false,
        };
    },
    methods: {
        cancel() {
            this.newLessonName = '';
            this.$emit('cancel-lesson');
        },
        async handleSubmit() {
            if (!this.newLessonName.trim()) return;

            this.isSubmitting = true;
            try {
                const newLesson = await api.post(`/api/modules/${this.moduleId}/lessons`, { 
                    name: this.newLessonName,
                });

                this.newLessonName = '';
                this.$emit('lesson-added', newLesson, this.moduleId); 
                alertStore.showMessage(`Clase "${newLesson.name}" agregada.`, 'success');

            } catch (err) {
                console.error("Error al crear lección:", err);
                alertStore.showMessage(err.message || 'Error al crear la clase.', 'danger');
            } finally {
                this.isSubmitting = false;
            }
        }
    },
    mounted() {
        this.$refs.lessonInput.focus();
    }
};