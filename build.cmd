cd /root/java/Build/ParseSite/
git fetch
git pull
mvn clean package
cp -f /root/java/Build/ParseSite/Parser/target/Parser-0.1.0.jar /root/java/Parser/Parser-0.1.0.jar
cp -f /root/java/Build/ParseSite/Sender/target/Sender-0.1.0.jar /root/java/Sender/Sender-0.1.0.jar
cd /root/java/Sender/
java -jar Sender-0.1.0.jar
cd /root/java/Parser/
java -jar Parser-0.1.0.jar