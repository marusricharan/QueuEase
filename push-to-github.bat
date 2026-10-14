@echo off
cd /d "%~dp0"
echo ===================================================
echo   Pushing QueueEase to GitHub: marusricharan/QueuEase
echo ===================================================
echo.
echo If prompted, authorize with your GitHub account in the browser.
echo.
git push -u origin main
echo.
if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] Successfully pushed QueueEase to https://github.com/marusricharan/QueuEase !
) else (
    echo [ERROR] Push failed. If authentication failed, you can use a GitHub Personal Access Token (PAT).
)
echo.
pause
