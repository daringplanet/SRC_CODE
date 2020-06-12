#!/usr/bin/perl -w
use strict;

open(INFILE, "<", "p5Customer.txt") or die "Could not open INFILE: #!\n";

my $email;
my $fullName;
my $title;
my $paid;
my $owe;
my $firstName;
my $lastName;

my $emailDir="./Email";

my $date = $ARGV[0] or die ("No date passed in");


if( -d $emailDir ){
    `rm -r Email`;
    `mkdir Email`;
} else {
    `mkdir Email`;
}



while(my $line = <INFILE>){
    
    chomp $line;    
    ($email, $fullName, $title, $paid, $owe) = split /,/, $line;
    if($paid < $owe){
        
        ($firstName, $lastName) = split / /, $fullName;
        
        open(TEMP, "<", "template.txt");
        
        open(EMAIL, ">", "./Email/$email");
            
            while(my $tempLine = <TEMP>){
                
                        
                    $tempLine =~ s/ADDRESS/$email/g;
                    $tempLine =~ s/DATE/$date/g;
                    $tempLine =~ s/FULLNAME/$fullName/g;
                    $tempLine =~ s/TITLE/$title/g;
                    $tempLine =~ s/AMOUNT/$owe/g;
                    $tempLine =~ s/NAME/$lastName/g; 

                    print EMAIL "$tempLine ";
                
                }
            
            close(EMAIL);
            close(TEMP);
        }

    }

    close(INFILE);


