BEGIN{ printf("Name\t     Flight     Seats   Total Cost\n");  total=0; i=0; j=0; name=""; flights[$2];}  

{
    if($1 == "CUST"){
        name=$6;
        i++;
        } else if($1 == "ENDCUST"){
        j++;
        printf("\t\t\tTotal\t %0.2f\n", total);
        total=0;
        } else if(i>j){ 
            
            flights[$2]+=$3;
        printf("%s\t     %s   %d \t %0.2f\n", name, $2, $3, ($3*$4));
        total+=($3*$4);
        }
    
    
}

END{
    printf("Flights       Total Seats");
    for(seats in flights){
            print seats "      " flights[seats];
    
    }
    
    }


