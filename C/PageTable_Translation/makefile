all: part1.exe part2.exe part3.exe

# make for the executable
part1.exe: part1.o
	gcc -g -o part1 part1.o
        

# Simple suffix rules for the .o
part1.o: part1.c
	gcc -g -c part1.c

part2.exe: part2.o
	gcc -g -o part2 part2.o

part2.o: phypage.c pagetable.c part2.c
	gcc -g -c phypage.c pagetable.c part2.c

part3.exe: part3.o
	gcc -g -o part3 part3.o

part3.o: phypage.c pagetable.c part3.c
	gcc -g -c phypage.c pagetable.c part3.c
