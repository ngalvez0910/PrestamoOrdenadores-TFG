<template>
  <MenuBarNoSession />

  <div class="register-page-container"> <div class="register-box">
    <h2>Registro</h2> <Dialog :header="modalTitle" v-model:visible="isInfoModalVisible" modal :style="{ width: '50vw', fontFamily: 'Montserrat' }" :breakpoints="{'960px': '75vw', '641px': '100vw'}">
    <div class="dialog-content" v-html="modalBody"></div>
  </Dialog>

    <form @submit.prevent="registerUser" novalidate>
      <div class="form-group">
        <label for="numeroIdentificacion" class="input-label">
          Número de Identificación
          <i
              class="pi pi-info-circle info-icon"
              @click="showInfo('numeroIdentificacion')"
              title="Haz clic para más información"
          ></i>
        </label>
        <input type="text" id="numeroIdentificacion" class="input-field" name="numeroIdentificacion" placeholder="Número de Identificación" v-model="form.numeroIdentificacion">
        <p class="error-message" v-if="errors.numeroIdentificacion">{{ errors.numeroIdentificacion }}</p>
      </div>

      <div class="form-row">
        <div class="form-col">
          <div class="form-group">
            <label for="nombre" class="input-label">Nombre</label>
            <input type="text" id="nombre" class="input-field" name="nombre" placeholder="Tu nombre" v-model="form.nombre">
            <p class="error-message" v-if="errors.nombre">{{ errors.nombre }}</p>
          </div>
        </div>
        <div class="form-col">
          <div class="form-group">
            <label for="apellidos" class="input-label">Apellidos</label>
            <input type="text" id="apellidos" class="input-field" name="apellidos" placeholder="Tus apellidos" v-model="form.apellidos">
            <p class="error-message" v-if="errors.apellidos">{{ errors.apellidos }}</p>
          </div>
        </div>
      </div>

      <div class="form-row">
        <div class="form-col">
          <div class="form-group">
            <label for="curso" class="input-label">Curso</label>
            <input class="input-field" id="curso" type="text" name="curso" placeholder="Ej: 1ASIR, 2DAW"  v-model="form.curso">
            <p class="error-message" v-if="errors.curso">{{ errors.curso }}</p>
          </div>
        </div>
        <div class="form-col">
          <div class="form-group">
            <label for="tutor" class="input-label">Tutor</label>
            <input class="input-field" id="tutor" type="text" name="tutor" placeholder="Nombre del tutor" v-model="form.tutor">
            <p class="error-message" v-if="errors.tutor">{{ errors.tutor }}</p>
          </div>
        </div>
      </div>

      <div class="form-group">
        <label for="email" class="input-label">
          Correo electrónico
          <i
              class="pi pi-info-circle info-icon"
              @click="showInfo('email')"
              title="Haz clic para más información"
          ></i>
        </label>
        <input class="input-field" id="email" type="email" name="email" placeholder="Correo institucional" v-model="form.email" @blur="validateEmail">
        <p class="error-message" v-if="errors.email">{{ errors.email }}</p>
      </div>

      <div class="form-row">
        <div class="form-col">
          <div class="form-group">
            <label for="password" class="input-label">
              Contraseña
              <i
                  class="pi pi-info-circle info-icon"
                  @click="showInfo('password')"
                  title="Haz clic para más información"
              ></i>
            </label>
            <div class="password-input-wrapper">
              <input
                  :type="passwordFieldType" id="password"
                  name="password"
                  class="input-field"
                  placeholder="Crea una contraseña segura"
                  v-model="form.password"
                  :aria-invalid="errors.password ? 'true' : 'false'"
                  aria-describedby="password-error"
              >
              <i
                  :class="['pi', passwordFieldType === 'password' ? 'pi-eye' : 'pi-eye-slash', 'password-toggle-icon']"
                  @click="togglePasswordVisibility"
                  title="Mostrar/Ocultar contraseña"
              ></i>
            </div>
            <p class="error-message" v-if="errors.password">{{ errors.password }}</p>
          </div>
        </div>
        <div class="form-col">
          <div class="form-group">
            <label for="confirmPassword" class="input-label">Confirmar contraseña</label>
            <div class="password-input-wrapper">
              <input
                  :type="confirmPasswordFieldType" id="confirmPassword"
                  name="confirmPassword"
                  class="input-field"
                  placeholder="Vuelve a escribir la contraseña"
                  v-model="form.confirmPassword"
                  :aria-invalid="errors.confirmPassword ? 'true' : 'false'"
                  aria-describedby="confirmPassword-error"
              >
              <i
                  :class="['pi', confirmPasswordFieldType === 'password' ? 'pi-eye' : 'pi-eye-slash', 'password-toggle-icon']"
                  @click="toggleConfirmPasswordVisibility"
                  title="Mostrar/Ocultar contraseña"
              ></i>
            </div>
            <p class="error-message" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</p>
          </div>
        </div>
      </div>

      <button type="submit" class="submit-button" :disabled="isSubmitting">
        {{ isSubmitting ? 'Registrando...' : 'Registrarse' }}
      </button>
    </form>

    <p class="login-link">
      ¿Ya tienes cuenta? <a href="/">Inicia sesión aquí</a>
    </p>

    <Toast />
  </div>
  </div>
