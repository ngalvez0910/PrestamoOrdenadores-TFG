describe('Página de Notificaciones', () => {
    beforeEach(() => {
        cy.visit('/');
    });

    it('Carga las notificaciones correctamente', () => {
        cy.get('input#email').type('admin.admin.loantech@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard')

        cy.contains('a', 'Notificaciones').click();

        cy.get('.notification-item').should('have.length.at.least', 1);
    });

    it('Muestra el botón de "Marcar todas como leídas" solo si hay no leídas', () => {
        cy.get('input#email').type('admin.admin.loantech@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard')

        cy.contains('a', 'Notificaciones').click();

        cy.get('.action-button.secondary-button')
            .should('exist')
            .and('contain', 'Marcar todas como leídas');
    });

    it('Marca una notificación como leída', () => {
        cy.get('input#email').type('admin.admin.loantech@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard')

        cy.contains('a', 'Notificaciones').click();

        cy.get('.notification-item.is-unread').first().find('.pi-eye').click();
    });

    it('Elimina una notificación', () => {
        cy.get('input#email').type('admin.admin.loantech@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard')

        cy.contains('a', 'Notificaciones').click();

        cy.get('.notification-item').should('exist').first().find('.pi-trash').click();
    });

    it('Marca todas como leídas', () => {
        cy.get('input#email').type('admin.admin.loantech@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard')

        cy.contains('a', 'Notificaciones').click();

        cy.get('.action-button.secondary-button').click();

        cy.get('.notification-item').each(item => {
            cy.wrap(item).should('not.have.class', 'is-unread');
        });
    });

    it('Abre el diálogo de detalles de notificación', () => {
        cy.get('input#email').type('admin.admin.loantech@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard')

        cy.contains('a', 'Notificaciones').click();

        cy.get('.notification-item').first().click();

        cy.get('.notification-dialog').should('be.visible');
        cy.get('.dialog-message').should('exist');
    });

    it('Navega al enlace desde el diálogo si existe', () => {
        cy.get('input#email').type('admin.admin.loantech@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard')

        cy.contains('a', 'Notificaciones').click();

        cy.get('.notification-item').first().click();

        cy.get('.notification-dialog').should('be.visible');
        cy.get('.p-button-text').contains('Ir al enlace').click();

        cy.location('pathname').should('not.eq', '/notificaciones');
    });

    it('Muestra mensaje si no hay notificaciones', () => {
        cy.get('input#email').type('pedro.ramos.profesor.loantech@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/profile')

        cy.contains('a', 'Notificaciones').click();

        cy.contains('No tienes notificaciones nuevas.').should('be.visible');
    });
});
