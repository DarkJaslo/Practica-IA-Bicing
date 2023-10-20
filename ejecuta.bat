@echo off
if "%~1"=="" (
    echo "Usage: ejecuta.bat <MainClassName>"
    echo "Especifica que clase quieres ejecutar como main. Ejemplo: ejecuta.bat Tester1"
) else (
    java -cp .;.\aima\AIMA.jar;Bicing\Bicing.jar %~1
)