describe('PrestamoMe Page', () => {
    beforeEach(() => {
        cy.visit('/');
    });

    it('Muestra título y botón para realizar préstamo', () => {
        cy.get('input#email').type('sofia.rodriguez@loantech.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/profile')

        cy.contains('a', 'Mis Préstamos').click();

        cy.contains('h2', 'Mis Préstamos').should('exist');
        cy.get('button.primary-button').contains('Realizar Préstamo').should('exist');
    });

    it('Carga y muestra la lista de préstamos', () => {
        cy.get('input#email').type('sofia.rodriguez@loantech.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/profile')

        cy.contains('a', 'Mis Préstamos').click();

        cy.get('tbody tr').should('have.length', 1);
        cy.get('tbody tr td').first().should('contain.text', '8XD345FGHI');
        cy.get('tbody tr td').eq(1).should('contain.text', '10-05-2025');
        cy.get('tbody tr td').eq(2).should('contain.text', '24-05-2025');
        cy.get('tbody tr td').eq(3).should('contain.text', 'EN_CURSO');
    });

    it('Muestra mensaje cuando no hay préstamos', () => {
        cy.get('input#email').type('pedro.ramos@profesor.loantech.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/profile')

        cy.contains('a', 'Mis Préstamos').click();

        cy.contains('Aún no tienes préstamos registrados.').should('exist');
    });

    it('Confirma y realiza un préstamo al pulsar el botón', () => {
        cy.get('input#email').type('sofia.rodriguez@loantech.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/profile')

        cy.contains('a', 'Mis Préstamos').click();

        cy.get('button.primary-button').click();

        cy.get('.p-confirm-dialog').should('be.visible');
        cy.get('.p-confirm-dialog .p-confirm-dialog-accept').click();
    });

    it('Cancela la realización de un préstamo si se rechaza el diálogo', () => {
        cy.get('input#email').type('sofia.rodriguez@loantech.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/profile')

        cy.contains('a', 'Mis Préstamos').click();

        cy.get('button.primary-button').click();

        cy.get('.p-confirm-dialog').should('be.visible');
        cy.get('.p-confirm-dialog .p-confirm-dialog-reject').click();

        cy.get('.p-toast-message').should('contain.text', 'El préstamo no se ha realizado.');
    });

    it('Descarga el PDF al pulsar el botón correspondiente en la tabla', () => {
        cy.get('input#email').type('sofia.rodriguez@loantech.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/profile')

        cy.contains('a', 'Mis Préstamos').click();

        cy.get('.action-button-table.pdf-button').first().click();

        cy.get('.p-toast-message').should('contain.text', 'La descarga del PDF ha comenzado.');
    });
});