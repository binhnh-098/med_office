param(
    [string]$MysqlExe = "",
    [string]$HostName = "127.0.0.1",
    [int]$Port = 3306,
    [string]$RootUser = "root",
    [string]$SchemaPath = ""
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($SchemaPath)) {
    $SchemaPath = Join-Path $PSScriptRoot "..\src\main\resources\schema-mysql.sql"
}

$SchemaPath = (Resolve-Path -LiteralPath $SchemaPath).Path

if ([string]::IsNullOrWhiteSpace($MysqlExe)) {
    $candidates = @(
        "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
        "C:\Program Files\MySQL\MySQL Workbench 8.0\mysql.exe"
    )

    $MysqlExe = $candidates | Where-Object { Test-Path -LiteralPath $_ } | Select-Object -First 1

    if ([string]::IsNullOrWhiteSpace($MysqlExe)) {
        $command = Get-Command mysql.exe -ErrorAction SilentlyContinue
        if ($command) {
            $MysqlExe = $command.Source
        }
    }
}

if ([string]::IsNullOrWhiteSpace($MysqlExe) -or -not (Test-Path -LiteralPath $MysqlExe)) {
    throw "mysql.exe was not found. Pass -MysqlExe with the full path to mysql.exe."
}

$securePassword = Read-Host "MySQL password for $RootUser@$HostName" -AsSecureString
$passwordPtr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)

try {
    $plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPtr)
    $env:MYSQL_PWD = $plainPassword

    Get-Content -LiteralPath $SchemaPath -Raw |
        & $MysqlExe "--host=$HostName" "--port=$Port" "--user=$RootUser" "--default-character-set=utf8mb4"

    if ($LASTEXITCODE -ne 0) {
        throw "Failed to apply schema-mysql.sql."
    }

    $envMap = @{}
    Get-Content (Join-Path $PSScriptRoot "..\.env") | ForEach-Object {
        if ($_ -match '^\s*([^#][^=]+?)\s*=\s*(.*)\s*$') {
            $envMap[$matches[1].Trim()] = $matches[2].Trim()
        }
    }

    $dbUser = [string]$envMap["MED_OFFICE_DB_USERNAME"]
    $dbPassword = [string]$envMap["MED_OFFICE_DB_PASSWORD"]

    if (-not [string]::IsNullOrWhiteSpace($dbUser)) {
        $env:MYSQL_PWD = $dbPassword
        & $MysqlExe "--host=$HostName" "--port=$Port" "--user=$dbUser" "--database=med_office" "--execute=SELECT DATABASE() AS database_name;"

        if ($LASTEXITCODE -ne 0) {
            throw "The application database user from .env could not connect."
        }
    }

    Write-Host "MySQL dev database is ready."
}
finally {
    Remove-Item Env:\MYSQL_PWD -ErrorAction SilentlyContinue
    if ($passwordPtr -ne [IntPtr]::Zero) {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPtr)
    }
}
