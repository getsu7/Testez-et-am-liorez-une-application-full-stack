const mockSessionInfo = {
  id: 1,
  username: 'yoga@studio.com',
  firstName: 'Admin',
  lastName: 'User',
  admin: true,
  token: 'fake-jwt-token',
  type: 'Bearer',
};

const mockSessions = [
  {
    id: 1,
    name: 'Morning Yoga',
    description: 'A refreshing morning yoga session',
    date: '2024-06-15T00:00:00.000Z',
    teacher_id: 1,
    users: [2, 3],
    createdAt: '2024-01-01T00:00:00.000Z',
    updatedAt: '2024-01-10T00:00:00.000Z',
  },
];

const mockTeachers = [
  {
    id: 1,
    firstName: 'Margot',
    lastName: 'DELAHAYE',
    createdAt: '2024-01-01T00:00:00.000Z',
    updatedAt: '2024-01-01T00:00:00.000Z',
  },
];

const loginAndGoToSessions = (admin = true) => {
  cy.visit('/login');

  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: { ...mockSessionInfo, admin },
  }).as('login');

  cy.intercept('GET', '/api/session', mockSessions).as('getSessions');

  cy.get('input[formControlName=email]').type('yoga@studio.com');
  cy.get('input[formControlName=password]').type('test!1234');
  cy.get('button[type=submit]').click();
  cy.wait('@login');
  cy.url().should('include', '/sessions');
};


describe('Sessions List spec', () => {
  beforeEach(() => {
    loginAndGoToSessions(true);
  });

  it('should show Sessions/Account/Logout links in navbar when authenticated', () => {
    cy.contains('span.link', 'Sessions').should('exist');
    cy.contains('span.link', 'Account').should('exist');
    cy.contains('span.link', 'Logout').should('exist');
  });

  it('should display the sessions list with session cards', () => {
    cy.contains('Rentals available').should('exist');
    cy.contains('Morning Yoga').should('exist');
  });

  it('should show Create button for admin user', () => {
    cy.contains('button', 'Create').should('exist');
  });

  it('should show Detail button on each session card', () => {
    cy.contains('button', 'Detail').should('exist');
  });

  it('should show Edit button for admin user on session cards', () => {
    cy.contains('button', 'Edit').should('exist');
  });
});

describe('Sessions List spec — non-admin', () => {
  beforeEach(() => {
    loginAndGoToSessions(false);
  });

  it('should NOT show Create button for non-admin user', () => {
    cy.contains('Rentals available').should('exist');
    cy.contains('button', 'Create').should('not.exist');
  });

  it('should NOT show Edit button for non-admin user', () => {
    cy.contains('button', 'Edit').should('not.exist');
  });
});

describe('Sessions List spec — empty list', () => {
  beforeEach(() => {
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: { ...mockSessionInfo, admin: true },
    }).as('login');

    cy.intercept('GET', '/api/session', []).as('getEmptySessions');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').click();
    cy.wait('@login');
    cy.url().should('include', '/sessions');
  });

  it('should display header with Create button but no session cards when list is empty', () => {
    cy.contains('Rentals available').should('exist');
    cy.contains('button', 'Create').should('exist');
    cy.get('mat-card.item').should('not.exist');
  });
});


describe('Session Detail spec — admin', () => {
  beforeEach(() => {
    loginAndGoToSessions(true);

    cy.intercept('GET', '/api/session/1', mockSessions[0]).as('getSessionDetail');
    cy.intercept('GET', '/api/teacher/1', mockTeachers[0]).as('getTeacher');
    cy.intercept('GET', '/api/session', mockSessions).as('getSessions');

    cy.contains('button', 'Detail').click();
    cy.wait('@getSessionDetail');
    cy.wait('@getTeacher');
  });

  it('should display the session detail page', () => {
    cy.contains('Morning Yoga').should('exist');
    cy.contains('Margot').should('exist');
  });

  it('should show Delete button for admin', () => {
    cy.contains('button', 'Delete').should('exist');
  });

  it('should delete the session and navigate back to sessions', () => {
    cy.intercept('DELETE', '/api/session/1', {
      statusCode: 200,
      body: {},
    }).as('deleteSession');

    cy.intercept('GET', '/api/session', []).as('getSessionsAfterDelete');

    cy.contains('button', 'Delete').click();
    cy.wait('@deleteSession');
    cy.url().should('include', '/sessions');
  });

  it('should navigate back to /sessions when clicking the back button', () => {
    cy.get('button[mat-icon-button]').first().click();
    cy.url().should('include', '/sessions');
  });
});


describe('Session Detail spec — non-admin not participating', () => {
  const nonAdminSession = { ...mockSessions[0], users: [2, 3] };

  beforeEach(() => {
    loginAndGoToSessions(false);

    cy.intercept('GET', '/api/session/1', nonAdminSession).as('getSessionDetail');
    cy.intercept('GET', '/api/teacher/1', mockTeachers[0]).as('getTeacher');
    cy.intercept('GET', '/api/session', mockSessions).as('getSessions');

    cy.contains('button', 'Detail').click();
    cy.wait('@getSessionDetail');
    cy.wait('@getTeacher');
  });

  it('should show Participate button for non-admin user not participating', () => {
    cy.contains('button', 'Participate').should('exist');
  });

  it('should call participate endpoint and refresh session', () => {
    const sessionWithUser = { ...nonAdminSession, users: [1, 2, 3] };

    cy.intercept('POST', '/api/session/1/participate/1', {
      statusCode: 200,
      body: {},
    }).as('participate');

    cy.intercept('GET', '/api/session/1', sessionWithUser).as('getSessionAfterParticipate');
    cy.intercept('GET', '/api/teacher/1', mockTeachers[0]).as('getTeacherAgain');

    cy.contains('button', 'Participate').click();
    cy.wait('@participate');
    cy.wait('@getSessionAfterParticipate');
    cy.contains('button', 'Do not participate').should('exist');
  });
});


