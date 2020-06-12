#!/bin/bash
echo "Testing POST /properties"


res=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X POST "http://localhost:12020/properties" -H "accept: */*" -H "api-key: cs4783FTW" -H "Content-Type: application/x-www-form-urlencoded" -d "address=123%20Wonder%20dr&city=Wow&state=WW&zip=3693699")

http_resCode=$(echo $res | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

#checks the first rechord then matches up all of them up to the end of the array of json objs.
reg="{\"message\":\"added\"}"

#echo $reg
#echo $res

echo "___Testing correct post"
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




echo "___Testing incorrect values"

res=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X POST "http://localhost:12020/properties" -H "accept: */*" -H "api-key: cs4783FTW" -H "Content-Type: application/x-www-form-urlencoded" -d "address=&city=&state=aaa&zip=121855154444156")

http_resCode=$(echo $res | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

reg="\[{\"message\":\"address\Wis\Wnot\Wbetween\W1\Wand\W200\Wcharacters\"},{\"message\":\"city\Wis\Wnot\Wbetween\W1\Wand\W50\Wcharacters\"},{\"message\":\"State\Wis\Wnot\W2\Wcharacters\"},{\"message\":\"zip\Wis\Wnot\Wbetween\W5\Wand\W10\Wcharacters\"}\]"

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

res=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X POST "http://localhost:12020/properties" -H "accept: */*" -H "api-key: bad" -H "Content-Type: application/x-www-form-urlencoded" -d "address=&city=&state=aaa&zip=121855154444156")
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
