@echo off
setlocal

REM Store the machine architecture in a variable
if "%PROCESSOR_ARCHITECTURE%"=="AMD64" (
    set "arch=x86_64-windows"
) else (
    echo Unsupported architecture
    exit /b 1
)

REM Setup Visual Studio compiler if it is not already on the PATH
SET "VS2022_File=C:\Program Files\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvarsall.bat"
SET "VS2019_File=C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\VC\Auxiliary\Build\vcvarsall.bat"
WHERE cl >nul 2>nul
IF ERRORLEVEL 1 (
    IF EXIST "%VS2022_File%" (
        CALL "%VS2022_File%" x64
        GOTO Compile
    )
    IF EXIST "%VS2019_File%" (
        CALL "%VS2019_File%" x64
        GOTO Compile
    )
    echo Warning: Both vcvarsall.bat files for Visual Studio 2022 and 2019 do not exist.
)

:Compile

REM Compile and run the program if the architecture is supported
cl /I "target\gluonfx\%arch%\gvm\HelloSharedLib" /EHsc sample/example.cpp /link "target\gluonfx\%arch%\HelloSharedLib.lib" /out:"target\gluonfx\%arch%\example.exe"

"target\gluonfx\%arch%\example.exe" 1 2

endlocal
