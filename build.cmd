cd /root/java/Build/ParseSite/
git fetch
git pull
mvn clean package
cp -f /root/java/Build/ParseSite/ParserMosfarr/target/ParserMosfarr.jar /root/java/ParserMosfarr/ParserMosfarr.jar
cp -f /root/java/Build/ParseSite/ParserFdsarr/target/ParserFdsarr.jar /root/java/ParserFdsarr/ParserFdsarr.jar
cp -f /root/java/Build/ParseSite/Sender/target/Sender-0.1.0.jar /root/java/Sender/Sender-0.1.0.jar
/usr/bin/java -jar /root/java/Sender/Sender-0.1.0.jar --spring.config.location=/root/java/Sender/config/telegram.properties,/root/java/Sender/config/application.properties
/usr/bin/java -jar /root/java/ParserMosfarr/ParserMosfarr.jar --spring.config.location=/root/java/ParserMosfarr/config/application.properties
/usr/bin/java -jar /root/java/ParserFdsarr/ParserFdsarr.jar --spring.config.location=/root/java/ParserFdsarr/config/application.properties