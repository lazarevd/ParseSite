mvn clean package
cp -r Parser/target/{config/,Parser-0.1.0.jar} /root/java/Parser/
java -jar Parser-0.1.0.jar
