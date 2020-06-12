# Define the machine object files for your program
OBJECTS = aju722Project6.o 
# Define your include file
INCLUDES = Queue.h Graph.h aju722Project6.c

# make for the executable
p6: ${OBJECTS}
	gcc -g -o p6 ${OBJECTS}

# Simple suffix rules for the .o
%.o: %.c ${INCLUDES}
	gcc -g -c -O3 $<

# Clean the .o files
clean:
	rm -f ${OBJECTS}
