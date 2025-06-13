describe('DispositivosDashboard', () => {
    beforeEach(() => {
        cy.visit('/');

        cy.get('input#email').type('admin@admin.loantech.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard');

        cy.contains('Ver dispositivos').click();

        cy.url().should('include', '/dispositivos');
    });

    it('Carga correctamente la tabla con dispositivos', () => {
        cy.get('.p-datatable').should('exist');
        cy.get('.p-datatable .p-datatable-tbody tr').contains('5CD456QWER');
    });

    it('Filtra por número de serie', () => {
        cy.get('#search-input').type('5CD');
        cy.get('.p-datatable .p-datatable-tbody tr').contains('5CD456QWER');
    });

    it('Cambia de página con la paginación', () => {
        cy.get('.p-paginator .p-paginator-next').click();
        cy.get('.p-datatable .p-datatable-tbody tr').should('exist');
    });

    it('Abre el modal para añadir stock', () => {
        cy.contains('button', 'Añadir Stock').click();
        cy.get('.add-stock-dialog').should('be.visible');
    });

    it('Valida campos vacíos al añadir dispositivo', () => {
        cy.contains('button', 'Añadir Stock').click();
        cy.get('button[type=submit]').click();
        cy.get('.p-toast-message').contains('complete todos los campos');
    });

    it('Valida formato incorrecto del número de serie', () => {
        cy.contains('button', 'Añadir Stock').click();
        cy.get('#dispositivoNumSerie').type('XYZ123');
        cy.get('#dispositivoComponentes').type('Cargador');
        cy.get('button[type=submit]').click();
        cy.get('.p-toast-message').contains('formato del Número de Serie es incorrecto');
    });

    it('Añade dispositivo correctamente', () => {
        cy.contains('button', 'Añadir Stock').click();
        cy.get('#dispositivoNumSerie').type('1AB123CDEF');
        cy.get('#dispositivoComponentes').type('Ratón, cargador');
        cy.get('button[type=submit]').click();
    });

    it('Muestra diálogo de confirmación al eliminar', () => {
        cy.get('.action-button.delete-button').first().click();
        cy.get('.p-dialog').contains('¿Está seguro');
    });

    it('Confirma la eliminación de un dispositivo', () => {
        cy.get('.action-button.delete-button').first().click();
        cy.get('.p-dialog .action-button-dialog.primary-button').click();
        cy.get('.p-dialog').should('not.exist');
    });
});