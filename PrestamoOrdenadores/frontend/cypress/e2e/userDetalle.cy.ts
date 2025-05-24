describe('UsuarioDetalle.vue', () => {
    beforeEach(() => {
        cy.visit('/');

        cy.get('input#email').type('admin.loantech.admin@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard');

        cy.contains('Ver usuarios').click();

        cy.url().should('include', '/usuarios');

        cy.get('button[title="Ver Detalles Usuario"]').first().click({force: true});

        cy.url().should('include', '/usuario/detalle/c1f40bb1900');
    });

    it('debería volver atrás al hacer clic en el botón "Volver"', () => {
        cy.get('.back-button').click();
        cy.url().should('include', '/usuarios');
    });

    it('debería mostrar los detalles del usuario en modo solo lectura', () => {
        cy.contains('Detalles del Usuario');
        cy.contains('María');
        cy.contains('Gómez');
        cy.contains('maria.loantech.profesor@gmail.com');
        cy.contains('1º Bachillerato');
        cy.contains('PROFESOR');
        cy.contains('SI');
        cy.contains('NO');
    });

    it('debería cambiar a modo edición al hacer clic en "Editar"', () => {
        cy.get('.edit-button').click();
        cy.get('#rol').should('be.visible');
        cy.get('#isActivo').should('be.visible');
        cy.get('.update-button').should('be.visible');
    });

    it('debería permitir cambiar el rol y guardar', () => {
        cy.get('.edit-button').click();
        cy.get('#rol').select('ADMIN');

        cy.get('.update-button').click();

        cy.contains('ADMIN');
        cy.get('#rol').should('not.exist');
    });

    it('debería mostrar diálogo de confirmación al hacer clic en "Derecho al Olvido"', () => {
        cy.get('.delete-button').click();
        cy.contains('Confirmar Borrado de Usuario');
        cy.contains(`¿Estás seguro de ejecutar el Derecho al Olvido`);
    });

    it('debería cancelar el diálogo de confirmación', () => {
        cy.get('.delete-button').click();
        cy.contains('Cancelar').click();
        cy.get('.p-dialog').should('not.exist');
    });

    it('debería ejecutar el borrado al confirmar', () => {
        cy.get('.delete-button').click();

        cy.contains('Borrar Permanentemente').click();
    });
});