all: bin/SayItAssistantServer.class bin/SayItAssistantClient.class

CLASSPATH = lib/json-20230227.jar:lib/slf4j-api-2.0.7.jar:lib/slf4j-simple-2.0.7.jar:lib/mongodb-driver-core-4.10.0-alpha0.jar:lib/mongodb-driver-sync-4.10.0-alpha0.jar:lib/bson-4.10.0-alpha0.jar:bin
TESTCLASSPATH = $(CLASSPATH):lib/junit-platform-console-standalone-1.9.2.jar
CHECKSTYLE = lib/checkstyle-10.10.0-all.jar

ENV = MONGO_URI="mongodb+srv://alrussell:tuPxiYrr4MKZCZJY@cluster0.jevkxgs.mongodb.net/?retryWrites=true&w=majority" OPENAI_TOKEN="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"
MOCK_ENV = MONGO_URI="mongodb://dummy" OPENAI_TOKEN="fake_token"

bin/SayItAssistantClient.class: src/client/*.java src/common/*.java
	javac -cp $(CLASSPATH) -d bin src/common/*.java src/client/*.java

bin/SayItAssistantServer.class: bin/SayItAssistantClient.class src/server/*.java src/common/*.java src/mock/*.java
	javac -cp $(CLASSPATH) -d bin src/mock/*.java src/server/*.java

server: bin/SayItAssistantServer.class 
	$(ENV) java -cp $(CLASSPATH) SayItAssistantServer

client: bin/SayItAssistantClient.class
	java -cp $(CLASSPATH) SayItAssistantClient

bin/SayItAssistantMS2Test.class: bin/SayItAssistantServer.class test/*.java
	javac -cp $(TESTCLASSPATH) -d bin test/*.java

test: bin/SayItAssistantMS2Test.class
	$(MOCK_ENV) java -cp $(TESTCLASSPATH) org.junit.platform.console.ConsoleLauncher -c SayItAssistantMS1Test -c SayItAssistantMS2Test --reports-dir=reports

check:
	java -jar $(CHECKSTYLE) -c checkstyle.xml src/*

clean:
	rm -f bin/*class
