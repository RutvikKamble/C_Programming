@echo off
setlocal
cd /d "%~dp0"
echo Starting ZAT Asset Management with DDL auto-update enabled...
echo Includes Asset Transfer (Module 5) and all previously implemented modules.
set SPRING_JPA_HIBERNATE_DDL_AUTO=update
call .\mvnw.cmd spring-boot:run
endlocal