</template>

<script lang="ts">
import MenuBarNoSession from "@/components/MenuBarNoSession.vue";
import Toast from 'primevue/toast';
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';
import { useToast } from 'primevue/usetoast';
import { authService } from '@/services/AuthService.ts';

export default {
  name: 'Register',
  components: { MenuBarNoSession, Toast, Button, Dialog },
  setup() {
    const toast = useToast();
    return { toast };
  },
  data() {
    return {
      form: {
        numeroIdentificacion: '',
        nombre: '',
        apellidos: '',
        curso: '',
        tutor: '',
        email: '',
        password: '',
        confirmPassword: '',
      },
      errors: {
        numeroIdentificacion: '',
        nombre: '',
        apellidos: '',
        email: '',
        curso: '',
        tutor: '',
        password: '',
        confirmPassword: '',
      },
      isSubmitting: false,
      isInfoModalVisible: false,
      modalTitle: '',
      modalBody: '',
      passwordFieldType: 'password',
      confirmPasswordFieldType: 'password',
    };
  },
  methods: {
    showInfo(infoType: string) {
      switch (infoType) {
        case 'numeroIdentificacion':
          this.modalTitle = '¿Cuál es mi Número de Identificación?';
          this.modalBody = `
            <p>Generalmente se encuentra en tu carnet de estudiante.</p>
            <p>Fíjate en la foto:</p>
            <img
              src="/carnet.png"
              alt="Ejemplo de carnet de estudiante mostrando número de identificación"
              style="max-width: 100%; height: auto; display: block; margin-top: 10px;"
            />
          `;
          break;
        case 'email':
          this.modalTitle = '¿Qué Correo Electrónico debo utilizar?';
          this.modalBody = 'Tu correo institucional asignado. Debe terminar en @loantech.com.';
          break;
        case 'password':
          this.modalTitle = 'Contraseña Segura';
          this.modalBody = 'La contraseña debe cumplir los siguientes requisitos:<br>- Mínimo 8 caracteres.<br>- Al menos una letra mayúscula.<br>- Al menos una letra minúscula.<br>- Al menos un dígito.<br>- Al menos un carácter especial (ej: !@#$%^&*).';
          break;
        default:
          this.modalTitle = 'Información';
          this.modalBody = 'No hay información disponible para este campo.';
      }
      this.isInfoModalVisible = true;
    },
    validateEmail() {
      const email = this.form.email;
      let isValidFormat = false;
      let errorMessage = '';

      if (!email) {
        errorMessage = 'El correo electrónico es obligatorio';
      } else {
        const endsWithLoantech = email.endsWith('@loantech.com');
        const endsWithProfesor = email.endsWith('@profesor.loantech.com');
        const endsWithAdmin = email.endsWith('@admin.loantech.com');

        const basicFormatCheck = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

        if (basicFormatCheck && (endsWithLoantech || endsWithProfesor || endsWithAdmin)) {
          isValidFormat = true;
        } else {
          errorMessage = 'El correo debe ser válido y terminar en @loantech.com';
        }
      }

      if (isValidFormat) {
        this.errors.email = '';
        this.validateEmailDependencies();
      } else {
        this.errors.email = errorMessage;
        this.errors.curso = '';
        this.errors.tutor = '';
      }
    },
    validateEmailDependencies() {
      this.errors.curso = '';
      this.errors.tutor = '';

      if (this.form.email.endsWith('@loantech.com')) {
        if (!this.form.curso) {
          this.errors.curso = 'El curso es obligatorio para estudiantes.';
        }
        if (!this.form.tutor) {
          this.errors.tutor = 'El tutor es obligatorio para estudiantes.';
        }
      } else if (this.form.email.endsWith('@profesor.loantech.com')) {
        if (!this.form.curso) {
          this.errors.curso = 'El curso/departamento es obligatorio para profesores.';
        }
        if (this.form.tutor) {
          this.errors.tutor = 'El campo tutor no es aplicable para profesores y debe estar vacío.';
        }
      } else if (this.form.email.endsWith('@admin.loantech.com')) {
        if (this.form.curso) {
          this.errors.curso = 'El campo curso no es aplicable para administradores y debe estar vacío.';
        }
        if (this.form.tutor) {
          this.errors.tutor = 'El campo tutor no es aplicable para administradores y debe estar vacío.';
        }
      }
    },
    validateForm(): boolean {
      this.errors = {
        numeroIdentificacion: '',
        nombre: '',
        apellidos: '',
        curso: '',
        tutor: '',
        email: '',
        password: '',
        confirmPassword: '',
      };

      let formIsValid = true;

      if (!this.form.numeroIdentificacion) {
        this.errors.numeroIdentificacion = 'Campo obligatorio';
        formIsValid = false;
      } else {
        const nieRegex = /^(\d{4})LT(\d{3})$/;
        if (!nieRegex.test(this.form.numeroIdentificacion)) {
          this.errors.numeroIdentificacion = 'Formato incorrecto. Debe ser XXXXLTXXX (ej: 2023LT123).';
          formIsValid = false;
        } else {
          const match = this.form.numeroIdentificacion.match(nieRegex);
          if (match) {
            const year = parseInt(match[1], 10);
            const currentYear = new Date().getFullYear();
            const minValidYear = currentYear - 30;

            if (year < minValidYear || year > currentYear) {
              this.errors.numeroIdentificacion = `Número de Identificación inválido`;
              formIsValid = false;
            }
          }
        }
      }

      if (!this.form.nombre) {
        this.errors.nombre = 'Campo obligatorio';
        formIsValid = false;
      }

      if (!this.form.apellidos) {
        this.errors.apellidos = 'Campo obligatorio';
        formIsValid = false;
      }

      if (!this.form.email) {
        this.errors.email = 'El correo electrónico es obligatorio';
        formIsValid = false;
      } else {
        const basicFormatCheck = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email);
        const endsWithLoantech = this.form.email.endsWith('@loantech.com');
        const endsWithProfesor = this.form.email.endsWith('@profesor.loantech.com');
        const endsWithAdmin = this.form.email.endsWith('@admin.loantech.com');

        if (!(basicFormatCheck && (endsWithLoantech || endsWithProfesor || endsWithAdmin))) {
          this.errors.email = 'El correo debe ser válido y terminar en @loantech.com.';
          formIsValid = false;
        }
      }

      if (!this.errors.email) {
        this.validateEmailDependencies();
        if (this.errors.curso) formIsValid = false;
        if (this.errors.tutor) formIsValid = false;
      }

      if (!this.form.password) {
        this.errors.password = 'La contraseña es obligatoria.';
        formIsValid = false;
      } else if (this.form.password.length < 8 || !/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(this.form.password)) {
        this.errors.password = 'La contraseña debe cumplir los siguientes requisitos:\n - Mínimo 8 caracteres.\n- Al menos una letra mayúscula.\n- Al menos una letra minúscula.\n- Al menos un dígito.\n- Al menos un carácter especial (ej: !@#$%^&*).';
        formIsValid = false;
      }

      if (!this.form.confirmPassword) {
        this.errors.confirmPassword = 'Por favor, confirma la contraseña.';
        formIsValid = false;
      } else if (this.form.password && this.form.password !== this.form.confirmPassword) {
        this.errors.confirmPassword = 'Las contraseñas no coinciden';
        formIsValid = false;
      }

      return formIsValid;
    },
    async registerUser() {
      if (!this.validateForm()) {
        this.toast.add({
          severity: 'error',
          summary: 'Error en el Registro',
          detail: 'Por favor, corrija los errores y vuelva a intentarlo.',
          life: 3000,
          styleClass: 'custom-toast-error'
        });
        return;
      }

      this.isSubmitting = true;

      const userData = {
        numeroIdentificacion: this.form.numeroIdentificacion,
        nombre: this.form.nombre,
        apellidos: this.form.apellidos,
        curso: this.form.curso,
        email: this.form.email,
        tutor: this.form.tutor,
        password: this.form.password,
        confirmPassword: this.form.confirmPassword,
      };

      try {
        const token = await authService.register(userData);
        if (token) {
          this.toast.add({
            severity: 'success',
            summary: 'Registro Exitoso',
            detail: 'El registro se completó y la sesión se inició automáticamente.',
            life: 3000,
            styleClass: 'custom-toast-success'
          });
          this.$router.push('/profile');
        } else {
          this.toast.add({
            severity: 'error',
            summary: 'Error en el Registro',
            detail: 'Hubo un problema al iniciar sesión automáticamente después del registro.',
            life: 5000,
            styleClass: 'custom-toast-error'
          });
        }
      } catch (error: any) {
        console.error('Error al registrar el usuario:', error.response?.data?.message || error.message);
        this.toast.add({
          severity: 'error',
          summary: 'Error en el Registro',
          detail: error.response?.data?.message || 'Hubo un error al registrar el usuario. Inténtelo de nuevo.',
          life: 5000,
          styleClass: 'custom-toast-error'
        });
      } finally {
        this.isSubmitting = false;
      }
    },
    togglePasswordVisibility() {
      this.passwordFieldType = this.passwordFieldType === 'password' ? 'text' : 'password';
    },
    toggleConfirmPasswordVisibility() {
      this.confirmPasswordFieldType = this.confirmPasswordFieldType === 'password' ? 'text' : 'password';
    }
  },
};
</script>

