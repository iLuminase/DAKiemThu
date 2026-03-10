# Quick Deploy Script for Windows PowerShell

Write-Host "🚀 Starting Inventory Management System..." -ForegroundColor Cyan
Write-Host ""

# Check if .env exists
if (-not (Test-Path ".env")) {
    Write-Host "⚠️  .env file not found! Creating from template..." -ForegroundColor Yellow
    Copy-Item ".env.example" ".env"
    Write-Host "✅ Created .env file. Please edit it with your CLERK_SECRET_KEY" -ForegroundColor Green
    Write-Host ""
    Write-Host "Edit .env file now? (y/n): " -ForegroundColor Yellow -NoNewline
    $response = Read-Host
    if ($response -eq 'y') {
        notepad .env
        Write-Host "Press Enter after editing .env..." -NoNewline
        Read-Host
    }
}

Write-Host "📦 Building and starting containers..." -ForegroundColor Cyan
docker-compose up -d --build

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Failed to start containers!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "⏳ Waiting for services to be ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

Write-Host ""
Write-Host "🔍 Checking container status..." -ForegroundColor Cyan
docker-compose ps

Write-Host ""
Write-Host "✅ Deployment complete!" -ForegroundColor Green
Write-Host ""
Write-Host "📝 Next steps:" -ForegroundColor Cyan
Write-Host "   1. Create admin user: docker exec -it inventory-mssql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P admin@123 -d InventoryManagement -C"
Write-Host "   2. Run SQL from DEPLOYMENT.md to create roles and admin user"
Write-Host "   3. Test API: curl http://localhost:8080/api/actuator/health"
Write-Host ""
Write-Host "📚 View logs: docker-compose logs -f" -ForegroundColor Cyan
Write-Host "🛑 Stop: docker-compose down" -ForegroundColor Cyan
Write-Host ""
