set APP_PATH=C:\Temp\iea-bulkloader
set WAS_CLIENT_DIR=C:\Aplicaciones\IBM\WebSphere\AppClient
set JAVA_HOME=%WAS_CLIENT_DIR%\java
set JAAS=-Djava.security.auth.login.config=%APP_PATH%\lib\jaas.conf.WebSphere
set NAMING=-Djava.naming.factory.initial=com.ibm.websphere.naming.WsnInitialContextFactory
set CHARSET=-Dfile.encoding=ISO-8859-1

rem for remote development environment use following
set EXTDIRS=-Djava.ext.dirs=%WAS_CLIENT_DIR%\java\jre\lib\ext;%WAS_CLIENT_DIR%\lib;%WAS_CLIENT_DIR%\plugins
set CLIENTSAS=-Dcom.ibm.CORBA.ConfigURL=file:%WAS_CLIENT_DIR%\properties\sas.client.props

rem for local development environment use following
rem set EXTDIRS=-Djava.ext.dirs=%WAS_SERVER_DIR%\java\jre\lib\ext;%WAS_SERVER_DIR%\runtimes
rem set CLIENTSAS=-Dcom.ibm.CORBA.ConfigURL=file:%WAS_SERVER_DIR%\profiles\AppSrv01\properties\sas.client.props

set CLASSPATH=%APP_PATH%\lib;%APP_PATH%\lib\sacs-bulkloader.jar;%APP_PATH%\lib\Jace.jar;%APP_PATH%\lib\log4j.jar;%APP_PATH%\lib\javacsv.jar

%JAVA_HOME%\bin\java -Xms1024M -Xmx1024M -cp "%CLASSPATH%" "%CLIENTSAS%" "%JAAS%" "%NAMING%" "%EXTDIRS%" mx.com.sacs.bulkloader.BulkLoaderLauncher 1