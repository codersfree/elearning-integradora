import { template } from './LessonVideoForm.template.js';
import api from '../../utils/apiUtils.js';
import { alertStore } from '../../store/alertStore.js';

export default {
    template: template,
    props: ['lesson'],
    emits: ['video-updated'],
    data() {
        return {
            isUploading: false,
            selectedFile: null,
        };
    },
    methods: {
        handleFileSelect(event) {
            const file = event.target.files[0];
            this.selectedFile = file;
        },
        async uploadFile() {
            const file = this.selectedFile;
            if (!file) {
                 alertStore.showMessage('Por favor, selecciona un archivo de video antes de subir.', 'danger');
                 return;
            }

            if (file.size > 4 * 1024 * 1024 * 1024) { 
                alertStore.showMessage('El archivo no debe superar los 4 GB.', 'danger');
                return;
            }

            this.isUploading = true;
            try {
                const formData = new FormData();
                formData.append('file', file);
                
                const response = await fetch(`/api/lessons/${this.lesson.id}/video`, {
                    method: 'POST',
                    body: formData
                });
                
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Error ${response.status}: ${errorText.substring(0, 50)}...`);
                }
                
                const updatedLesson = await response.json();
                
                this.$emit('video-updated', updatedLesson);
                this.selectedFile = null;
                alertStore.showMessage('Video subido y guardado con éxito.', 'success');

            } catch (error) {
                alertStore.showMessage(error.message || 'Fallo en la subida del video.', 'danger');
            } finally {
                this.isUploading = false;
                if (this.$refs.videoFile) {
                    this.$refs.videoFile.value = '';
                }
            }
        },
        async deleteVideo() {
            if (!confirm('¿Estás seguro de eliminar el video de esta clase?')) return;
            
            this.isUploading = true;
            try {
                // Lógica de eliminación del video mediante API DELETE
                await api.del(`/api/lessons/${this.lesson.id}/video`); 
                
                // Actualización local
                const updatedLesson = { ...this.lesson, videoPath: null, duration: 0, isPreview: false };
                
                this.$emit('video-updated', updatedLesson);
                alertStore.showMessage('Video eliminado con éxito.', 'success');
            } catch (e) {
                alertStore.showMessage('Error al eliminar el video.', 'danger');
            } finally {
                this.isUploading = false;
            }
        }
    },
    computed: {
        hasVideo() {
            return this.lesson.videoPath && this.lesson.videoPath.length > 0;
        },
        selectedFileName() {
            return this.selectedFile ? this.selectedFile.name : 'No has seleccionado ningún archivo';
        },
        videoFileName() {
            if (this.lesson.videoPath) {
                return this.lesson.videoPath.split('/').pop();
            }
            return 'Archivo de video';
        },
        formattedDuration() {
            const minutes = Math.floor(this.lesson.duration / 60);
            const seconds = (this.lesson.duration % 60).toString().padStart(2, '0');
            return `${minutes}:${seconds}`;
        }
    }
};