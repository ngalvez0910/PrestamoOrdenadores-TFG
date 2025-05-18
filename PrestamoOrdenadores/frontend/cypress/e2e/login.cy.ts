describe('Login Page', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  it('muestra el formulario con campos y botón', () => {
    cy.get('h2').should('contain', 'Iniciar Sesión')
    cy.get('input#email').should('exist')
    cy.get('input#password').should('exist')
    cy.get('button[type="submit"]').should('contain', 'Entrar')
  })

  it('valida email inválido y muestra error', () => {
    cy.get('input#email').type('email-invalido')
    cy.get('input#password').type('password123')
    cy.get('button[type="submit"]').click()

    cy.get('#email-error').should('contain', 'Correo electrónico inválido')
    cy.get('.p-toast-message').should('contain', 'Por favor, corrija los errores indicados')
  })

  it('valida password corta y muestra error', () => {
    cy.get('input#email').type('user@example.com')
    cy.get('input#password').type('123')
    cy.get('button[type="submit"]').click()

    cy.get('#password-error').should('contain', 'Contraseña incorrecta')
    cy.get('.p-toast-message').should('contain', 'Por favor, corrija los errores indicados')
  })

  it('puede alternar visibilidad de la contraseña', () => {
    cy.get('input#password').should('have.attr', 'type', 'password')
    cy.get('.password-toggle-icon').click()
    cy.get('input#password').should('have.attr', 'type', 'text')
    cy.get('.password-toggle-icon').click()
    cy.get('input#password').should('have.attr', 'type', 'password')
  })

  it('realiza login exitoso y redirige según rol ADMIN', () => {
    cy.get('#email').type('admin@admin.loantech.com')
    cy.get('#password').type('Password123?')
    cy.get('button[type="submit"]').click()

    cy.url().should('include', '/admin/dashboard')
  })


  it('realiza login exitoso y redirige a perfil para usuario normal', () => {
    cy.get('input#email').type('sofia.rodriguez@loantech.com')
    cy.get('input#password').type('Password123?')
    cy.get('button[type="submit"]').click()

    cy.url().should('include', '/profile')
  })
})