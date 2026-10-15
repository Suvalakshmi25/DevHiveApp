# PowerShell script to login and save token to a file

$BASE_URL = "http://localhost:8080"
$TOKEN_FILE = ".\token.txt"

# Login and get token
Write-Host "Logging in..." -ForegroundColor Cyan
$response = curl -s -X POST "$BASE_URL/api/auth/login" `
  -H "Content-Type: application/json" `
  -d '{"email":"test@example.com","password":"Password123!"}'

# Parse JSON response
$tokenData = $response | ConvertFrom-Json
$token = $tokenData.token

if ($token) {
    # Save token to file
    Set-Content -Path $TOKEN_FILE -Value $token
    Write-Host "✅ Token saved to $TOKEN_FILE" -ForegroundColor Green
    Write-Host "Token (first 50 chars): $($token.Substring(0, 50))..." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "To use this token in a request:" -ForegroundColor Cyan
    Write-Host "  `$token = Get-Content .\token.txt" -ForegroundColor White
    Write-Host "  curl -H `"Authorization: Bearer `$token`" http://localhost:8080/api/orgs" -ForegroundColor White
} else {
    Write-Host "❌ Failed to get token. Response: $response" -ForegroundColor Red
}
