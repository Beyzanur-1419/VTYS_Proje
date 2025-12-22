@echo off
echo Opening Port 3000 for NodeJS...
netsh advfirewall firewall add rule name="NodeJS Port 3000" dir=in action=allow protocol=TCP localport=3000
echo.
echo Rule added. Press any key to exit.
pause
