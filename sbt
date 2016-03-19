#! /usr/bin/bash

java -Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M -jar ./sbt-launch.jar "$@"

if [[ $1 == "clean" ]]; then
   rm -rf project/target
   rm -rf project/project
fi
