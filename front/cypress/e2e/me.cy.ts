const mockAdminSessionInfo = {
  id: 1,
  username: 'yoga@studio.com',
  firstName: 'Admin',
  lastName: 'User',
  admin: true,
  token: 'fake-jwt-token',
  type: 'Bearer',
};

const mockAdminUser = {
  id: 1,
  email: 'yoga@studio.com',
  firstName: 'Admin',
  lastName: 'User',
  admin: true,
  password: '',
  createdAt: '2023-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
};

const mockNonAdminUser = {
  id: 2,
  email: 'user@test.com',
  firstName: 'Jane',
  lastName: 'Doe',
  admin: false,
  password: '',
  createdAt: '2023-06-01T00:00:00.000Z',
  updatedAt: '2024-02-01T00:00:00.000Z',
};

const loginAndGoToMe = (admin = true) => {
  const sessionInfo = admin
    ? { ...mockAdminSessionInfo, admin: true, id: 1 }
    : { ...mockAdminSessionInfo, admin: false, id: 2, username: 'user@test.com', firstName: 'Jane', lastName: 'Doe' };

  const userInfo = admin ? mockAdminUser : mockNonAdminUser;

  cy.visit('/login');

  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: sessionInfo,
  }).as('login');

  cy.intercept('GET', '/api/session', []).as('sessions');
  cy.intercept('GET', `/api/user/${sessionInfo.id}`, userInfo).as('getUser');

  cy.get('input[formControlName=email]').type(admin ? 'yoga@studio.com' : 'user@test.com');
  cy.get('input[formControlName=password]').type('test!1234');
  cy.get('button[type=submit]').click();
  cy.wait('@login');
  cy.url().should('include', '/sessions');

  cy.contains('span.link', 'Account').click();
  cy.wait('@getUser');
  cy.url().should('include', '/me');
};


describe('Me Component — admin user', () => {
  beforeEach(() => {
    loginAndGoToMe(true);
  });

  it('should display user information', () => {
    cy.contains('User information').should('exist');
    cy.contains('Admin').should('exist');
    cy.contains('USER').should('exist');
    cy.contains('yoga@studio.com').should('exist');
  });

  it('should display "You are admin" for admin user', () => {
    cy.contains('You are admin').should('exist');
  });

  it('should NOT display the Delete button for admin', () => {
    cy.contains('Delete my account:').should('not.exist');
  });

  it('should navigate back to /sessions when clicking the back button', () => {
    cy.get('button[mat-icon-button]').first().click();
    cy.url().should('include', '/sessions');
  });
});


describe('Me Component — non-admin user', () => {
  beforeEach(() => {
    loginAndGoToMe(false);
  });

  it('should display user information for non-admin', () => {
    cy.contains('User information').should('exist');
    cy.contains('Jane').should('exist');
    cy.contains('DOE').should('exist');
    cy.contains('user@test.com').should('exist');
  });

  it('should show Delete my account section for non-admin', () => {
    cy.contains('Delete my account:').should('exist');
  });

  it('should NOT display "You are admin" for non-admin', () => {
    cy.contains('You are admin').should('not.exist');
  });

  it('should navigate back to /sessions when clicking the back button', () => {
    cy.get('button[mat-icon-button]').first().click();
    cy.url().should('include', '/sessions');
  });

  it('should delete account and redirect to /login', () => {
    cy.intercept('DELETE', '/api/user/2', {
      statusCode: 200,
      body: {},
    }).as('deleteUser');

    cy.get('button').contains('Detail').click();
    cy.wait('@deleteUser');
    cy.url().should('include', '/login');
  });
});
