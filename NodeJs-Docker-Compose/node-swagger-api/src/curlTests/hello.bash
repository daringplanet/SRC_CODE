#!/bin/bash
echo "Testing GET /hello"

response=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X GET "http://localhost:12020/hello")
http_resCode=$(echo $response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')


reg="\[\{\"message\":\"hello yourself\"\}\]"

#checking message body
if [[ ! "$response" =~ $reg ]]; then
    echo "...Not correct message: $response"
    exit 1
fi;
echo "...Message ok"

#checking response Code
if [ ! $http_resCode == "200" ]; then
    echo "...Error in HTTP request code: $http_resCode"
    exit 1
fi;

echo "...Status code ok"
