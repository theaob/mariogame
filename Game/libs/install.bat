@echo off
mvn install:install-file -Dfile=jbox2d-library.jar -DgroupId=org.jbox2d -DartifactId=jbox2d-library -Dversion=2.2.1.1 -Dpackaging=jar
