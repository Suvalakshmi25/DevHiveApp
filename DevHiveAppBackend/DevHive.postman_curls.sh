#!/bin/bash
BASE="http://localhost:8080"
TOKEN="<YOUR_TOKEN>"

# Auth
curl -s -X POST "$BASE/api/auth/register" -H "Content-Type: application/json" -d '{"name":"Test User","email":"test@example.com","password":"Password123!"}'

curl -s -X POST "$BASE/api/auth/login" -H "Content-Type: application/json" -d '{"email":"test@example.com","password":"Password123!"}'

# Organizations
curl -s -X POST "$BASE/api/orgs" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{"name":"DevHive Org"}'
curl -s -X GET "$BASE/api/orgs" -H "Authorization: Bearer $TOKEN"
curl -s -X POST "$BASE/api/orgs/1/invite" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{"email":"invitee@example.com","role":"DEVELOPER"}'
curl -s -X GET "$BASE/api/orgs/1/members" -H "Authorization: Bearer $TOKEN"

# Membership
curl -s -X PUT "$BASE/api/members/1/2" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{"role":"VIEWER"}'
curl -s -X DELETE "$BASE/api/members/1/2" -H "Authorization: Bearer $TOKEN"

# Projects
curl -s -X POST "$BASE/api/projects" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{"name":"First Project","organizationId":1}'
curl -s -X GET "$BASE/api/projects?orgId=1" -H "Authorization: Bearer $TOKEN"
curl -s -X GET "$BASE/api/projects/1" -H "Authorization: Bearer $TOKEN"
curl -s -X PUT "$BASE/api/projects/1" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{"name":"Updated Project Name"}'
curl -s -X DELETE "$BASE/api/projects/1" -H "Authorization: Bearer $TOKEN"

# Tasks
curl -s -X POST "$BASE/api/tasks" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{"title":"Implement Login","description":"Build auth endpoints and JWT handling","priority":"HIGH","projectId":1}'
curl -s -X GET "$BASE/api/tasks?projectId=1" -H "Authorization: Bearer $TOKEN"
curl -s -X PUT "$BASE/api/tasks/1" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{"title":"Implement Login and Register","description":"Update task details","priority":"MEDIUM"}'
curl -s -X DELETE "$BASE/api/tasks/1" -H "Authorization: Bearer $TOKEN"
curl -s -X PATCH "$BASE/api/tasks/1/status" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{"status":"IN_PROGRESS"}'
curl -s -X PATCH "$BASE/api/tasks/1/assign" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{"userId":2}'

# Comments
curl -s -X POST "$BASE/api/comments/1" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{"content":"This task needs a review"}'
curl -s -X GET "$BASE/api/comments/1" -H "Authorization: Bearer $TOKEN"
curl -s -X PUT "$BASE/api/comments/1" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{"content":"Updated comment content"}'
curl -s -X DELETE "$BASE/api/comments/1" -H "Authorization: Bearer $TOKEN"

# Snippets
curl -s -X POST "$BASE/api/snippets" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{"title":"Example Snippet","code":"console.log(\"hello\");","language":"JavaScript","projectId":1}'
curl -s -X GET "$BASE/api/snippets?projectId=1" -H "Authorization: Bearer $TOKEN"
curl -s -X POST "$BASE/api/snippets/1/comments" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{"comment":"Nice snippet!","lineNumber":1}'
curl -s -X GET "$BASE/api/snippets/1/comments" -H "Authorization: Bearer $TOKEN"

# Notifications
curl -s -X GET "$BASE/api/notifications" -H "Authorization: Bearer $TOKEN"
curl -s -X PATCH "$BASE/api/notifications/1" -H "Authorization: Bearer $TOKEN"

# Search
curl -s -X GET "$BASE/api/search?q=dev" -H "Authorization: Bearer $TOKEN"
