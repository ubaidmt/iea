set APP_PATH=C:\Temp\iea-bulkloader
set JAVA_HOME=C:\Aplicaciones\IBM\WebSphere\AppClient\java
set JAAS=-Djava.security.auth.login.config=%APP_PATH%\lib\jaas.conf.WebSphere
set CHARSET=-Dfile.encoding=ISO-8859-1
set CLASSPATH=%APP_PATH%\lib;%APP_PATH%\lib\sacs-bulkloader.jar;%APP_PATH%\lib\Jace.jar;%APP_PATH%\lib\log4j.jar;%APP_PATH%\lib\javacsv.jar;%APP_PATH%\lib\stax-api.jar;%APP_PATH%\lib\xlxpScanner.jar;%APP_PATH%\lib\xlxpScannerUtils.jar

%JAVA_HOME%\bin\java -Xms1024M -Xmx1024M -cp "%CLASSPATH%" "%JAAS%" "%CHARSET%" mx.com.sacs.bulkloader.BulkLoaderLauncher 1