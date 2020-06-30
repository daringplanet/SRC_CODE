# -*- coding: utf-8 -*-
"""
# UTSA CS 3793 Artificial Intelligence
Fall 2019
---

**Team Project**

As a team, you will work to build a machine learning model to classify images.


Data is provided for training and testing of your model in **cs3793data.zip**. It contains an undisclosed number of images grouped by their assigned label/category.


Your team may build any machine learning model to accomplish this task. To evaluate your model, analyze how many images it is able to correctly classify during testing.


*Any libraries are permitted, for all aspects of this project.
However, all code must be your own - code from outside sources is not permitted.*

---


# Data

Replace this text with a brief explanation of what data preparation and analysis your team needed to perform.


Be sure split your data into training and testing tests.


In analysis, ensure you know the answers the following questions…


*   How many images are there?
*   How many classes are there?
*   How many samples are there per class?
"""

import keras
from keras.utils import to_categorical
from sklearn.preprocessing import LabelBinarizer
from keras.preprocessing.image import ImageDataGenerator, array_to_img, img_to_array, load_img
from keras.models import Sequential
from keras.layers import Conv2D, MaxPooling2D
from keras.layers import Activation, Dropout, Flatten, Dense
from sklearn.model_selection import train_test_split
from keras.preprocessing.image import ImageDataGenerator
from keras.optimizers import adam
from PIL import Image
import numpy as np
import pandas as pd
from google.colab import drive
from zipfile import ZipFile
from google.colab import drive
from google.colab import files
import os
import matplotlib.pyplot as plt

drive.mount('/content/drive/')

data_path = "/content/drive/My Drive/AI_FinalProject/cs3793data.zip"

# READ IN CATEGORY FILES
categories = list() #list of all category names
pictures = dict() #dict with keys of categories and values are list of paths to pictures in category
x = list()

# Check our given path if the file exists, then extract its contents
if not os.path.isdir(data_path):
  zf = ZipFile(data_path, mode='r')
  zf.extractall(pwd='password'.encode('ascii'))
  zf.close()
y = list()

# using the current path with the extracted data
data_path = './cs3793data'
for f in os.listdir(data_path):
    if not f == '.DS_Store':
      categories.append(f)
      path = data_path+"/"+f
      pics = list()
      for pic in os.listdir(path):
        if pic == '.DS_Store':
          continue
        i = path+"/"+pic
        pics.append(i)
        x.append(i)
        y.append(f)
      pictures[f] = pics.copy()

# Analysis
print('Total images:',len(x))
print('Total amount of classes:', len(categories))
for img in pictures:
  print(img, ":", len(pictures[img]))

img_x = 128
img_y = 128
x = np.array(x)

datagen = ImageDataGenerator(
        rotation_range=40,
        width_shift_range=0.2,
        height_shift_range=0.2,
        rescale=1./255,
        shear_range=0.2,
        zoom_range=0.2,
        horizontal_flip=True,
        fill_mode='nearest')

img_array = list()
for img in x:
  tmp = load_img(img, color_mode='rgb', target_size=(img_x,img_y))
  foo = img_to_array(tmp)
  img_array.append(foo)

img_array = np.array(img_array)
print(img_array.shape)

x_train, x_test, y_train, y_test = train_test_split(img_array, y, test_size=0.33, random_state=42, shuffle=True)
encoder = LabelBinarizer()

# convert class vectors to binary class matrices
y_train = encoder.fit_transform(y_train)
y_test = encoder.fit_transform(y_test)
print('categorical example: ', y_train[0])

"""---


# Model

We are choosing a sequential convolution 2D network, which allows us to maximize our classification accuracy of images while using max pooling and data augmentation to prevent overfitting.
"""

model = Sequential()

# Inital LAYER
model.add(Conv2D(32, kernel_size=(3, 3), strides=(1,1), activation='relu', input_shape = (img_x, img_y, 3)))
model.add(MaxPooling2D(pool_size=(2,2)))

# 1st hidden LAYER
model.add(Conv2D(32,3,3))
model.add(Activation('relu'))
model.add(MaxPooling2D(pool_size=(2,2)))

# 2 hidden LAYER
model.add(Conv2D(64,3,3))
model.add(Activation('relu'))
model.add(MaxPooling2D(pool_size=(2,2)))

#output layer
# preparing DROP OUT LAYER - used to prevent overfitting
model.add(Flatten()) # flatten feature map to one dimension
model.add(Dense(64)) # initialize a fully connected layer
model.add(Activation('relu')) # apply relu to it
model.add(Dropout(0.5)) # DROPOUT!!!! (drops out a random set of activations in this layer by setting them to 0 as data flows through)
model.add(Dense(53)) # initialize one more full connected layer this will return an N-dimensional vector (N number of classes)
model.add(Activation('softmax')) # converts everything to probabiliities for each class

# LEARN TIME
# now we need to minimize a loss function WHICH measures difference between expected and target output
# model.compile(loss='categorical_crossentropy', optimizer='rmsprop',metrics=['accuracy'])
model.compile(loss='binary_crossentropy', optimizer=keras.optimizers.Adamax(lr=0.001, beta_1=0.9, beta_2=0.999),
              metrics=['accuracy'])

# TRAINING TIME
hist = model.fit(x_train, y_train,epochs=3,verbose=1)

"""---


# Evaluation

We chose the accuracy matrix because it just worked the best for our model. We tried other performance matrices but they didn't perform as well.
"""

e = model.evaluate(x_test, y_test)
print("Test Loss:", e[0])
print("Test Accuracy:", e[1])

"""---


# Graphs
"""

hist2 = model.predict(x_test,verbose=1)
print(hist2.shape)

corCount = [0]*53
inCount = [0]*53
for i in range(len(y_test)):
  for j in range(53):
    print(np.max(hist2[i]))

# Graphs to represent our data
plt.plot(hist.history['acc'],hist.history['loss'])
plt.title('Accuracy vs Loss over epochs')
plt.xlabel('Accuracy')
plt.ylabel('Loss')
plt.savefig('fig1.png')

plt.show()

model.summary()

allV = list()
cat = list()
vals = list()

plt.figure(figsize=(20,20))
plt.title('# of Images per Category')
plt.xlabel('Category Name')
plt.ylabel('# of Images')

for key in pictures:
  allV.append((key, len(pictures[key])))

print(allV)
allV.sort(key= lambda x: x[1])
print(allV)

for c, v in allV:
  cat.append(c)
  vals.append(v)

for i in range(len(cat)):
  plt.bar(cat[i], vals[i],width=1)

trash = plt.xticks(rotation='90')
plt.savefig('fig-44')
