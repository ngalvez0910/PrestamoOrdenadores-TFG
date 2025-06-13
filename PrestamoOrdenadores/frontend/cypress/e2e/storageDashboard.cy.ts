describe('StorageDashboard', () => {
    beforeEach(() => {
        cy.visit('/');

        cy.get('input#email').type('admin.loantech.admin@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard');

        cy.contains('Ver almacenamiento').click();

        cy.url().should('include', '/storage');
    });

    it('renderiza todos los bloques del dashboard', () => {
        const secciones = [
            'Dispositivos',
            'Usuarios',
            'Incidencias',
            'Préstamos',
            'Sanciones',
            'Copia de Seguridad'
        ];

        secciones.forEach((text) => {
            cy.contains('.dashboard-box', text).should('exist');
        });
    });

    it('dispara descarga de XLSX al hacer clic en cada botón', () => {
        const botones = [
            'Dispositivos',
            'Usuarios',
            'Incidencias',
            'Préstamos',
            'Sanciones'
        ];

        botones.forEach((label) => {
            cy.contains('.dashboard-box', label)
                .find('button')
                .click();

            cy.get('.p-toast-message')
                .should('contain.text', 'Descarga Iniciada');
        });
    });

    it('crea copia de seguridad y muestra notificación', () => {
        cy.contains('.dashboard-box', 'Copia de Seguridad')
            .contains('Exportar')
            .click();

        cy.get('.p-toast-message')
            .should('contain.text', 'Copia de Seguridad Creada');
    });

    it('abre el modal al importar y muestra la lista de backups', () => {
        cy.contains('.dashboard-box', 'Copia de Seguridad')
            .contains('Importar')
            .click();

        cy.get('.modal-restauracion').should('be.visible').contains('Selecciona un backup para restaurar');

        cy.get('.backup-item').contains('db_backup_');
    });

    it('permite cerrar el modal', () => {
        cy.contains('.dashboard-box', 'Copia de Seguridad')
            .contains('Importar')
            .click();

        cy.get('.modal-close-button').click();
        cy.get('.modal-restauracion').should('not.exist');
    });

    it('permite restaurar un backup desde el modal', () => {
        cy.contains('.dashboard-box', 'Copia de Seguridad')
            .contains('Importar')
            .click();

        cy.get('.boton-restaurar-item').first().click();

        cy.get('.p-toast-message')
            .should('contain.text', 'Restauración Exitosa');
    });
});
