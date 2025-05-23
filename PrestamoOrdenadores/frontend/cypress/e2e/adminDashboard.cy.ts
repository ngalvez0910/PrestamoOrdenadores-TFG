describe('Dashboard de Admin', () => {
    beforeEach(() => {
        cy.visit('/');

        cy.get('input#email').type('admin.admin.loantech@gmail.com')
        cy.get('input#password').type('Password123?')
        cy.get('button[type="submit"]').click()

        cy.url().should('include', '/admin/dashboard')
    });

    it('Debe mostrar todos los enlaces del dashboard', () => {
        cy.contains('Ver préstamos').should('exist');
        cy.contains('Ver dispositivos').should('exist');
        cy.contains('Ver usuarios').should('exist');
        cy.contains('Ver incidencias').should('exist');
        cy.contains('Ver almacenamiento').should('exist');
        cy.contains('Ver sanciones').should('exist');
    });

    const links = [
        { text: 'Ver préstamos', path: '/admin/dashboard/prestamos' },
        { text: 'Ver dispositivos', path: '/admin/dashboard/dispositivos' },
        { text: 'Ver usuarios', path: '/admin/dashboard/usuarios' },
        { text: 'Ver incidencias', path: '/admin/dashboard/incidencias' },
        { text: 'Ver almacenamiento', path: '/admin/dashboard/storage' },
        { text: 'Ver sanciones', path: '/admin/dashboard/sanciones' },
    ];

    links.forEach(({ text, path }) => {
        it(`Debe navegar correctamente al hacer clic en "${text}"`, () => {
            cy.contains(text).click();
            cy.url().should('include', path);

            cy.visit('/admin/dashboard');
        });
    });
});
