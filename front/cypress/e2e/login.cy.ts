describe('Login spec', () => {
  beforeEach(() => {
    cy.visit('/login');
  });

  it('should display the login form', () => {
    cy.get('input[formControlName=email]').should('exist');
    cy.get('input[formControlName=password]').should('exist');
  });

  it('should have the submit button disabled when form is empty', () => {
    cy.get('button[type=submit]').should('be.disabled');
  });

  it('should show Login and Register links in navbar when not authenticated', () => {
    cy.contains('a.link', 'Login').should('exist');
    cy.contains('a.link', 'Register').should('exist');
  });

  it('should toggle password visibility when clicking the eye icon', () => {
    cy.get('input[formControlName=password]').type('somepassword');
    cy.get('input[formControlName=password]').should('have.attr', 'type', 'password');
    cy.get('button[mat-icon-button]').click();
    cy.get('input[formControlName=password]').should('have.attr', 'type', 'text');
  });

  it('Login successfull', () => {
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true,
        token: 'fake-token',
        type: 'Bearer',
      },
    }).as('login');

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session');

    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type("test!1234");
    cy.get('button[type=submit]').click();

    cy.wait('@login');
    cy.url().should('include', '/sessions');
  });

  it('should display an error message on login failure', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Invalid credentials' },
    }).as('loginError');

    cy.get('input[formControlName=email]').type('wrong@test.com');
    cy.get('input[formControlName=password]').type('wrongpassword');
    cy.get('button[type=submit]').click();

    cy.wait('@loginError');
    cy.get('.error').should('contain', 'An error occurred');
  });
});
