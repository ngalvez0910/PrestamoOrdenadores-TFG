describe('Registro de usuario', () => {
    beforeEach(() => {
        cy.visit('/registro');
    });

    it('Renderiza el formulario de registro', () => {
        cy.contains('Registro');
        cy.get('form').should('exist');
    });

    it('Valida campos requeridos', () => {
        cy.get('button[type="submit"]').click();
        cy.contains('Campo obligatorio');
        cy.contains('El correo electrónico es obligatorio');
        cy.contains('La contraseña es obligatoria.');
        cy.contains('Por favor, confirma la contraseña.');
    });

    it('Valida formato de Número de Identificación incorrecto', () => {
        cy.get('#numeroIdentificacion').type('12345');
        cy.get('button[type="submit"]').click();
        cy.contains('Formato incorrecto');
    });

    it('Valida correo inválido', () => {
        cy.get('#email').type('correo@dominio.com');
        cy.get('button[type="submit"]').click();
        cy.contains('El correo debe ser válido y terminar en @loantech.com');
    });

    it('Valida contraseña débil', () => {
        cy.get('#password').type('abc');
        cy.get('#confirmPassword').type('abc');
        cy.get('button[type="submit"]').click();
        cy.contains('La contraseña debe cumplir los siguientes requisitos');
    });

    it('Valida que las contraseñas coincidan', () => {
        cy.get('#password').type('Password123!');
        cy.get('#confirmPassword').type('Otra123!');
        cy.get('button[type="submit"]').click();
        cy.contains('Las contraseñas no coinciden');
    });

    it('Realiza un registro válido', () => {
        cy.intercept('POST', '/api/auth/register', {
            statusCode: 200,
            body: { token: 'fake-token' },
        }).as('registerRequest');

        cy.get('#numeroIdentificacion').type('2023LT123');
        cy.get('#nombre').type('Pepe');
        cy.get('#apellidos').type('Pérez');
        cy.get('#curso').type('2DAW');
        cy.get('#tutor').type('Profesor X');
        cy.get('#email').type('pepe@loantech.com');
        cy.get('#password').type('Password123!');
        cy.get('#confirmPassword').type('Password123!');

        cy.get('button[type="submit"]').click();

        cy.contains('Registro Exitoso');
    });
});
