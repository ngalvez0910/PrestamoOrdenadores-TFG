describe('Profile Page', () => {
    beforeEach(() => {
        cy.visit('/');
        cy.get('input#email').type('sofia.rodriguez.loantech@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/profile')
    });

    it('muestra datos del perfil correctamente', () => {
        cy.contains('Mi Perfil');
        cy.get('.avatar-image');
        cy.get('#nombre').contains('Sofía Rodríguez');
        cy.get('#email').contains('sofia.rodriguez.loantech@gmail.com');
        cy.get('#curso').contains('3º ESO');
    });

    it('abre y cierra el diálogo de cambio de contraseña', () => {
        cy.get('.change-password-button').click();
        cy.get('.p-dialog').should('contain.text', 'Cambiar Contraseña');

        cy.get('button[type="submit"]').click();
        cy.contains('La contraseña antigua es obligatoria.');
        cy.contains('La nueva contraseña es obligatoria.');
        cy.contains('Por favor, confirma la nueva contraseña.');

        cy.get('.p-dialog .pi-times').click();
        cy.get('.p-dialog').should('not.exist');
    });

    it('abre y usa el diálogo de selección de avatar', () => {
        cy.get('.avatar-button').click();
        cy.get('.p-dialog').should('contain.text', 'Seleccionar Avatar');

        cy.get('.avatar-option').eq(2).click().should('have.class', 'selected');

        cy.get('button').contains('Confirmar Selección').click();

        cy.get('.p-toast-message-success').should('exist');

        cy.get('.p-dialog').should('not.exist');

        cy.get('.avatar-image').should('not.have.attr', 'src', 'https://example.com/avatar.jpg');
    });

    it('cerrar sesión redirige correctamente', () => {
        cy.get('.logout-button-tabs').click();

        cy.url().should('not.include', '/profile');
    });
});
