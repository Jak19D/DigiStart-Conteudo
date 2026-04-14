# Script para instalar RabbitMQ
Write-Host "Instalando Erlang..."
Start-Process -FilePath ".\erlang-installer.exe" -ArgumentList "/S" -Wait

Write-Host "Instalando RabbitMQ..."
Start-Process -FilePath ".\rabbitmq-server.exe" -ArgumentList "/S" -Wait

Write-Host "Configurando variáveis de ambiente..."
$erlangPath = "C:\Program Files\erl-27.1.2\bin"
$rabbitmqPath = "C:\Program Files\RabbitMQ Server\rabbitmq_server-3.12.13\sbin"

[Environment]::SetEnvironmentVariable("ERLANG_HOME", $erlangPath, "Machine")
[Environment]::SetEnvironmentVariable("RABBITMQ_SERVER", $rabbitmqPath, "Machine")

# Adicionar ao PATH
$currentPath = [Environment]::GetEnvironmentVariable("PATH", "Machine")
[Environment]::SetEnvironmentVariable("PATH", "$currentPath;$erlangPath;$rabbitmqPath", "Machine")

Write-Host "Instalação concluída! Reinicie o PowerShell para usar os comandos do RabbitMQ."
