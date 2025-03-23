<template>
  <MenuBar />
  <div class="botones-container">
    <div class="boton-atras">
      <a href="/admin/dashboard/usuarios">
        <button class="back-button">
          <i class="pi pi-arrow-left"></i>
        </button>
      </a>
    </div>
  </div>
  <div class="usuario-details" v-if="userData">
    <div class="iconoUser">
      <i class="pi pi-user"></i>
    </div>
    <div class="numIdentificacion">
      <h2><strong>Número de Identificación:</strong> {{userData.numeroIdentificacion}}</h2>
    </div>
    <div class="row">
      <div class="nombre col-4">
        <h6>Nombre: </h6>
        <input readonly type="text" id="nombre" v-model="userData.nombre"/>
      </div>
      <div class="apellidos col-4">
        <h6>Apellidos: </h6>
        <input readonly type="text" id="apellidos" v-model="userData.apellidos"/>
      </div>
      <div class="email col-4">
        <h6>Email: </h6>
        <input readonly type="text" id="email" v-model="userData.email"/>
      </div>
    </div>
    <div class="row">
      <div class="curso col-4">
        <h6>Curso: </h6>
        <input readonly type="text" id="curso" v-model="userData.curso"/>
      </div>
      <div class="tutor col-4">
        <h6>Tutor: </h6>
        <input readonly type="text" id="tutor" v-model="userData.tutor"/>
      </div>
      <div class="rol col-4">
        <h6>Rol: </h6>
        <input readonly type="text" id="rol" v-model="userData.rol"/>
      </div>
    </div>
    <div class="row">
      <div class="login col-6">
        <h6>Último Login: </h6>
        <input readonly type="text" id="lastLoginDate" v-model="userData.lastLoginDate"/>
      </div>
      <div class="passReset col-6">
        <h6>Última Modificación de Contraseña: </h6>
        <input readonly type="text" id="lastPasswordResetDate" v-model="userData.lastPasswordResetDate"/>
      </div>
    </div>
    <div class="row">
      <div class="creacion col-6">
        <h6>Fecha de Creación: </h6>
        <input readonly type="text" id="createdDate" v-model="userData.createdDate"/>
      </div>
      <div class="actualizacion col-6">
        <h6>Fecha de última Actualización: </h6>
        <input readonly type="text" id="updatedDate" v-model="userData.updatedDate"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import MenuBar from "@/components/AdminMenuBar.vue";
import {getUserByGuidAdmin} from "@/services/UsuarioService";

export default defineComponent({
  name: "UsuarioDetalle",
  components: {MenuBar},
  data() {
    return {
      userData: null as any,
    };
  },
  async mounted() {
    try {
      const guid = this.$route.params.guid;
      const guidString = Array.isArray(guid) ? guid[0] : guid;
      const user = await getUserByGuidAdmin(guidString);
      this.userData = user;
      console.log(user)
    } catch (error) {
      console.error("Error al obtener los detalles del usuario:", error);
    }
  }
})
</script>

<style scoped>
body{
  overflow-y: auto;
}

.boton-atras {
  margin-left: -70%;
  margin-top: 40%;
}

.back-button {
  padding: 0.7rem 1.2rem;
  font-size: 0.875rem;
  background-color: #14124f;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
  margin-top: 20%;
  width: 5%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-button:hover{
  background-color: #0d0c34;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(72, 70, 159);
}

.back-button i {
  pointer-events: none;
}

a{
  background-color: inherit !important;
}

.usuario-details {
  background-color: white;
  border-radius: 10px;
  padding: 20px;
  width: 300%;
  max-width: max-content;
  box-shadow: 0 8px 16px rgba(20, 18, 79, 0.3);
  margin-left: -20%;
  margin-top: -15%;
  margin-bottom: 7%;
}

.pi-user {
  font-size: 4.5rem;
  margin-left: 80%;
  margin-bottom: 5%;
  margin-top: 2%
}

h6{
  font-weight: bold;
}

.numIdentificacion {
  margin-top: 0;
  font-size: 1.5rem;
}

.nombre, .apellidos, .email, .curso, .tutor, .rol, .fotoCarnet, .login, .creacion, .actualizacion, .passReset {
  margin-top: 15px;
}

input, select{
  border-radius: 20px;
  padding: 8px;
  border: 1px solid #d6621e;
  width: 100%;
  max-width: max-content;
  transition: border 0.3s ease;
  outline: none;
}

.botones-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin-bottom: 20px;
}
</style>