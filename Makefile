all: bin/SayItAssistantServer.class bin/SayItAssistantClient.class

bin/SayItAssistantServer.class: src/server/*.java src/common/*.java
	javac -cp lib/*:bin -d bin src/common/*.java src/server/*.java

bin/SayItAssistantClient.class: src/client/*.java src/common/*.java
	javac -cp lib/*:bin -d bin src/common/*.java src/client/*.java

server: bin/SayItAssistantServer.class 
	java -cp lib/*:bin SayItAssistantServer

client: bin/SayItAssistantClient.class
	java -cp lib/*:bin SayItAssistantClient

bin/SayItAssistantTest.class: bin/SayItAssistantServer.class bin/SayItAssistantClient.class test/*.java
	javac -cp lib/*:bin -d bin test/*.java

test: bin/SayItAssistantTest.class
	java -cp lib/*:bin org.junit.platform.console.ConsoleLauncher -c SayItAssistantTest --reports-dir=reports

check:
	java -jar lib/checkstyle-10.10.0-all.jar -c checkstyle.xml src/*

clean:
	rm -f bin/*
