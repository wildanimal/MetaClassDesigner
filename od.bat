@set classpath=.;bin;org.eclipse.swt.win32.win32.x86_64.jar;

@for /F "tokens=*" %%i in ('dir libs\ /A:-D /B') do @call classpath.bat libs\%%i

java -Dfile.encoding=UTF-8 -Dcatalina.base=%CD% -cp "%classpath%" ui.AppMain2