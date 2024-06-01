#!/bin/sh

cu=$USER

if [ "$cu" = "student" ]; then
    echo "Hello"
else
    echo "Bye"
fi

