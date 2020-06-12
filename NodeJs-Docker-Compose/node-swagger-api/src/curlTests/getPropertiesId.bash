#!/bin/bash
echo "Testing GET /properties/:id"

res=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X GET "http://localhost:12020/properties/12")
http_resCode=$(echo $res | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

#checks the first rechord then matches up all of them up to the end of the array of json objs.
reg="{\"id\":[0-9]*,\"address\":\".{1,200}\",\"city\":\".{1,50}\",\"state\":\".{2}\",\"zip\":\"[0-9]{5,10}\"\}"

#echo $reg
#echo $res

echo "___Testing correct id"
#checking message body
if [ `echo $res | grep -c -E $reg` -le 0 ]; then
    echo "  ...Not correct message: $res"
    exit 1
fi;
echo "  ...Message ok"

#checking response Code
if [ ! $http_resCode == "200" ]; then
    echo "  ...Error in HTTP request code: $http_resCode"
    exit 1
fi;

echo "  ...Status code ok"


echo "___Testing incorrect id"

res=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X GET "http://localhost:12020/properties/1")
http_resCode=$(echo $res | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

reg="{\"message\":\"Error\WNot\Wfound\"}"

#echo $res
#echo $reg

#checking message body
if [ `echo $res | grep -c -E $reg` -le 0 ]; then
    echo "  ...Not correct message: $res"
    exit 1
fi;
echo "  ...Message ok"

#checking response Code
if [ ! $http_resCode == "404" ]; then
    echo "  ...Error in HTTP request code: $http_resCode"
    exit 1
fi;

echo "  ...Status code ok"




echo "___Testing invalid id"

res=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X GET "http://localhost:12020/properties/a")
http_resCode=$(echo $res | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

reg="{\"message\":\"Invalid\Winteger\"}"

#echo $res
#echo $reg

#checking message body
if [ `echo $res | grep -c -E $reg` -le 0 ]; then
    echo "  ...Not correct message: $res"
    exit 1
fi;
echo "  ...Message ok"

#checking response Code
if [ ! $http_resCode == "400" ]; then
    echo "  ...Error in HTTP request code: $http_resCode"
    exit 1
fi;

echo "  ...Status code ok"
