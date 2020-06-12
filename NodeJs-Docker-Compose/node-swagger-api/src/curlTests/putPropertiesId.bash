#!/bin/bash
echo "Testing PUT /properties/:id"


res=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X PUT "http://localhost:12020/properties/12" -H "accept: */*" -H "api-key: cs4783FTW" -H "Content-Type: application/x-www-form-urlencoded" -d "address=123%20Curl%20Test&city=&state=&zip=0989898")

http_resCode=$(echo $res | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

#checks the first rechord then matches up all of them up to the end of the array of json objs.
reg="{\"message\":\"updated\"}"

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

res=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X PUT "http://localhost:12020/properties/1" -H "accept: */*" -H "api-key: cs4783FTW" -H "Content-Type: application/x-www-form-urlencoded" -d "address=123%20Curl%20Test&city=&state=&zip=0989898")
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

res=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X PUT "http://localhost:12020/properties/a" -H "accept: */*" -H "api-key: cs4783FTW" -H "Content-Type: application/x-www-form-urlencoded" -d "address=123%20Curl%20Test&city=&state=&zip=0989898")
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

echo "___Testing Unauthorized user"

res=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X PUT "http://localhost:12020/properties/12" -H "accept: */*" -H "api-key: bad" -H "Content-Type: application/x-www-form-urlencoded" -d "address=123%20Curl%20Test&city=&state=&zip=0989898")
http_resCode=$(echo $res | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

reg="{\"message\":\"Invalid\Wapi-key\"}"

#echo $res
#echo $reg

#checking message body
if [ `echo $res | grep -c -E $reg` -le 0 ]; then
    echo "  ...Not correct message: $res"
    exit 1
fi;
echo "  ...Message ok"

#checking response Code
if [ ! $http_resCode == "401" ]; then
    echo "  ...Error in HTTP request code: $http_resCode"
    exit 1
fi;

echo "  ...Status code ok"
