#!/bin/bash
menu=0
while [ $menu ]; do
    echo "Enter one of the follwing actons or CTRL-D to exit"
    echo "C -create a customer file"
    echo "P -accept a customer payment"
    echo "F -find customer by apartment number"
    if ! read userInput; then
            # got EOF
            break
    fi
    case "$userInput" in 
    C|c) bash create
	    ;;
    P|p) bash payment.bash
	    ;;
    F|f) bash findFile
	    ;;
    esac
done



