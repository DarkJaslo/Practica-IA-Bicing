@echo off
setlocal enabledelayedexpansion

set count=0
for %%x in (%*) do set /a count+=1

if %count% equ 0 (
    echo "Usage: ejecuta.bat <MainClassName>"
    echo "Especifica que clase quieres ejecutar como main. Ejemplo: ejecuta.bat Tester1"
) else if %count% equ 9 (
    java -Xmx8G -cp .;.\aima\AIMA.jar;Bicing\Bicing.jar %~1 %~1 %~2 %~3 %~4 %~5 %~6 %~7 %~8 %~9
) else (
    java -Xmx8G -cp .;.\aima\AIMA.jar;Bicing\Bicing.jar %~1
)

endlocal