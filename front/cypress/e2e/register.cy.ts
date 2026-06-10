
describe('Register spec', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it('should display the register form', () => {
    cy.get('mat-card-title').should('contain', 'Register');
    cy.get('input[formControlName=firstName]').should('exist');
    cy.get('input[formControlName=lastName]').should('exist');
    cy.get('input[formControlName=email]').should('exist');
    cy.get('input[formControlName=password]').should('exist');
  });

  it('should have the submit button disabled when form is empty', () => {
    cy.get('button[type=submit]').should('be.disabled');
  });

  it('should register successfully and redirect to /login', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: {},
    }).as('register');

    cy.get('input[formControlName=firstName]').type('Jane');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('newuser@test.com');
    cy.get('input[formControlName=password]').type('password123');

    cy.get('button[type=submit]').click();
    cy.wait('@register');

    cy.url().should('include', '/login');
  });

  it('should display an error message on register failure', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'Email already used' },
    }).as('registerError');

    cy.get('input[formControlName=firstName]').type('Jane');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('existing@test.com');
    cy.get('input[formControlName=password]').type('password123');

    cy.get('button[type=submit]').click();
    cy.wait('@registerError');

    cy.get('.error').should('contain', 'An error occurred');
  });
});

