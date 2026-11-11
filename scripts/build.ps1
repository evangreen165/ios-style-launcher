param(
    [string]$Task = "assembleDebug",
    [string]$JavaHome = "C:\Android\toolchain\jdk17",
    [string]$AndroidHome = "C:\Android\toolchain\sdk"
)
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$Gradlew = Join-Path $Root "gradlew.bat"
if (-not (Test-Path -LiteralPath $Gradlew)) {
    throw "gradlew.bat not found in $Root. Run 'gradle wrapper --gradle-version 8.9' first."
}
$env:JAVA_HOME = $JavaHome
$env:ANDROID_HOME = $AndroidHome
& $Gradlew $Task
exit $LASTEXITCODE
