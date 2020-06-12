#!/bin/bash
read -p "Enter the Email: " email
read -p "Payment Ammount: " payAmmount


if [ -n "$(find ~/CS3423/assignments/1assign/Data -name "$email" -mmin -5)" ]; then
    cd Data
    . pay2.bash<"$email"
    cat > $email << EOF
    $email $name
    $apt $rent $(($balance - $payAmmount))  $date
EOF
      cd ..
else
    echo "Error: No account found"

fi
