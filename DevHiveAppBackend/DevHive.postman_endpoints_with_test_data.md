DevHive API Endpoints — Example Test Data

Base URL: http://localhost:8080
Authorization: Bearer <YOUR_TOKEN>

Example variable values:
- orgId = 1
- projectId = 1
- taskId = 1
- commentId = 1
- snippetId = 1
- notificationId = 1
- userId = 2
- searchQuery = dev

Endpoints (method — full URL)

Auth
- POST http://localhost:8080/api/auth/register
  Body (JSON):
  {
    "name": "Test User",
    "email": "test@example.com",
    "password": "Password123!"
  }

- POST http://localhost:8080/api/auth/login
  Body (JSON):
  {
    "email": "test@example.com",
    "password": "Password123!"
  }

Organizations
- POST http://localhost:8080/api/orgs
  Headers: Authorization: Bearer <YOUR_TOKEN>
  Body (JSON): { "name": "DevHive Org" }

- GET http://localhost:8080/api/orgs
  Headers: Authorization: Bearer <YOUR_TOKEN>

- POST http://localhost:8080/api/orgs/1/invite
  Headers: Authorization: Bearer <YOUR_TOKEN>
  Body (JSON): { "email": "invitee@example.com", "role": "DEVELOPER" }

- GET http://localhost:8080/api/orgs/1/members
  Headers: Authorization: Bearer <YOUR_TOKEN>

Membership
- PUT http://localhost:8080/api/members/1/2
  Headers: Authorization: Bearer <YOUR_TOKEN>
  Body (JSON): { "role": "VIEWER" }

- DELETE http://localhost:8080/api/members/1/2
  Headers: Authorization: Bearer <YOUR_TOKEN>

Projects
- POST http://localhost:8080/api/projects
  Headers: Authorization: Bearer <YOUR_TOKEN>
  Body (JSON): { "name": "First Project", "organizationId": 1 }

- GET http://localhost:8080/api/projects?orgId=1
  Headers: Authorization: Bearer <YOUR_TOKEN>

- GET http://localhost:8080/api/projects/1
  Headers: Authorization: Bearer <YOUR_TOKEN>

- PUT http://localhost:8080/api/projects/1
  Headers: Authorization: Bearer <YOUR_TOKEN>
  Body (JSON): { "name": "Updated Project Name" }

- DELETE http://localhost:8080/api/projects/1
  Headers: Authorization: Bearer <YOUR_TOKEN>

Tasks
- POST http://localhost:8080/api/tasks
  Headers: Authorization: Bearer <YOUR_TOKEN>
  Body (JSON):
  {
    "title": "Implement Login",
    "description": "Build auth endpoints and JWT handling",
    "priority": "HIGH",
    "projectId": 1
  }

- GET http://localhost:8080/api/tasks?projectId=1
  Headers: Authorization: Bearer <YOUR_TOKEN>

- PUT http://localhost:8080/api/tasks/1
  Headers: Authorization: Bearer <YOUR_TOKEN>
  Body (JSON): { "title": "Implement Login and Register", "description": "Update task details", "priority": "MEDIUM" }

- DELETE http://localhost:8080/api/tasks/1
  Headers: Authorization: Bearer <YOUR_TOKEN>

- PATCH http://localhost:8080/api/tasks/1/status
  Headers: Authorization: Bearer <YOUR_TOKEN>
  Body (JSON): { "status": "IN_PROGRESS" }

- PATCH http://localhost:8080/api/tasks/1/assign
  Headers: Authorization: Bearer <YOUR_TOKEN>
  Body (JSON): { "userId": 2 }

Comments
- POST http://localhost:8080/api/comments/1
  Headers: Authorization: Bearer <YOUR_TOKEN>
  Body (JSON): { "content": "This task needs a review" }

- GET http://localhost:8080/api/comments/1
  Headers: Authorization: Bearer <YOUR_TOKEN>

- PUT http://localhost:8080/api/comments/1
  Headers: Authorization: Bearer <YOUR_TOKEN>
  Body (JSON): { "content": "Updated comment content" }

- DELETE http://localhost:8080/api/comments/1
  Headers: Authorization: Bearer <YOUR_TOKEN>

Snippets
- POST http://localhost:8080/api/snippets
  Headers: Authorization: Bearer <YOUR_TOKEN>
  Body (JSON):
  {
    "title": "Example Snippet",
    "code": "console.log(\"hello\");",
    "language": "JavaScript",
    "projectId": 1
  }

- GET http://localhost:8080/api/snippets?projectId=1
  Headers: Authorization: Bearer <YOUR_TOKEN>

- POST http://localhost:8080/api/snippets/1/comments
  Headers: Authorization: Bearer <YOUR_TOKEN>
  Body (JSON): { "comment": "Nice snippet!", "lineNumber": 1 }

- GET http://localhost:8080/api/snippets/1/comments
  Headers: Authorization: Bearer <YOUR_TOKEN>

Notifications
- GET http://localhost:8080/api/notifications
  Headers: Authorization: Bearer <YOUR_TOKEN>

- PATCH http://localhost:8080/api/notifications/1
  Headers: Authorization: Bearer <YOUR_TOKEN>

Search
- GET http://localhost:8080/api/search?q=dev
  Headers: Authorization: Bearer <YOUR_TOKEN>
