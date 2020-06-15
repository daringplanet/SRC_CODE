

import re
import sys


#Exception class for too few operands
class TooFewOperands(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(self, *args, **kwargs)

#Exeption class for variables not defined
class VarNotDefined(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(self, *args, **kwargs)
#Exeption Class for Labels Not Defined
class LabelNotDefined(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(self, *args, **kwargs)
#Exepton class for Invalid Expressions
class InvalidExpression(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(self, *args, **kwargs)
#Execption class for Invalid Value Types
class InvalidValueType(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(self, *args, **kwargs)

#Class for an Executor
class Executor:

    #constructor for and Executer
    #lineList is a list of all the texzt lines
    #varTypeD is a Dictonary for variables types
    #varValueD is a Dictonary for variables values
    #labelD is a Dictonary for labels and holds the line the lable corrsponds too
    #currentLine is to keep track of the currentl line the executer is on
    #linesExe is to keep track of all executable lines
    #v is a boolean value if the -v option is on
    #totalLines is a value to keep track of all lines executed
    def __init__(self, lineList, varTypeD, varValueD, labelD, v):
        self.currentLine=0
        self.lineList = lineList
        self.varTypeD = varTypeD
        self.varValueD = varValueD
        self.labelD = labelD
        self.linesExe = 1
        self.v = v
        self.totalLinesExe = 0

    #ExecuteLines will parse through a list of lines that the
    #obj contains and executes the lines
    def executeLines (self):
        self.totalLinesExe += 1 #keeps track of all lines Executed
        if self.linesExe > 5000: #keep from infinate loop
            print("execution ends,", self.totalLinesExe, "lines")
            sys.exit(-1)

        if self.currentLine >= len(self.lineList): #range checking
            return None
        if self.v: #checks to see if the -v option is marked
            print("executing line:", self.currentLine, self.lineList[self.currentLine])

        self.linesExe += 1
        #while loop checking for any blank lines and skipping them
        while self.lineList[self.currentLine] == "":
            self.currentLine += 1
            if self.currentLine >= len(self.lineList):
                return None
        tokenM = self.lineList[self.currentLine].split()
        #checking to see what kind of statement it is
        if tokenM[0] == '#':
            self.linesExe -= 1
            self.currentLine += 1

            return True
        if tokenM[0] == 'VAR':
            self.currentLine += 1

            return True
        if tokenM[0] == 'IF':

            self.compare()
            self.currentLine += 1


            return True
        if tokenM[0] == 'PRINT':

            self.printState()
            self.currentLine += 1

            return True
        if tokenM[0] == 'GOTO':

            self.goTo()
            self.currentLine += 1

            return True
        if tokenM[0] == 'ASSIGN':

            self.assignState()
            self.currentLine += 1

            return True
        if re.match(r'.*:', tokenM[0]):

            self.label()
            self.currentLine += 1

            return True
        return None


    #assignState
    #the purpose of this method is to determine if the assign statemnet is valid and
    #execute the statement and rechord the data
    #lenth is a var to get the length of tokens for bounds checking for
        #a valid assign statement
    #result is a var to get the key for the value that will be updated
    #op is the operator
    #var1 is the first operand
    #var2 is the second operand
    def assignState(self):

        tokens = self.lineList[self.currentLine].split()

        lenth = len(tokens)
        if lenth <3 or lenth == 4 or lenth > 5: #checks bounds, see description
            raise TooFewOperands("*** line=", seelf.lineList[self.currentLine], "***" )
        if lenth == 3: #routine for if it is a 3 length assign statement
            try:
                if tokens[1].upper() in self.varTypeD:
                    if tokens[2].isdecimal():
                        self.varValueD[tokens[1].upper()] = int(tokens[2])

                        return
                    elif tokens[2].upper() in self.varValueD:
                        self.varValueD[tokens[1].upper()] = self.varValueD[tokens[2].upper()]
                        return
            except:
                raise InvalidExpression("*** line=", self.lineList[self.currentLine], "***")


    #routine for if it is a 5 length assign statement
        result = tokens[1].upper()
        op = tokens[2]#see description
        var1 = tokens[3]#see description
        var2 = tokens[4]#see description


        if result in self.varTypeD: #check to see if the result key is valid
            #checking the differnt operators at this level
            if op == "*":

                if var2.isdecimal():
                    self.varValueD[result] = self.varValueD[var1.upper()] * int(var2)
                elif var2.upper() in self.varValueD:
                    self.varValueD[result] = self.varValueD[var1.upper()]  * int(self.varValueD[var2.upper()] )

                else:
                    raise InvalidValueType("Atleast one var is not a number, statement:",  self.lineList[self.currentLine])
            elif op == "+":
                if var1.isdecimal():
                    if var2.isdecimal():
                        self.varValueD[result] = int(var1) + int(var2)
                    elif var2.upper() in self.varValueD:
                        self.varValueD[result] = int(var1) + int(self.varValueD[var2.upper])
                elif var1.upper() in self.varValueD:
                    if var2.upper() in self.varValueD:
                        self.varValueD[result] = int(self.varValueD[var1.upper()]) + int(self.varValueD[var2.upper()])
                    elif var2.isdecimal():
                        self.varValueD[result] = int(self.varValueD[var1.upper()]) + int(var2)

                    else:
                       raise  InvalidValueType("Atleast one var is not a number, statement:",  self.lineList[self.currentLine])
                else:
                    raise InvalidValueType("Atleast one var is not a number, statement:",  self.lineList[self.currentLine])

            elif op == "-":
                if var1.isdecimal():
                    if var2.isdecimal():
                       self.varValueD[result] = int(var1) - int(var2)
                    elif var2.upper() in self.varValueD:
                        self.varValueD[result] = int(var1) - int(self.varValueD[var2.upper()])
                    else:
                        raise InvalidValueType("Atleast one var is not a number, statement:",  self.lineList[self.currentLine])
                elif var1.upper() in self.varValueD and self.varTypeD[var1.upper()] == "int":
                    if var2.upper() in self.varValueD and self.varTypeD[var2.upper()] == "int":
                        self.varValueD[result] = int(self.varValueD[var1.upper()]) - int(self.varValueD[var2.upper()])
                    elif var2.isdecimal():
                        self.varValueD[result] = int(self.varValueD[var1.upper()]) - int(var2)

                    else:
                        raise InvalidValueType("Atleast one var is not a number, statement:",  self.lineList[self.currentLine])
            elif op == "&":
                var1 = var1.upper()
                var2 = var2.upper()
                if self.varTypeD[var1] == "string":
                    if self.varTypeD[var2] == "string":
                        self.varValueD[result] = self.varValueD[var1] + self.varValueD[var2]
                    else:
                        raise InvalidValueType("Atleast one var is not a number, statement:",  self.lineList[self.currentLine])
                else:
                    raise InvalidValueType("Atleast one var is not a number, statement:",  self.lineList[self.currentLine])



            else:
                raise InvalidValueType("Atleast one var is not a number, statement:",  self.lineList[self.currentLine])
            return



    #label
    #handels all labels in the BEEP source code
    #tokens is a list of words from a BEEP statement
    def label(self):

        tokens = self.lineList[self.currentLine].split()
        #if after the label we need to print
        if tokens[1] == "PRINT":
            self.printState()
        elif tokens[1] == "if" or tokens[1] == "IF": #if after the label we have an if condition
            self.compare()
        return





    #goTo
    #handles all go to calles from Beep src code
    #tokens is a list of words from a BEEP statement
    def goTo(self):

        tokens = self.lineList[self.currentLine].split()
        tokens[1] = tokens[1].upper()

        if tokens[1] in self.labelD:

            self.currentLine = self.labelD[tokens[1]] -1
            self.executeLines()
            self.currentLine -= 1

    #compare
    #handels all compare operations
    #line is a line of code form the BEEP src
    #tokens is a list containing words from the line
    #comp is the comparator
    #op1 is the first operand
    #op2 is the second operand
    #label is the new label to jump to if the condition is met
    def compare(self):
        print("Inside compare")
        line  = self.lineList[self.currentLine].upper()
        line = re.sub(".*IF", "", line, 1) #takes out not important information
        line = re.sub(".*if", "", line, 1)
        tokens = line.split()
        print("tokens=", tokens)


        comp = tokens[0] #see description for what variables are
        op1 = tokens[1]
        op2 = tokens[2]
        label = tokens[3].upper()
        print("comp=", comp, "op1", op1, "op2", op2, "label", label)
        #Validating the operands
        if op1.upper() in self.varTypeD and self.varTypeD[op1.upper()] == "int":
            op1 = self.varValueD[op1.upper()]
        elif op1.isdecimal() == None:
            raise InvalidValueType("Atleast one var is not a number, statement:",  self.lineList[self.currentLine])
        if op2.upper() in self.varTypeD and self.varTypeD[op2.upper()] == "int":
            op2 = self.varValueD[op2.upper()]
        elif op2.isdecimal() == None:
            raise InvalidValueType("Atleast one var is not a number, statement:",  self.lineList[self.currentLine])

        try: #try and compare the two operands and got to the new label if the
             #label is defined

            if comp == ">":
                if int(op1) > int(op2):
                    print("Inside If >")
                    self.currentLine = self.labelD[label]-1
                    self.executeLines()
            elif comp == ">=":
                if int(op1) >= int(op2):

                    self.currentLine = self.labelD[label]-1
                    self.executeLines()
                else:

                    self.currentLine += 1
        except: #other wise the label is not defined
            raise LabelNotDefined(label)
        return


    #printState
    #printState prints all print statements in the BEEP src code
    #line contains the current line of of code being executed
    #tokens is a list that contains the words from the line
    #regEx is a regulare experssion to match the literal words to be printed
        #infromation
    def printState(self):

        line = self.lineList[self.currentLine]
        line = re.sub(".*PRINT", "", line, 1)
        tokens = line.split()
        regEx = re.compile(r'\"')

        for token in tokens: #for each token in tokens, loop through the tokens list

            if regEx.match(token): #if tokens is a literal word to print
                insideQ = token.split('"', 3)
                print(insideQ[1], end=" ")
            else: #else try and print the value of the token
                try:
                    print(self.varValueD[token.upper()], end=" ")
                except:
                    raise VarNotDefined("*** line", self.currentLine, "error detected ***")
        print() #make a new line when done printing one line for the BEEP src code
