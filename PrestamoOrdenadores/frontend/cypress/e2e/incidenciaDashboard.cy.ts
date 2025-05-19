describe('Incidencias Dashboard', () => {
    beforeEach(() => {
        cy.visit('/');

        cy.get('input#email').type('admin@admin.loantech.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard');

        cy.contains('Ver incidencias').click();

        cy.url().should('include', '/incidencias');
    });

    it('Carga los datos correctamente', () => {
        cy.contains('GUID').should('exist');
        cy.contains('Asunto').should('exist');
        cy.contains('Estado').should('exist');
        cy.contains('Usuario').should('exist');
        cy.contains('Fecha Creación').should('exist');
    });

    it('Filtra por texto en el input de búsqueda', () => {
        cy.get('#search-input').type('pendiente');
        cy.get('.p-datatable-tbody').within(() => {
            cy.contains('pendiente', { matchCase: false }).should('exist');
        });
    });

    it('Muestra y selecciona fecha desde el calendario', () => {
        cy.get('.calendar-icon-container').click();
        cy.get('.p-datepicker-calendar td').contains('19').click();

        cy.get('.active-filter-badge').should('contain.text', '19/05/2025');
        cy.get('.p-datatable-tbody tr').contains('INC000003');
    });

    it('Limpia el filtro de fecha', () => {
        cy.get('.calendar-icon-container').click();
        cy.get('.p-datepicker-calendar td').contains('1').click();
        cy.get('.active-filter-badge').should('exist').click();
        cy.get('.active-filter-badge').should('not.exist');

        cy.get('.p-datatable-tbody tr').contains('INC000003');
    });

    it('Navega a detalle de una incidencia al hacer click en ver', () => {
        cy.get('.action-button.view-button').first().click();
        cy.url().should('include', '/incidencia/detalle/INC000003');
    });

    it('Abre y cierra el diálogo de eliminación', () => {
        cy.get('.action-button.delete-button').first().click();
        cy.contains('¿Está seguro de que desea eliminar').should('exist');

        cy.contains('Cancelar').click();
        cy.contains('¿Está seguro de que desea eliminar').should('not.exist');
    });

    it('Confirma eliminación de incidencia', () => {
        cy.get('.action-button.delete-button').first().click();
        cy.contains('Eliminar').click();

        cy.contains('¿Está seguro de que desea eliminar').should('not.exist');
    });
});
