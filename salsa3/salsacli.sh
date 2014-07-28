#!/bin/bash

JAVA=java
JAVAOPTS="-Xmx1024m -Djava.util.logging.config.file=src/log-config.properties 
		  -Dorg.springframework.shell.core.JLineShell.enableJLineLogging=false"

CLICLASS=com.tuneit.salsa3.SalsaCLI

JARS=
for JAR in lib/*.jar
do
	JARS=${JARS}${JAR}:
done

$JAVA -classpath ${JARS}bin/ $JAVAOPTS $CLICLASS $@ 