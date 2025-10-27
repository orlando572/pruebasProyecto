# Script para actualizar todos los servicios para usar axiosInstance con JWT

$services = @(
    "src\service\admin\AdminUsuarioService.jsx",
    "src\service\user\ChatBotService.jsx",
    "src\service\user\ComparadorService.jsx",
    "src\service\user\FinancieroService.jsx",
    "src\service\user\PensionesService.jsx",
    "src\service\user\PerfilService.jsx",
    "src\service\user\SegurosService.jsx"
)

foreach ($service in $services) {
    $filePath = Join-Path $PSScriptRoot $service
    
    if (Test-Path $filePath) {
        Write-Host "Actualizando: $service"
        
        $content = Get-Content $filePath -Raw
        
        # Reemplazar import de axios
        $content = $content -replace "import axios from 'axios';", "import axiosInstance from '../../config/axiosConfig';"
        
        # Reemplazar URL completa por ruta relativa
        $content = $content -replace 'const API_URL = "http://localhost:8090/api/', 'const API_URL = "/'
        
        # Reemplazar todas las llamadas axios. por axiosInstance.
        $content = $content -replace '\baxios\.', 'axiosInstance.'
        
        Set-Content $filePath -Value $content -NoNewline
        
        Write-Host "✓ Actualizado: $service" -ForegroundColor Green
    } else {
        Write-Host "✗ No encontrado: $service" -ForegroundColor Red
    }
}

Write-Host "`n¡Actualización completada!" -ForegroundColor Cyan
