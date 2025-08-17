@echo off
echo Running tests...
call mvn clean test
echo.
echo Generating Allure report...
call mvn allure:serve
start http://localhost:8080
