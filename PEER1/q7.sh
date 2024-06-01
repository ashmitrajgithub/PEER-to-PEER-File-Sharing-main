#!/bin/sh
if[$# -ne 3];then
    echo "Invalid"
    echo "Enter input in the following format: op1 operator op2"
else
    num1=$1
    num2=$2
    num3=$3

     case num2 in
