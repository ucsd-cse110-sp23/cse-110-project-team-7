all: bin/SayItAssistant.class

bin/SayItAssistant.class: src/SayItAssistant.java
	javac -cp lib/*:bin -d bin src/SayItAssistant.java

run: bin/SayItAssistant.class
	java -cp lib/*:bin SayItAssistant

test: bin/SayItAssistant.class
	@echo Not yet implemented

clean:
	rm -f bin/SayItAssistant.class
