CFLAGS = gcc -g
OBJ = xdir.o errExit.o

# Automatic substitution using suffix rules to
# make .o files from either .c or .cpp files
%.o: %.c
	${CFLAGS} -c $< 

# Build xdir based on the required .o files
xdir: ${OBJ}
	${CFLAGS} -o xdir ${OBJ}

clean:
	rm ${OBJ}
