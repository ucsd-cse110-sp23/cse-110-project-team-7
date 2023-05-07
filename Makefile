all: bin/SayItAssistant.class

bin/SayItAssistant.class: src/*.java
	javac -cp lib/*:bin -d bin src/*.java

run: bin/SayItAssistant.class
	java -cp lib/*:bin SayItAssistant

bin/SayItAssistantTest.class: bin/SayItAssistant.class test/*.java
	javac -cp lib/*:bin -d bin test/*.java

test: bin/SayItAssistantTest.class
	java -cp lib/*:bin org.junit.platform.console.ConsoleLauncher -c SayItAssistantTest

check:
	java -jar lib/checkstyle-10.10.0-all.jar -c checkstyle.xml src/*

clean:
	rm -f bin/*
