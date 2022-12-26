:: This script assumes that Songs of Syx is installed in the default Steam library location.
:: If you have installed Songs of Syx in a different location, you will need to edit the paths below.

:: Set the path to the Songs of Syx installation directory.
SET SONGS_OF_SYX_PATH=C:\Program Files (x86)\Steam\steamapps\common\Songs of Syx

@echo off

:: Copy the base folder
xcopy /E /I /Y "%SONGS_OF_SYX_PATH%\base" "%cd%\base"

:: Copy the cache folder
xcopy /E /I /Y "%SONGS_OF_SYX_PATH%\cache" "%cd%\cache"

:: Copy the errorLogs folder
xcopy /E /I /Y "%SONGS_OF_SYX_PATH%\errorLogs" "%cd%\errorLogs"

:: Copy the SongsOfSyx.jar file
xcopy /Y "%SONGS_OF_SYX_PATH%\SongsOfSyx.jar" "%cd%\SongsOfSyx.jar"

@echo on

REM Make sure to copy the Porcupine Framework JAR to the root directory of this project!