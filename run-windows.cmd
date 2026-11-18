@echo off
cd /d "%~dp0"
where java >nul 2>&1 || (echo Install Java 17 or newer first. & pause & exit /b 1)
if not exist "target\printcompare-0.1.0.jar" (
  where mvn >nul 2>&1 || (echo Install Apache Maven first. & pause & exit /b 1)
  call mvn clean verify
  if errorlevel 1 (pause & exit /b 1)
)
java -jar "target\printcompare-0.1.0.jar"
if errorlevel 1 pause
