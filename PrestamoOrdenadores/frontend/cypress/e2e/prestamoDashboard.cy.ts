describe('PrestamoDashboard Component', () => {
    beforeEach(() => {
        cy.visit('/');

        cy.get('input#email').type('admin.admin.loantech@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard');

        cy.contains('Ver préstamos').click();

        cy.url().should('include', '/prestamos');
    });

    it('debe cargar correctamente el componente y mostrar los datos iniciales', () => {
        cy.get('.p-datatable-custom').should('exist');
        cy.get('.p-datatable-custom .p-datatable-tbody tr').should('exist');
    });

    it('permite buscar por GUID, Usuario o Dispositivo', () => {
        cy.get('#search-input').clear().type('123');
        cy.get('.p-datatable-custom').should('contain', '123');
    });

    it('muestra y oculta el calendario correctamente', () => {
        cy.get('.calendar-icon-container').click();
        cy.get('.calendar-dropdown').should('be.visible');
        cy.get('body').click(0, 0);
        cy.get('.calendar-dropdown').should('not.exist');
    });

    it('filtra por fecha al seleccionar en el calendario', () => {
        cy.get('.calendar-icon-container').click();
        cy.get('.p-datepicker-calendar td span:not(.p-disabled)').first().click();
        cy.get('.active-filter-badge').should('exist');
    });

    it('limpia el filtro de fecha', () => {
        cy.get('.calendar-icon-container').click();
        cy.get('.p-datepicker-calendar td span:not(.p-disabled)').first().click();
        cy.get('.active-filter-badge').click();
        cy.get('.active-filter-badge').should('not.exist');
    });

    it('cambia de página en la tabla', () => {
        cy.get('.p-paginator .p-paginator-next').click();
        cy.get('.p-datatable-tbody').should('exist');
    });

    it('abre el diálogo de eliminación al hacer clic en el botón de eliminar', () => {
        cy.get('.p-datatable-tbody .action-button.delete-button').first().click();
        cy.get('.p-dialog').should('contain', 'Confirmar Eliminación');
    });

    it('permite cancelar el diálogo de eliminación', () => {
        cy.get('.p-datatable-tbody .action-button.delete-button').first().click();
        cy.get('.p-dialog .p-button-text').contains('Cancelar').click();
        cy.get('.p-dialog').should('not.exist');
    });

    it('confirma eliminación de un préstamo', () => {
        cy.intercept('DELETE', '**/prestamos/*').as('deletePrestamo');

        cy.get('.p-datatable-tbody .action-button.delete-button').first().click();
        cy.get('.p-dialog .p-button').contains('Eliminar').click();

        cy.get('.p-dialog').should('not.exist');
        cy.get('.p-toast').should('exist');
    });
});