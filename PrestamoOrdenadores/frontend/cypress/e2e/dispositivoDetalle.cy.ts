describe('Detalle de Dispositivo', () => {
    beforeEach(() => {
        cy.visit('/');

        cy.get('input#email').type('admin@admin.loantech.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard');

        cy.contains('Ver dispositivos').click();

        cy.url().should('include', '/dispositivos');

        cy.get('button[title="Editar/Ver Detalles"]').first().click({force: true});

        cy.url().should('include', '/dispositivo/detalle/ed472271676');
    });

    it('Permite volver con el botón de retroceso', () => {
        cy.get('.back-button').click();
        cy.url().should('include', '/dispositivos');
    });

    it('Muestra los detalles correctamente', () => {
        cy.contains('Detalles del Dispositivo').should('be.visible');
        cy.get('.input-field').eq(0).should('have.value', '1AB123WXYZ');
        cy.get('.input-field').eq(1).should('have.value', 'ratón, cargador');
        cy.get('.status-badge').should('contain', 'DISPONIBLE');
        cy.get('.input-field:visible').last().should('have.value', 'NO');
    });

    it('Permite activar y desactivar el modo edición', () => {
        cy.get('.edit-button').click();
        cy.get('select#estado').should('exist');
        cy.get('.edit-button').click();
        cy.get('select#estado').should('not.exist');
    });

    it('Muestra mensaje de "sin cambios"', () => {
        cy.get('.edit-button').click();
        cy.get('.update-button').click();
        cy.contains('No se realizaron cambios.').should('be.visible');
    });

    it('Cancela los cambios al cerrar edición', () => {
        cy.get('.edit-button').click();
        cy.get('#estado').select('PRESTADO');
        cy.get('.edit-button').click();
        cy.get('.status-badge').should('contain', 'DISPONIBLE');
    });

    it('Edita campos y actualiza el dispositivo', () => {
        cy.get('.edit-button').click();
        cy.get('#estado').select('PRESTADO');
        cy.get('.update-button').click();

        cy.contains('Dispositivo actualizado.').should('be.visible');
        cy.get('.edit-button').contains('Editar');
        cy.contains('PRESTADO').should('be.visible');
    });
});