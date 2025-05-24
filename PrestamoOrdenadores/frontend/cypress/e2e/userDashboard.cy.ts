describe('UsuariosDashboard', () => {
    beforeEach(() => {
        cy.visit('/');

        cy.get('input#email').type('admin.loantech.admin@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard');

        cy.contains('Ver usuarios').click();

        cy.url().should('include', '/usuarios');
    });

    it('Debe renderizar correctamente el DataTable con usuarios', () => {
        cy.contains('Núm. Identificación').should('exist');
        cy.contains('Email').should('exist');
        cy.contains('Nombre').should('exist');
        cy.contains('Curso').should('exist');
        cy.contains('Tutor').should('exist');
        cy.contains('Rol').should('exist');

        cy.contains('sofia.rodriguez.loantech@gmail.com').should('exist');
        cy.contains('david.garcia.loantech@gmail.com').should('exist');
    });

    it('Filtra usuarios al buscar por nombre', () => {
        cy.get('#search-input').type('ana');
        cy.contains('Ana López').should('exist');
        cy.contains('Luis Martín').should('not.exist');
    });

    it('Redirige al detalle del usuario al hacer clic en Ver', () => {
        cy.get('button[title="Ver Detalles Usuario"]').first().click({force: true});
        cy.url().should('include', '/usuario/detalle/c1f40bb1900');
    });

    it('Abre diálogo de confirmación al hacer clic en eliminar', () => {
        cy.get('button[title="Eliminar/Desactivar Usuario"]').first().click({force: true});
        cy.contains('¿Está seguro de que desea eliminar el usuario').should('exist');
        cy.contains('Cancelar').should('exist');
        cy.contains('Eliminar').should('exist');
    });

    it('Cancela eliminación correctamente', () => {
        cy.get('button[title="Eliminar/Desactivar Usuario"]').first().click({force: true});
        cy.contains('Cancelar').click();
        cy.get('.p-dialog').should('not.exist');
    });

    it('Confirma eliminación de usuario', () => {
        cy.get('button[title="Eliminar/Desactivar Usuario"]').first().click({force: true});
        cy.contains('Eliminar').click();
        cy.get('.p-dialog').should('not.exist');
    });
});