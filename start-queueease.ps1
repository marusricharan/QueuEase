Set-Location $PSScriptRoot
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "  Starting QueueEase - Multi-Business Platform" -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Serving Full-Stack Application at: http://localhost:8080" -ForegroundColor Green
Write-Host ""
Start-Process "http://localhost:8080"
java -jar target\queueease-1.0.0.jar
