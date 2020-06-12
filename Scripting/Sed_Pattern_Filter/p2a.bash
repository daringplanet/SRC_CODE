#!/bin/bash

file=dpkg.log
sed -E -f p2aInstalled.sed  $file > install.new
sed -E -f p2aRemoved.sed $file > remove.new
sort install.new -o install.new
sort remove.new -o remove.new
comm -12   install.new remove.new > commOut.txt
while read line
do
	echo "/$line/d"

done < commOut.txt > finalPattern

sed -E -f finalPattern install.new > p2a.out
bash p2b.bash



