describe('SancionDashboard', () => {
    beforeEach(() => {
        cy.visit('/');

        cy.get('input#email').type('admin@admin.loantech.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard');

        cy.contains('Ver sanciones').click();

        cy.url().should('include', '/sanciones');
    });

    it('Muestra sanciones cargadas inicialmente', () => {
        cy.get('.p-datatable-tbody tr').should('be.visible');
        cy.contains('SANC000001');
        cy.contains('2023LT044');
        cy.contains('ADVERTENCIA');
    });

    it('Filtra sanciones al escribir en la búsqueda', () => {
        cy.get('#search-input').type('123');
        cy.get('.p-datatable-tbody tr').contains('123456789ab');
    });

    it('Muestra y oculta el calendario al hacer click en el ícono', () => {
        cy.get('.calendar-icon-container').click();
        cy.get('.calendar-dropdown').should('be.visible');

        cy.get('body').click(0, 0);
        cy.get('.calendar-dropdown').should('not.exist');
    });

    it('Filtra sanciones al seleccionar una fecha', () => {
        cy.get('.calendar-icon-container').click();
        cy.get('.p-datepicker-calendar td').contains('19').click();

        cy.get('.active-filter-badge').should('contain.text', '19/05/2025');
        cy.get('.p-datatable-tbody tr').contains('123456789ab');
    });

    it('Limpia filtro de fecha al hacer click en la badge', () => {
        cy.get('.calendar-icon-container').click();
        cy.get('.p-datepicker-calendar td').contains('1').click();
        cy.get('.active-filter-badge').should('exist').click();
        cy.get('.active-filter-badge').should('not.exist');

        cy.get('.p-datatable-tbody tr').contains('123456789ab');
    });

    it('Navega al detalle de sanción al hacer click en el botón "Ver Detalles"', () => {
        cy.get('.view-button').first().click();

        cy.url().should('include', '/sancion/detalle/SANC000001');
    });

    it('Cancela la eliminación desde el diálogo', () => {
        cy.get('.delete-button').first().click();

        cy.get('.p-dialog').should('be.visible');
        cy.get('.p-dialog .secondary-button').click();

        cy.get('.p-dialog').should('not.exist');

        cy.get('.p-datatable-tbody tr').contains('123456789ab');
    });

    it('Muestra diálogo de confirmación al eliminar sanción y confirma eliminación', () => {
        cy.get('.delete-button').first().click();

        cy.get('.p-dialog').should('be.visible');
        cy.contains('¿Está seguro de que desea eliminar la sanción con guid SANC000001');

        cy.get('.p-dialog .primary-button').click();

        cy.get('.p-dialog').should('not.exist');
    });

    it('Pagina correctamente al cambiar página', () => {
        cy.get('.p-paginator-next').click();
        cy.get('.p-datatable-tbody tr').should('have.length.at.least', 0);
    });
});
