BEGIN{FS=","; array[$2];}

{
    if ($6 > $5){
        array[$2] = $1;
        
        email=$1
        fullName=$2+$3
        name=$3;
        title=$4
        paid=$5
        owe=$6
       print "sed -E -e 's/address/" email "/' -e 's/date/"month "\\\\\\/" day "\\\\\\/" year "/' -e 's/title/" title "/' -e 's/fullname/" fullName "/' -e 's/name/" name "/' -e 's/amount/" owe "/' sedTemplate.sed > g" email ".sed\n"
       printf("sed -E -f g%s.sed template.txt > ./Email/%s\n", email, email);


        }
}
END{
    for (person in array){
        if(person != null)
        print "rm g" array[person] ".sed\n"
        }
    }