describe('Session Detail spec — non-admin already participating', () => {
  const sessionWithUser = { ...mockSessions[0], users: [1, 2, 3] };

  beforeEach(() => {
    loginAndGoToSessions(false);

    cy.intercept('GET', '/api/session/1', sessionWithUser).as('getSessionDetail');
    cy.intercept('GET', '/api/teacher/1', mockTeachers[0]).as('getTeacher');
    cy.intercept('GET', '/api/session', mockSessions).as('getSessions');

    cy.contains('button', 'Detail').click();
    cy.wait('@getSessionDetail');
    cy.wait('@getTeacher');
  });

  it('should show Do not participate button when user is already participating', () => {
    cy.contains('button', 'Do not participate').should('exist');
  });

  it('should call unParticipate endpoint and refresh session', () => {
    const sessionWithoutUser = { ...mockSessions[0], users: [2, 3] };

    cy.intercept('DELETE', '/api/session/1/participate/1', {
      statusCode: 200,
      body: {},
    }).as('unParticipate');

    cy.intercept('GET', '/api/session/1', sessionWithoutUser).as('getSessionAfterUnParticipate');
    cy.intercept('GET', '/api/teacher/1', mockTeachers[0]).as('getTeacherAgain');

    cy.contains('button', 'Do not participate').click();
    cy.wait('@unParticipate');
    cy.wait('@getSessionAfterUnParticipate');
    cy.contains('button', 'Participate').should('exist');
  });
});


describe('Session Form — create mode', () => {
  beforeEach(() => {
    loginAndGoToSessions(true);

    cy.intercept('GET', '/api/teacher', mockTeachers).as('getTeachers');

    cy.contains('button', 'Create').click();
    cy.url().should('include', '/sessions/create');
    cy.wait('@getTeachers');
  });

  it('should display the create session form', () => {
    cy.contains('Create session').should('exist');
    cy.get('input[formControlName=name]').should('exist');
    cy.get('input[formControlName=date]').should('exist');
    cy.get('textarea[formControlName=description]').should('exist');
  });

  it('should have the Save button disabled when form is empty', () => {
    cy.get('button[type=submit]').should('be.disabled');
  });

  it('should navigate back to /sessions via the back arrow', () => {
    cy.intercept('GET', '/api/session', mockSessions).as('getSessions');
    cy.get('button[mat-icon-button]').click();
    cy.url().should('include', '/sessions');
  });

  it('should create a session and navigate back to /sessions', () => {
    cy.intercept('POST', '/api/session', {
      statusCode: 200,
      body: {
        id: 2,
        name: 'New Session',
        description: 'A brand new yoga session',
        date: '2024-07-01T00:00:00.000Z',
        teacher_id: 1,
        users: [],
      },
    }).as('createSession');

    cy.intercept('GET', '/api/session', mockSessions).as('getSessions');

    cy.get('input[formControlName=name]').type('New Session');
    cy.get('input[formControlName=date]').type('2024-07-01');
    cy.get('mat-select[formControlName=teacher_id]').click();
    cy.get('mat-option').first().click();
    cy.get('textarea[formControlName=description]').type('A brand new yoga session');

    cy.get('button[type=submit]').click();
    cy.wait('@createSession');
    cy.url().should('include', '/sessions');
  });
});

describe('Session Form — update mode', () => {
  beforeEach(() => {
    loginAndGoToSessions(true);

    cy.intercept('GET', '/api/teacher', mockTeachers).as('getTeachers');
    cy.intercept('GET', '/api/session/1', mockSessions[0]).as('getSessionForEdit');

    cy.contains('button', 'Edit').click();
    cy.url().should('include', '/sessions/update/1');
    cy.wait('@getTeachers');
    cy.wait('@getSessionForEdit');
  });

  it('should display the update session form with pre-filled data', () => {
    cy.contains('Update session').should('exist');
    cy.get('input[formControlName=name]').should('have.value', 'Morning Yoga');
  });

  it('should navigate back to /sessions via the back arrow', () => {
    cy.intercept('GET', '/api/session', mockSessions).as('getSessions');
    cy.get('button[mat-icon-button]').click();
    cy.url().should('include', '/sessions');
  });

  it('should update the session and navigate back to /sessions', () => {
    cy.intercept('PUT', '/api/session/1', {
      statusCode: 200,
      body: { ...mockSessions[0], name: 'Updated Session' },
    }).as('updateSession');

    cy.intercept('GET', '/api/session', mockSessions).as('getSessions');

    cy.get('input[formControlName=name]').clear().type('Updated Session');
    cy.get('button[type=submit]').click();
    cy.wait('@updateSession');
    cy.url().should('include', '/sessions');
  });
});

