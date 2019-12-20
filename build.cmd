cd /root/java/Build/ParseSite/
git fetch
git pull
mvn clean package
cp -r Parser/target/{config/,Parser-0.1.0.jar} /root/java/Parser/
cp -r Sender/target/{config/,Sender-0.1.0.jar} /root/java/Sender/
cd /root/java/Parser/
java -jar Parser-0.1.0.jar