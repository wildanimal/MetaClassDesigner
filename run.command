#!/bin/sh

cd `dirname $0`

cp2=./:src/:bin/:libs/:org.eclipse.swt.cocoa.macosx.x86_64.jar

for i in `ls libs/*.jar`
do
cp2=$cp2:$i
done

#echo java -Dcatalina.base=$cp -classpath "${cp2}" -jar ormd.jar

#java -XstartOnFirstThread -jar ORMD_OSX.jar
java -XstartOnFirstThread -Dcatalina.base=$PWD -cp $cp2 ui.AppMain2