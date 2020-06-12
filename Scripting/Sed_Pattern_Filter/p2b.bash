#!/bin/bash


grep -f commOut.txt install.new | uniq -c | sort > installAmmount.txt
grep -f commOut.txt remove.new | uniq -c | sort > removeAmmout.txt


comm -23 installAmmount.txt removeAmmout.txt> commOut2.txt
sed 's/^.*[0-9] //' commOut2.txt > commOut3.txt

while read line
do

echo "/$line/d"
done < commOut3.txt > newFinalPattern

sed -f newFinalPattern remove.new > remove2.new

while read line 
do 
echo "/$line/d"

done < remove2.new > finalPattern2

sed -f finalPattern2 install.new > p2b.out













rm commOut3.txt
rm commOut2.txt
rm commOut.txt
rm finalPattern
rm finalPattern2
rm installAmmount.txt
rm install.new
rm remove2.new
rm removeAmmout.txt
rm remove.new
rm newFinalPattern

