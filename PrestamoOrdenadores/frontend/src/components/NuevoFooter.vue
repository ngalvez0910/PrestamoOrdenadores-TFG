<template>
  <footer class="app-footer">
    <div class="footer-content">
      <div class="copyright">
        &copy; {{ currentYear }} LoanTech. Todos los derechos reservados.
      </div>
      <nav class="footer-nav">
        <ul>
          <li><a href="/politicaPrivacidad">Política de Privacidad</a></li>
          <li><a href="/terminosServicio">Términos de Servicio</a></li>
          <li>
            <span class="icon">📧</span> <a href="mailto:contacto@loantech.com">contacto@loantech.com</a>
          </li>
          <li>
            <span class="icon">📞</span> <a href="tel:+34910384889">+34 910 384 889</a>
          </li>
        </ul>
      </nav>
    </div>
  </footer>
</template>

<script setup>
import { ref } from 'vue';
const currentYear = ref(new Date().getFullYear());
</script>

<style scoped>
.app-footer {
  background-color: #f8f9fa;
  color: #6c757d;
  padding: 2rem 1rem;
  border-top: 1px solid #e7e7e7;
  margin-top: auto;
  width: 100%;
  box-sizing: border-box;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.copyright {
  font-size: 0.9em;
  text-align: center;
  flex-basis: 100%;
  order: 2;
}

.footer-nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
  justify-content: center;
}

.footer-nav li {
  display: inline-block;
}

.footer-nav a {
  color: #007bff;
  text-decoration: none;
  font-size: 0.9em;
}

.footer-nav a:hover,
.footer-nav a:focus {
  text-decoration: underline;
  color: #0056b3;
}

@media (min-width: 768px) {
  .footer-content {
    flex-wrap: nowrap;
    justify-content: space-between;
  }

  .copyright {
    text-align: left;
    flex-basis: auto;
    order: 0;
  }

  .footer-nav ul {
    justify-content: flex-end;
  }
}
</style>