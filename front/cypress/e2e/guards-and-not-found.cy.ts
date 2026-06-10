describe('Guards spec', () => {
  describe('AuthGuard — redirect to login when not authenticated', () => {
    it('should redirect to /login when accessing /sessions without auth', () => {
      cy.visit('/sessions');
      cy.url().should('include', '/login');
    });

    it('should redirect to /login when accessing /me without auth', () => {
      cy.visit('/me');
      cy.url().should('include', '/login');
    });

    it('should redirect to /login when accessing /sessions/create without auth', () => {
      cy.visit('/sessions/create');
      cy.url().should('include', '/login');
    });
  });

  describe('UnauthGuard — redirect to sessions when already authenticated', () => {
    beforeEach(() => {
      cy.visit('/login');

      cy.intercept('POST', '/api/auth/login', {
        statusCode: 200,
        body: {
          id: 1,
          username: 'yoga@studio.com',
          firstName: 'Admin',
          lastName: 'User',
          admin: true,
          token: 'fake-jwt-token',
          type: 'Bearer',
        },
      }).as('login');

      cy.intercept('GET', '/api/session', []).as('sessions');

      cy.get('input[formControlName=email]').type('yoga@studio.com');
      cy.get('input[formControlName=password]').type('test!1234');
      cy.get('button[type=submit]').click();
      cy.url().should('include', '/sessions');
    });

    it('should show logout link when authenticated', () => {
      cy.contains('span.link', 'Logout').should('exist');
    });

    it('should logout and redirect to login', () => {
      cy.contains('span.link', 'Logout').click();
      cy.url().should('include', '/login');
    });
  });
});

describe('Not Found page', () => {
  it('should display the 404 page when visiting an unknown route', () => {
    cy.visit('/this-page-does-not-exist', { failOnStatusCode: false });
    cy.url().should('include', '/404');
  });

  it('should display "Page not found !" on 404 page', () => {
    cy.visit('/404');
    cy.contains('Page not found !').should('exist');
  });
});

