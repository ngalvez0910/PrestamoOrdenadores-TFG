describe('Página Mis Incidencias', () => {
    beforeEach(() => {
        cy.visit('/');
    });

    it('muestra título y botón para reportar', () => {
        cy.get('input#email').type('sofia.rodriguez.loantech@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/profile')

        cy.contains('a', 'Mis Incidencias').click();

        cy.contains('h2', 'Mis Incidencias').should('be.visible');
        cy.contains('button', 'Reportar Incidencia').should('be.visible');
    });

    it('muestra mensaje cuando no hay incidencias', () => {
        cy.get('input#email').type('pedro.ramos.loantech.profesor@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/profile')

        cy.contains('a', 'Mis Incidencias').click();

        cy.contains('No has reportado ninguna incidencia.').should('be.visible');
    });

    it('abre el modal al hacer clic en "Reportar Incidencia"', () => {
        cy.get('input#email').type('sofia.rodriguez.loantech@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/profile')

        cy.contains('a', 'Mis Incidencias').click();

        cy.contains('Reportar Incidencia').click();
        cy.contains('Reportar Nueva Incidencia').should('be.visible');
    });

    it('valida campos obligatorios al enviar formulario vacío', () => {
        cy.get('input#email').type('sofia.rodriguez.loantech@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/profile')

        cy.contains('a', 'Mis Incidencias').click();

        cy.contains('Reportar Incidencia').click();
        cy.get('.action-button.primary-button').contains('Reportar Incidencia').click();
        cy.contains('El asunto es obligatorio.').should('be.visible');
        cy.contains('La descripción es obligatoria.').should('be.visible');
    });

    it('valida límites de caracteres', () => {
        cy.get('input#email').type('sofia.rodriguez.loantech@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/profile')

        cy.contains('a', 'Mis Incidencias').click();

        cy.contains('Reportar Incidencia').click();
        cy.get('#incidenciaAsunto').type('a'.repeat(256), { force: true });
        cy.get('#incidenciaDescripcion').type('a'.repeat(1001));
        cy.get('.action-button.primary-button').contains('Reportar Incidencia').click();
        cy.contains('El asunto no puede exceder los 255 caracteres.').should('be.visible');
        cy.contains('La descripción no puede exceder los 1000 caracteres.').should('be.visible');
    });

    it('envía incidencia válida y actualiza tabla', () => {
        cy.get('input#email').type('sofia.rodriguez.loantech@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/profile')

        cy.contains('a', 'Mis Incidencias').click();

        cy.contains('Reportar Incidencia').click();
        cy.get('#incidenciaAsunto').type('Proyector no funciona', { force: true });
        cy.get('#incidenciaDescripcion').type('Intenté encenderlo varias veces y no da señal.');
        cy.get('.action-button.primary-button').contains('Reportar Incidencia').click();

        cy.contains('Proyector no funciona').should('exist');
        cy.contains('Intenté encenderlo').should('not.exist');
    });
});
