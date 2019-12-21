cd /root/java/Build/ParseSite/
git fetch
git pull
mvn clean package
cp -f /root/java/Build/ParseSite/Parser/target/Parser-0.1.0.jar /root/java/Parser/Parser-0.1.0.jar
cp -f /root/java/Build/ParseSite/Sender/target/Sender-0.1.0.jar /root/java/Sender/Sender-0.1.0.jar
/usr/bin/java -jar /root/java/Sender/Sender-0.1.0.jar --spring.config.location=/root/java/Sender/config/telegram.properties,/root/java/Sender/config/application.properties
/usr/bin/java -jar /root/java/Parser/Parser-0.1.0.jar --spring.config.location=/root/java/Parser/config/application.properties
