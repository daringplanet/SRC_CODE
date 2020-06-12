#!/bin/bash
bash startService.bash &

sleep 3

#echo "--------------------------------------"
bash hello.bash
if [ ! $? -eq 0 ]; then
    echo "Failed on /hello"
    kill $(ps | grep node | awk '{print $1}')
    exit 1
fi;
echo ""
echo "--------------------------------------"

bash getProperties.bash
if [ ! $? -eq 0 ]; then
    echo "Failed on /getProperties"
    kill $(ps | grep node | awk '{print $1}')
    exit 1
fi;


echo ""
echo "--------------------------------------"
bash getPropertiesId.bash
if [ ! $? -eq 0 ]; then
    echo "Failed on /getPropertiesId"
    kill $(ps | grep node | awk '{print $1}')
    exit 1
fi;
echo ""
echo "--------------------------------------"
bash putPropertiesId.bash
if [ ! $? -eq 0 ]; then
    echo "Failed on /putPropertiesId"
    kill $(ps | grep node | awk '{print $1}')
    exit 1
fi;
echo ""
echo "--------------------------------------"
bash postProperties.bash
if [ ! $? -eq 0 ]; then
    echo "Failed on /postProperties"
    kill $(ps | grep node | awk '{print $1}')
    exit 1
fi;
echo ""
echo "--------------------------------------"
bash deletePropertiesId.bash
if [ ! $? -eq 0 ]; then
    echo "Failed on /deletePropertiesId"
    kill $(ps | grep node | awk '{print $1}')
    exit 1
fi;

echo "All Test were Successful"
echo "Ending all processes"

kill $(ps | grep node | awk '{print $1}')
