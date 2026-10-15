# Postman-style endpoint checks for DevHive
# Run with: powershell -NoProfile -File .\postman_checks.ps1
$ErrorActionPreference = 'Stop'
$base = 'http://localhost:8080'
$t = [int][double]::Parse((Get-Date -UFormat %s))
$user1 = "postman-user-$t@example.com"
$user2 = "postman-user2-$t@example.com"
$orgName = "Postman Org $t"
$projectName = "Postman Project $t"
$snippetTitle = 'Postman Snippet'
$snippetCode = 'console.log("hello");'
$snippetLang = 'JavaScript'
function fail($msg){ Write-Host "ERROR: $msg"; exit 1 }
Write-Host "REGISTER user1"
Invoke-RestMethod -Uri ($base + '/api/auth/register') -Method Post -Body (@{name='Postman One'; email=$user1; password='P@ssw0rd!'} | ConvertTo-Json) -ContentType 'application/json' | Out-Null
Write-Host "LOGIN user1"
$login1 = Invoke-RestMethod -Uri ($base + '/api/auth/login') -Method Post -Body (@{email=$user1; password='P@ssw0rd!'} | ConvertTo-Json) -ContentType 'application/json'
$token1 = $login1.token
Write-Host "TOKEN1: $token1"
if (-not $token1) { fail 'login1 failed' }
Write-Host "REGISTER user2"
Invoke-RestMethod -Uri ($base + '/api/auth/register') -Method Post -Body (@{name='Postman Two'; email=$user2; password='P@ssw0rd!'} | ConvertTo-Json) -ContentType 'application/json' | Out-Null
Write-Host "CREATE ORG"
$org = Invoke-RestMethod -Uri ($base + '/api/orgs') -Method Post -Headers @{Authorization = 'Bearer ' + $token1} -Body (@{name = $orgName} | ConvertTo-Json) -ContentType 'application/json'
$orgId = $org.id
if (-not $orgId) { fail 'create org failed' }
Write-Host "ORG ID: $orgId"
Write-Host "CREATE PROJECT"
$project = Invoke-RestMethod -Uri ($base + '/api/projects') -Method Post -Headers @{Authorization = 'Bearer ' + $token1} -Body (@{name=$projectName; organizationId=[int]$orgId} | ConvertTo-Json) -ContentType 'application/json'
$projectId = $project.id
if (-not $projectId) { fail 'create project failed' }
Write-Host "PROJECT ID: $projectId"
Write-Host "CREATE TASK"
$task = Invoke-RestMethod -Uri ($base + '/api/tasks') -Method Post -Headers @{Authorization = 'Bearer ' + $token1} -Body (@{title='Postman Task'; description='test task'; priority='MEDIUM'; projectId=[int]$projectId} | ConvertTo-Json) -ContentType 'application/json'
$taskId = $task.id
if (-not $taskId) { fail 'create task failed' }
Write-Host "TASK ID: $taskId"
Write-Host "INVITE USER2"
Invoke-RestMethod -Uri ($base + '/api/orgs/' + $orgId + '/invite') -Method Post -Headers @{Authorization = 'Bearer ' + $token1} -Body (@{email=$user2; role='DEVELOPER'} | ConvertTo-Json) -ContentType 'application/json' | Out-Null
Write-Host "GET MEMBERS AFTER INVITE"
$members = Invoke-RestMethod -Uri ($base + '/api/orgs/' + $orgId + '/members') -Method Get -Headers @{Authorization = 'Bearer ' + $token1}
$user1Id = $members[0].userId
$user2Id = ($members | Where-Object { $_.email -eq $user2 }).userId
if (-not $user2Id) { fail 'invite user2 failed' }
Write-Host "USER2 ID: $user2Id"
Write-Host "ADD COMMENT"
$comment = Invoke-RestMethod -Uri ($base + '/api/comments/' + $taskId) -Method Post -Headers @{Authorization = 'Bearer ' + $token1} -Body (@{content='This is a test comment.'} | ConvertTo-Json) -ContentType 'application/json'
$commentId = $comment.id
if (-not $commentId) { fail 'add comment failed' }
Write-Host "COMMENT ID: $commentId"
Write-Host "CREATE SNIPPET"
$snippet = Invoke-RestMethod -Uri ($base + '/api/snippets') -Method Post -Headers @{Authorization = 'Bearer ' + $token1} -Body (@{title=$snippetTitle; code=$snippetCode; language=$snippetLang; projectId=[int]$projectId} | ConvertTo-Json) -ContentType 'application/json'
$snippetId = $snippet.id
if (-not $snippetId) { fail 'create snippet failed' }
Write-Host "SNIPPET ID: $snippetId"
Write-Host "ADD SNIPPET COMMENT"
$snComment = Invoke-RestMethod -Uri ($base + '/api/snippets/' + $snippetId + '/comments') -Method Post -Headers @{Authorization = 'Bearer ' + $token1} -Body (@{comment='Nice snippet!'; lineNumber=1} | ConvertTo-Json) -ContentType 'application/json'
Write-Host "SEARCH"
Invoke-RestMethod -Uri ($base + '/api/search?q=Postman') -Method Get -Headers @{Authorization = 'Bearer ' + $token1} | Out-Null
Write-Host "ASSIGN TASK"
Invoke-RestMethod -Uri ($base + '/api/tasks/' + $taskId + '/assign') -Method Patch -Headers @{Authorization = 'Bearer ' + $token1} -Body (@{userId=[int]$user1Id} | ConvertTo-Json) -ContentType 'application/json' | Out-Null
Write-Host "GET NOTIFICATIONS"
$notifications = Invoke-RestMethod -Uri ($base + '/api/notifications') -Method Get -Headers @{Authorization = 'Bearer ' + $token1}
if ($notifications -and $notifications.Count -gt 0) { $nid = $notifications[0].id; Write-Host "MARK AS READ $nid"; Invoke-RestMethod -Uri ($base + '/api/notifications/' + $nid) -Method Patch -Headers @{Authorization = 'Bearer ' + $token1} | Out-Null } else { Write-Host 'NO NOTIFICATIONS' }
Write-Host "CLEANUP: remove member, delete comment, delete task"
Invoke-RestMethod -Uri ($base + '/api/members/' + $orgId + '/' + $user2Id) -Method Delete -Headers @{Authorization = 'Bearer ' + $token1} | Out-Null
Invoke-RestMethod -Uri ($base + '/api/comments/' + $commentId) -Method Delete -Headers @{Authorization = 'Bearer ' + $token1} | Out-Null
Invoke-RestMethod -Uri ($base + '/api/tasks/' + $taskId) -Method Delete -Headers @{Authorization = 'Bearer ' + $token1} | Out-Null
Write-Host 'ALL CHECKS PASSED'
