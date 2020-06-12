all: prog

# make for the executable
prog: prog.o
	gcc -g -o prog prog.o

# Simple suffix rules for the .o
prog.o: LinkedList.h prog.c
	gcc -g -c LinkedList.h prog.c



