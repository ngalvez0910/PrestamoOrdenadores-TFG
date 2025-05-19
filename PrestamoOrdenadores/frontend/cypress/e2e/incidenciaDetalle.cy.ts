describe('IncidenciaDetalle.vue', () => {
    beforeEach(() => {
        cy.visit('/');

        cy.get('input#email').type('admin@admin.loantech.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard');

        cy.contains('Ver incidencias').click();

        cy.url().should('include', '/incidencias');
    });

    it('debe renderizar los detalles correctamente', () => {
        cy.get('button[title="Ver Detalles Incidencia"]').first().click({force: true});

        cy.url().should('include', '/incidencia/detalle/INC000003');

        cy.contains('Detalles de la Incidencia');
        cy.get('.asunto-display').should('contain.text', 'Cargador roto');
        cy.get('textarea#descripcion').should('have.value', 'El cargador esta despeluchado');
        cy.get('.status-badge').should('contain.text', 'PENDIENTE');
    });

    it('debe volver atrás al hacer clic en el botón de volver', () => {
        cy.get('button[title="Ver Detalles Incidencia"]').first().click({force: true});

        cy.url().should('include', '/incidencia/detalle/INC000003');

        cy.get('.back-button').click();
        cy.url().should('include', '/incidencias');
    });

    it('debe entrar y salir del modo edición', () => {
        cy.get('button[title="Ver Detalles Incidencia"]').first().click({force: true});

        cy.url().should('include', '/incidencia/detalle/INC000003');

        cy.get('.edit-button').click();
        cy.get('input#asunto').should('be.visible');
        cy.get('select#estado').should('be.visible');

        cy.get('.edit-button').click();
        cy.get('.asunto-display').should('contain.text', 'Cargador roto');
    });

    it('debe mostrar un toast si no hay cambios', () => {
        cy.get('button[title="Ver Detalles Incidencia"]').first().click({force: true});

        cy.url().should('include', '/incidencia/detalle/INC000003');

        cy.get('.edit-button').click();
        cy.get('.update-button').click();
        cy.contains('No se realizaron cambios.');
    });

    it('debe actualizar la incidencia si se cambia el estado', () => {
        cy.get('button[title="Ver Detalles Incidencia"]').first().click({force: true});

        cy.url().should('include', '/incidencia/detalle/INC000003');

        cy.get('.edit-button').click();

        cy.get('select#estado').select('RESUELTO');

        cy.get('.update-button').click();
        cy.get('.status-badge').should('contain.text', 'RESUELTO');
    });

    it('no debe mostrar botón de editar si la incidencia está resuelta', () => {
        cy.get('button[title="Ver Detalles Incidencia"]').eq(2).click({force: true});

        cy.url().should('include', '/incidencia/detalle/INC000003');

        cy.get('.edit-button').should('not.exist');
    });
});
