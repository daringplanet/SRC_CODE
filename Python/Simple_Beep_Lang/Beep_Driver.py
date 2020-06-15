#!/usr/bin/python3

#Beep_Driver.py, Written by William Lippard

# Purpose:
#       The Dirver file reads in the BEEP source code file,
#       puts all the lines of the src code into a list,
#       puts all the vars into dicts with their data type
#       being in one dict and their value in another,
#       puts the labels and theri line numbers into dict,
#       prints the source code with the line numbers,
#       and prints out the vars and labels ....

import sys
import re
from Beep_Dict import declareVar, printVariables, printLabels
from Executor import *


sysLen = len(sys.argv)
fileName = None
    #check to see if we have a file
if sysLen < 2 : #defaults to Beep_Input
    print("warrning: Correct fileName  needed as command argument. Usage: python3 p6Driver.py fileNameName.txt")
    #sys.exit(1)
    print("No file given. Defaulting to 'Beep_Input.txt")
    fileName = open("Beep_Input.txt", "r")
else:
    #opens the file to read
    fileName = open(sys.argv[1], "r")

#some list, dicts, and vars

lineList = [] #saves each line of the file
varTypeD = {} #saves the dataType with the var name being the key
varValueD = {} #saves the value with the var name being the key
labelD = {} #saves the line number with the label in uppercase being the key

lineNumber=1 #count the lines in the file releative to 1
dateType = "" 
var = ""
value = ""
label = ""
global vSwitch
vSwitch = None
if sysLen == 3 and sys.argv[2] == "-v":
    vSwitch = True




print("BEEP source code in ", fileName, ":", sep="")

#while there is more lines in the file
while True:
    inputLine = fileName.readline() #read the line in
    if inputLine == "": #check to see if there are no more lines
        break
    inputLine = inputLine.rstrip('\n')   #removes the newline
    lineList.append(inputLine) #add the line from the file in the lineList
    print("", lineNumber, inputLine ) #print the line with the lineNumber

    tokenM = inputLine.split() #default delimiter is ' '
    if len(tokenM) > 0: #makes sure we have tokens 
        if tokenM[0] == "VAR": #if the token is a var declration
           declareVar(tokenM, varTypeD, varValueD) #add it into the varTypeD and varValueD dicts
        if re.search(r':', tokenM[0]): #if the token is a label
            label = re.sub(":", "", tokenM[0]) #remove the :
            label = label.upper() #make it upper case
            if label in labelD.keys(): #check to see if it alread exist in the labelD dict
                print("***Error: label '", label, "' appears on multiple lines: ", labelD[label], " and ", lineNumber, sep="" )
            else:
                labelD[label.upper()] = lineNumber #create a new entry for labelD 
    lineNumber+=1



fileName.close()

printVariables(varTypeD, varValueD) #prints the variables and their values

printLabels(labelD) #prints the labels and their lineNumbers
exe = Executor(lineList, varTypeD, varValueD, labelD, vSwitch)
executing = True
print("execution begins ...")
while executing:
    executing = exe.executeLines()
    
#lines = exe.getExeLines()
print("execution ends", exe.linesExe, "lines executed")

