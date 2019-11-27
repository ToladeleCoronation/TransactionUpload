#!/bin/bash
mvn install:install-file -Dfile=ojdbc8.jar -DgroupId=com.oracle -DartifactId=ojdbc -Dpackaging=jar -Dversion=8