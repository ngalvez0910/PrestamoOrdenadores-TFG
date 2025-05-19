describe('PrestamoDetalle Component', () => {
    beforeEach(() => {
        cy.visit('/');

        cy.get('input#email').type('admin@admin.loantech.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard');

        cy.contains('Ver préstamos').click();

        cy.url().should('include', '/prestamos');

        cy.get('button[title="Ver Detalles"]').first().click({force: true});

        cy.url().should('include', '/prestamo/detalle/938012eccbd');
    });

    it('debe mostrar los detalles del préstamo', () => {
        cy.get('.prestamo-details').should('exist');
        cy.get('#guid').should('have.value', '938012eccbd');
        cy.get('#userNumIdentificacion').should('have.value', '2015LT849');
        cy.get('#dispositivo').should('have.value', '5CD456QWER');
        cy.get('#fechaPrestamo').should('exist');
        cy.get('#fechaDevolucion').should('exist');
    });

    it('debería volver atrás al hacer clic en el botón "Volver"', () => {
        cy.get('.back-button').click();
        cy.url().should('include', '/prestamos');
    });

    it('debe alternar el estado editable al hacer clic en "Editar"', () => {
        cy.get('button.edit-button').click();
        cy.get('#estado').should('exist').should('not.have.attr', 'readonly');
        cy.get('button.edit-button').click();
        cy.get('#estado').should('not.exist');
    });

    it('debe mostrar un mensaje si no hay cambios', () => {
        cy.get('button.edit-button').click();
        cy.get('button.update-button').click();
        cy.contains('No se realizaron cambios en el estado.').should('exist');
    });

    it('debe cambiar el estado del préstamo y actualizar', () => {
        cy.get('button.edit-button').click();
        cy.get('#estado').select('CANCELADO');

        cy.get('button.update-button').click();

        cy.contains('Préstamo').should('exist');
        cy.get('.status-badge').should('contain.text', 'CANCELADO');
    });
});