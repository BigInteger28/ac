# a makefile is pretty stupid at this point, but typing 'make' is easy

ALL:
	mkdir -p bin
	javac -g -d bin `find . -name "*.java"`
	jar cfe ac.jar frontend.Main -C bin .
