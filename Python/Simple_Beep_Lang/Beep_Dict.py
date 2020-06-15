#!/usr/bin/python3

import re

#Beep_Dict.py written by William Lippard



# Purpose:
#       To put declared variables from BEEP source code into
#       the varTypeD and varValueD dicts
# Parameters:
#       tokenM - a list of strings from a line of text
#       varTypeD - a dict that contains the dataTypes for var
#       varValueD - a dict that contains the values for var
def declareVar(tokenM, varTypeD, varValueD):

    dataType = tokenM[1]
    var = tokenM[2].upper()
    varTypeD[var] = dataType

    if len(tokenM) > 3:
        value = tokenM[3]
        value = re.sub("\"", "", value)
        varValueD[var] = value


# Purpose:
#       To print the variable, data type and value
#       in sorted order of the variable name
# parameters
#       varTypeD - a dict that contains the dataTypes for var
#       varValueD - a dict that contains the values for var
def printVariables(varTypeD, varValueD):
    print("Variables:")
    print("\tVar".ljust(10), "Type".ljust(10), "Value".ljust(10), sep="" )
    for key in sorted(varTypeD):
        print("\t", key.ljust(10), varTypeD[key].ljust(10), sep="", end="")
        if key in varValueD.keys():
            print(varValueD[key].ljust(10))
        else:
            print()




# Purpose:
#       To print the label and the line number of which
#       the label is on in sorted order of the label name
# parameters
#       labelD - a dict that contains the lineNumbers for labels
def printLabels(labelD):

    print("Labels:")
    print("\tLabel".ljust(15), "Statement".ljust(10) )
    for label in sorted(labelD):
        print("\t", label.ljust(15), labelD[label], sep="" )
