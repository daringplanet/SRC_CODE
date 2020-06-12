#!/bin/bash


if [ -d "Email" ]; then 
    rm -r Email;

fi

mkdir Email
sed -E 's/ /,/' p4Customer.txt > gP4Customer.txt
echo "$1" | awk '{split($0,a,"/"); print a[3],a[2],a[1]}' | (read  year day month; awk -v "year=$year" -v "day=$day" -v "month=$month" -v "date=$1"  -f p4.awk  gP4Customer.txt > gSedEmail.bash)
chmod u+x gSedEmail.bash
bash gSedEmail.bash
rm gSedEmail.bash
rm gP4Customer.txt


