# PowerShell script to test API endpoints using saved token

$BASE_URL = "http://localhost:8080"
$TOKEN_FILE = ".\token.txt"

# Function to load token from file
function Get-Token {
    if (Test-Path $TOKEN_FILE) {
        return Get-Content $TOKEN_FILE
    } else {
        Write-Host "❌ Token file not found. Run save_token.ps1 first." -ForegroundColor Red
        exit 1
    }
}

# Function to test endpoint
function Test-Endpoint {
    param(
        [string]$Method,
        [string]$Endpoint,
        [string]$Body
    )
    
    $token = Get-Token
    $url = "$BASE_URL$Endpoint"
    
    Write-Host "Testing: $Method $Endpoint" -ForegroundColor Cyan
    
    $headers = @{
        "Content-Type" = "application/json"
        "Authorization" = "Bearer $token"
    }
    
    if ($Body) {
        $response = curl -i -X $Method $url -H @headers -d $Body
    } else {
        $response = curl -i -X $Method $url -H @headers
    }
    
    Write-Host $response
    Write-Host ""
}

# Examples
Write-Host "DevHive API Test Script" -ForegroundColor Green
Write-Host "======================" -ForegroundColor Green
Write-Host ""

# Test GET Organizations
Test-Endpoint -Method "GET" -Endpoint "/api/orgs"

# Test POST Organization
# Test-Endpoint -Method "POST" -Endpoint "/api/orgs" -Body '{ "name":"Test Org" }'

# Test GET Tasks
# Test-Endpoint -Method "GET" -Endpoint "/api/tasks?projectId=1"
