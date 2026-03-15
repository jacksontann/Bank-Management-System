# Bank Management System - Run Script
# Usage: .\run.ps1

# Set JAVA_HOME if not already set (required for Maven wrapper)
if (-not $env:JAVA_HOME) {
    $env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2"
}
& "$PSScriptRoot\mvnw.cmd" exec:java