<style scoped>
.register-page-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 60px);
  min-width: 700px;
  padding: 80px 20px 40px 20px;
  box-sizing: border-box;
  margin-left: 37%;
  margin-top: 5%;
}

.register-box {
  background-color: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(var(--color-primary-rgb), 0.15);
  width: 100%;
  max-width: 750px;
  text-align: center;
}

.register-box h2 {
  color: var(--color-primary);
  margin-top: 0;
  margin-bottom: 35px;
  font-size: 1.8rem;
  font-weight: 700;
}

form {
  display: flex;
  flex-direction: column;
  gap: 15px;
  text-align: left;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.form-col {
  flex: 1;
  min-width: 200px;
}

.form-group {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.input-label {
  font-size: 0.9rem;
  color: var(--color-text-dark);
  font-weight: 500;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.info-icon {
  color: var(--color-interactive);
  cursor: pointer;
  margin-left: 8px;
  font-size: 1rem;
  vertical-align: middle;
  transition: color 0.2s ease;
}

.info-icon:hover {
  color: var(--color-interactive-darker);
}

.input-field {
  border-radius: 8px;
  padding: 12px 15px;
  border: 1px solid var(--color-neutral-medium);
  background-color: white;
  color: var(--color-text-dark);
  width: 100%;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  outline: none;
  box-sizing: border-box;
  font-size: 1rem;
  padding-right: 45px !important;
}

.input-field:focus {
  border-color: var(--color-interactive);
  box-shadow: 0 0 0 3px rgba(var(--color-interactive-rgb), 0.2);
}

.input-field[aria-invalid="true"] {
  border-color: var(--color-error);
}
.input-field[aria-invalid="true"]:focus {
  box-shadow: 0 0 0 3px rgba(var(--color-error-rgb), 0.2);
}

.password-input-wrapper {
  position: relative;
  width: 100%;
}

.password-toggle-icon {
  position: absolute;
  top: 50%;
  right: 15px;
  transform: translateY(-50%);
  cursor: pointer;
  color: var(--color-neutral-medium);
  transition: color 0.2s ease;
}

.password-toggle-icon:hover {
  color: var(--color-interactive);
}

.error-message {
  color: var(--color-error);
  font-size: 0.85rem;
  margin-top: 6px;
  min-height: 1.2em;
  white-space: pre-wrap;
}

.submit-button {
  background-color: var(--color-interactive);
  color: var(--color-text-on-dark-hover);
  border: none;
  border-radius: 8px;
  padding: 12px 20px;
  font-size: 1.1rem;
  font-weight: bold;
  width: 100%;
  transition: background-color 0.3s ease, transform 0.1s ease, opacity 0.3s ease;
  cursor: pointer;
  margin-top: 20px;
}

.submit-button:hover:not(:disabled) {
  background-color: var(--color-interactive-darker);
}

.submit-button:active:not(:disabled) {
  transform: scale(0.98);
}

.submit-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-link {
  margin-top: 25px;
  font-size: 0.9rem;
  color: var(--color-text-dark);
}

.login-link a {
  color: var(--color-interactive);
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>