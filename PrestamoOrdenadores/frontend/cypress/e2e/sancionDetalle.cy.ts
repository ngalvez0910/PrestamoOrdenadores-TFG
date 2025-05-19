describe('SancionDetalle Component', () => {
    beforeEach(() => {
        cy.visit('/');

        cy.get('input#email').type('admin@admin.loantech.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard');

        cy.contains('Ver sanciones').click();

        cy.url().should('include', '/sanciones');

        cy.get('button[title="Ver Detalles Sanción"]').first().click({force: true});

        cy.url().should('include', '/sancion/detalle/SANC000001');
    });

    it('botón volver navega hacia atrás', () => {
        cy.get('.back-button').click();
        cy.url().should('include', '/sanciones');
    });

    it('muestra detalles de la sanción correctamente', () => {
        cy.get('.readonly-field').contains('SANC000001');
        cy.get('.readonly-field').contains('2023LT044');
        cy.get('.readonly-field').contains('123456789ab');
        cy.get('.readonly-field').contains('20-12-2024');
        cy.get('.readonly-field').contains('19-05-2025');
        cy.get('.readonly-field').contains('NO');
        cy.get('.status-badge').contains('ADVERTENCIA');
    });

    it('permite alternar modo edición y cancelar', () => {
        cy.get('.edit-button').contains('Editar').click();
        cy.get('select#tipo').should('exist').and('have.value', 'ADVERTENCIA');
        cy.get('.edit-button').contains('Cancelar').click();
        cy.get('select#tipo').should('not.exist');
    });

    it('muestra mensaje info si no hay cambios al actualizar', () => {
        cy.get('.edit-button').click();
        cy.get('.update-button').click();
        cy.get('.p-toast-message-info').should('exist').and('contain', 'No se realizaron cambios.');
        cy.get('.edit-button').contains('Editar');
    });

    it('actualiza la sanción correctamente', () => {
        cy.get('.edit-button').click();
        cy.get('select#tipo').select('BLOQUEO_TEMPORAL');
        cy.get('.update-button').click();

        cy.get('.edit-button').contains('Editar');
        cy.get('.status-badge').contains('BLOQUEO TEMPORAL');
        cy.get('.p-toast-message-success').should('exist').and('contain', 'Sanción actualizada.');
    });
});