<template>
  <div class="footer-wrapper">
    <div class="footer">
      <div class="footer-content">
        <div class="footer-brand">
          <a href="/" class="brand-link">
            <img src="/logo.png" alt="LoanTech Logo" class="footer-logo-img" />
            <h1>LoanTech</h1>
          </a>
        </div>

        <div class="footer-info">
          <div class="contact-column">
            <div class="contact-item">
              <span class="icon">📧</span> <a href="mailto:contacto@loantech.com">contacto@loantech.com</a>
            </div>
            <div class="contact-item">
              <span class="icon">📞</span> <a href="tel:+34000000000">+34 000 000 000</a>
            </div>
          </div>
          <div class="contact-column">
            <div class="contact-item address-item">
              <span class="icon">📍</span> <span>C. Manzanares, 28692, Madrid</span>
            </div>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; {{ currentYear }} LoanTech. Todos los derechos reservados.</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'

export default defineComponent({
  name: "Footer",
  setup() {
    const currentYear = ref(new Date().getFullYear());
    return {
      currentYear
    }
  }
})
</script>

<style scoped>
.footer-wrapper {
  display: block;
  width: 100%;
  background-color: #1f2937;
  color: #f3f4f6;
}

.footer {
  max-width: 1200px;
  margin: 0 auto;
}

.footer-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.footer-brand {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.brand-link {
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: #f3f4f6;
}

.footer-logo-img {
  width: 50px;
  height: auto;
}

.footer-info {
  display: flex;
  flex-wrap: wrap;
  gap: 2rem;
  justify-content: flex-start;
}

.contact-column {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.icon {
  font-size: 1.2rem;
}

.address-item span:last-child {
  line-height: 1.4;
}

.footer-bottom {
  border-top: 1px solid #374151;
  margin-top: 2rem;
  padding-top: 1rem;
  text-align: center;
  font-size: 0.9rem;
  color: #9ca3af;
}
</style>