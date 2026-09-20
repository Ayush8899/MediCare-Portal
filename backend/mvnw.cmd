@echo off
setlocal

set "MAVEN_VERSION=3.9.6"
set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\apache-maven-%MAVEN_VERSION%"

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo [INFO] Maven %MAVEN_VERSION% not found locally. Downloading portable Maven...
    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
        "$ErrorActionPreference = 'Stop';" ^
        "$zip = Join-Path $env:TEMP 'apache-maven-%MAVEN_VERSION%-bin.zip';" ^
        "$url = 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip';" ^
        "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12;" ^
        "Write-Host 'Downloading Maven from' $url '...';" ^
        "(New-Object Net.WebClient).DownloadFile($url, $zip);" ^
        "$dest = Join-Path $env:USERPROFILE '.m2\wrapper';" ^
        "if (!(Test-Path $dest)) { New-Item -ItemType Directory -Path $dest -Force | Out-Null };" ^
        "Write-Host 'Extracting to' $dest '...';" ^
        "Expand-Archive -Path $zip -DestinationPath $dest -Force;" ^
        "Remove-Item $zip -Force;" ^
        "Write-Host 'Maven installed successfully to' (Join-Path $dest 'apache-maven-%MAVEN_VERSION%');"
)

"%MAVEN_HOME%\bin\mvn.cmd" %*
