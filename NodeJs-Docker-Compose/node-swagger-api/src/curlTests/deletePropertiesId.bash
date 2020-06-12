#!/bin/bash
echo "Testing DELETE /properties/:id"


#finds the highest id starting from 120 to delete
res=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X GET "http://localhost:12020/properties" -H "accept: */*")
#echo $res
id=500
echo "...Looking for highest id."
while [ $id -ge 13 ]
do
    reg="{\"id\":$id"
 #   echo $id
 #   echo $reg
    if [ ! `echo $res | grep -c -E $reg` -le 0 ]; then
	echo "...Using $id id for the delete test"
        break
    fi
    id=$(($id - 1))
done

#echo $id

res=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X DELETE "http://localhost:12020/properties/$id" -H "accept: */*" -H "api-key: cs4783FTW")

http_resCode=$(echo $res | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

#checks the first rechord then matches up all of them up to the end of the array of json objs.
reg="{\"message\":\"deleted\"}"

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

res=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X DELETE "http://localhost:12020/properties/$id" -H "accept: */*" -H "api-key: cs4783FTW")
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

res=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X DELETE "http://localhost:12020/properties/a" -H "accept: */*" -H "api-key: cs4783FTW")
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

res=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X DELETE "http://localhost:12020/properties/$id" -H "accept: */*" -H "api-key: bad")
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
