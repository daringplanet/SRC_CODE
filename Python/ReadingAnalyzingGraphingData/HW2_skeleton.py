# -*- coding: utf-8 -*-
"""
Created on Thu Sep 12 09:37:09 2019
HW2 code skeleton
@author: JRuan
Edited by : William Lippard, aju722
"""


"""
@author: William
"""
def yearTotals(data):
    years = list()
    totals = list()
    for i in range(len(data)):  # for each row
        years.append(data[i][0])
        sum = 0
        for j in range(len(data[i])):
            if j == 0:
                continue
            sum += data[i][j]
        totals.append(sum)
    return years, totals


def avgMonthlyCases(dataSet):
    iCount = 0
    avgList = [0]*(len(dataSet[0])-1) #create list and initlize the value to 0 for each month
    for i in range(len(dataSet)):
        iCount+=1
        sum = 0
        iLen = len(dataSet[i])
        for j in range(iLen):
            if j==0:
                continue
            avgList[j-1]+=dataSet[i][j]
    for i in range(len(avgList)):
        avgList[i] = avgList[i] / iCount

    return avgList

import matplotlib.pyplot as plt
import numpy as np

#%% load data
import pandas as pd
measles=pd.read_csv('Measles.csv',header=None).values
mumps=pd.read_csv('Mumps.csv',header=None).values
chickenPox=pd.read_csv('chickenPox.csv',header=None).values


# close all existing floating figures
plt.close('all')

measYears = list()
mesTot = list()
mumpYears = list ()
mumpTot = list()

measYears, mesTot = yearTotals(measles)
mumpYears, mumpTot = yearTotals(mumps)
poxYears, poxTotals = yearTotals(chickenPox)

plt.show()




#%% Q1. plot annual total measles cases in each year
plt.figure()
plt.title('Fig 1: NYC measles cases')
plt.xlabel("Year")
plt.ylabel("Number of cases")
# complete this part
#print(len(mumps))

plt.plot(measYears, mesTot, "b*-")

plt.show()








#%% Q2 plot annual total measels and mumps cases in log scale

plt.figure()
plt.title('Fig 2: Measles and mumps cases in NYC')

# complete this part
plt.plot(measYears, mesTot, "-x", c="blue", label="Measels")
plt.plot(mumpYears, mumpTot, "go:", label="Mumps")
plt.xlabel("Year")
plt.ylabel("Number of cases")

plt.yscale("log")
plt.legend(bbox_to_anchor = (1,1,0,0), ncol=1)

plt.show()



#%% Q3 plot average mumps cases for each month of the year

plt.figure()
plt.title('Fig 3: Average monthly mumps cases')

# complete this part

avgMMumps = avgMonthlyCases(mumps)
#print(avgMMumps)

plt.bar(list(range(len(avgMMumps))), avgMMumps)

plt.ylabel("Average number of cases")
plt.xlabel("Month")
plt.show()








#%% Q4 plot monthly mumps cases against measles cases 
mumpsCases = mumps[:, 1:].reshape(41*12)
measlesCases = measles[:, 1:].reshape(41*12)


plt.figure()
plt.title('Fig 4: Monthly mumps vs measles cases')

# complete this part
plt.scatter(mumpsCases, measlesCases)
plt.xlabel("Number of Mumps cases")
plt.ylabel("Number of Measles cases")
plt.show()








#%% Q5 plot monthly mumps cases against measles cases in log scale
plt.figure()
plt.title('Fig 5: Monthly mumps vs measles cases (log scale)')

# complete this part

plt.scatter(mumpsCases, measlesCases)
plt.xlabel("Number of Mumps cases")
plt.ylabel("Number of Measles cases")
plt.xscale("log")
plt.yscale("log")
plt.show()




#%% Q6 plot annual total chicken pox cases in each year

plt.figure()
plt.title('Fig 6: NYC chicken pox cases')

poxDict = dict()
# complete this part

for i in range(len(poxYears)):
    poxDict.update({poxYears[i] : poxTotals[i]})

iCount = 0
for key in sorted(poxDict.keys()):
    poxTotals[iCount] = poxDict[key]
    iCount+=1

poxYears = sorted(poxYears)


plt.plot(poxYears, poxTotals, "bo-")

plt.xlabel("Year")
plt.ylabel("Number of cases")

plt.show()
