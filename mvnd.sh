#!/usr/bin/env bash

export JAVA_HOME=/usr/local/jdk-21/;
export JRE_HOME=${JAVA_HOME}/jre;
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib;
export PATH=${JAVA_HOME}/bin:${PATH};
java -version

/usr/local/maven-mvnd-1.0.2-linux-amd64/bin/mvnd clean package -f pom.xml -Dmaven.test.skip=true
if [ $? != 0 ]; then
  echo "打包失败"
  exit 1
fi