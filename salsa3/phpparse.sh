#!/bin/bash

PHPSCRIPT=${1}
JAVA=java
PARSERCLASS=com.tuneit.salsa3.PHPParser

if [ "x${PHPSCRIPT}" = "x" ] ; then
	echo "Specify a PHP script"
	exit 1
fi

$JAVA -classpath org.json.jar:bin/ $PARSERCLASS $PHPSCRIPT