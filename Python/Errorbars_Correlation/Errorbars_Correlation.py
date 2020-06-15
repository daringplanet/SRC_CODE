# -*- coding: utf-8 -*-
"""

@author: William Lippard


"""

#%% import necessary modules

import numpy as np
import pandas as pd
from matplotlib.pyplot import *




#%% load the data and setup some variables

data = pd.read_csv('diseases.csv',header=None).values
data = data.reshape([3, 41, 12]) #data[0] is for measles, etc.
diseases = ['Measles', 'Mumps', 'ChickenPox']
year = np.arange(1931, 1972)
month = np.arange(1, 13)

#%% Q1 calucate and show average number of cases per year, and 95% CI of the average

figure()
avgs = list()

sumYears = data.sum(axis=2)
avgYears = sumYears.mean(axis=1)

stdYears = sumYears.std(axis=1)
SEM = stdYears / len(year) ** 0.5

errorbar(diseases, avgYears, SEM*1.96, fmt='o', capsize=6)

xlabel('NYC diseases')
ylabel('Number of cases')
ylim([0, 25000])
title('Fig 1 Average annual disease cases (95% CI)')
show()

#%% Q2 calucate and show fraction of cases occurred in each month

figure()


c = data.sum(axis=1, dtype=float)
total = c.sum(axis=1)
c[0,:] = c[0, :] / total[0]
c[1,:] = c[1, :] / total[1]
c[2,:] = c[2, :] / total[2]


#print(c)
plot(range(1,13), c[0], "-b", label="Measles")
plot(range(1,13), c[1], "-", color="orange", label="Mumps")
plot(range(1,13), c[2], "-", color="green", label="ChickenPox")

xlabel('Month')
ylabel('Fraction of cases')
title('Fig 2. Fraction of cases in each month')
legend()
show()


#%% Q3.1 scatter plot avg monthly mumps cases vs chicken pox cases
figure()


avgData = data.sum(axis=1, dtype=float)
total = avgData.sum(axis=1)
avgData[0,:] = avgData[0, :] / 41
avgData[1,:] = avgData[1, :] / 41
avgData[2,:] = avgData[2, :] / 41

scatter(avgData[1, :], avgData[2, :])

xlabel('Avg monthly cases of ' + diseases[1])
ylabel('Avg monthly cases of ' + diseases[2])
title('Fig 3.1 monthly cases of mumps vs chickpen pox')
show()




#%% Q3.2 scatter plot total annual mumps cases vs chicken pox cases
figure()

yearAvgs = data.sum(axis=2)
scatter(yearAvgs[1, :], yearAvgs[2, :])

xlabel('Total annual cases of ' + diseases[1])
ylabel('Total annual cases of ' + diseases[2])
title('Fig 3.2 annual cases of mumps vs chicken pox')
show()




#%% Q4.1, 4.2 calculate Pearson correlation coefficient
cor1 = np.corrcoef(avgData[1, :], avgData[2, :])
#replace 0 with variable name
print('Pearson correlation between avg monthly mumps cases and monthly Chicken Pox cases is ', cor1[0,1])

cor2 = np.corrcoef(yearAvgs[1, :], yearAvgs[2, :])
#replace 0 with variable name
print('Pearson correlation between annual mumps cases and annual Chicken Pox cases is ', cor2[0,1])

#%% Q4.3, 4.4 calculate the Spearman correlation coefficient
mmRank = np.argsort(np.argsort(avgData[1, :]))
cmRank = np.argsort(np.argsort(avgData[2, :]))
cor3 = np.corrcoef(mmRank, cmRank)

#replace 0 with variable name
print('Spearman correlation between monthly mumps cases and monthly Chicken Pox cases is ', cor3[0,1])


myRank = np.argsort(np.argsort(yearAvgs[1, :]))
cyRank = np.argsort(np.argsort(yearAvgs[2, :]))
cor4 = np.corrcoef(myRank, cyRank)
#replace 0 with variable name
print('Spearman correlation between annual mumps cases and annual Chicken Pox cases is ', cor4[0,1])


#%% Q5 calculate and show correlation between number of mumps cases in each month
figure()

# replace cc with your calculated value
cc = np.random.rand(12,12)
mumps = data[1]

for i in range(len(month)): #for each month
   for j in range(len(month)): #for each month
       #find the corrolation between all the years month by month in a 12x12 matrix
       cc[i][j] = np.corrcoef(mumps[:, i], mumps[:, j])[0][1]

imshow(cc)
title('Fig 4 Correlation between monthly mumps cases')
xticks(range(12), range(1,13))
yticks(range(12), range(1,13))
xlabel('Month')
ylabel('Month')
show()




#%% Q6 calculate and show average fraction of diseases occurring in different months
figure()
mesF = np.zeros((41, 12))
mupF = np.zeros((41, 12))
chiF = np.zeros((41, 12))
#print(mesF.shape)
for i in range(len(year)): #for each year
   mesYT = data[0, i].sum()  #get the total for each year for each desise.
   mupYT = data[1, i].sum()
   chiYT = data[2, i].sum()
   for j in range(len(month)): #for each month
       #calculate the percentage of case for that month within that year.
       mesF[i, j] = float(data[0, i, j]) / float(mesYT)
       mupF[i, j] = float(data[1, i, j]) / float(mupYT)
       chiF[i, j] = float(data[2, i, j]) / float(chiYT)

   mesMean = mesF.mean(axis=0) #calculate the mean for each column
   mupMean = mupF.mean(axis=0)
   chiMean = chiF.mean(axis=0)

#print(mesMean.shape)
plot(mesMean, 'b-', label="Measles")
plot(mupMean, '-', color='orange', label="Mumps")
plot(chiMean, 'g-', label="ChickenPox")
xlabel('Month')
ylabel('Fraction of cases')
title('Fig 5 Avg fraction of cases')
legend()
show()





#%% c1a
answer = '...'
print('My answer to challenge c1a: ', answer)

#%% c1b show one line of code below to support c1a
print('My one line code to challenge c1b: ......')

# insert the line below to be execued

#%% c2
answer = '...'
print ('My answer to challenge c2:', answer)

# optional code to support your answer
