#!/bin/sh

if [ $# -ne 2 ]; then
   echo "Usage: $0 filename1 filename2"
else
   if cmp -s "$1" "$2"; then
      echo "Files have the same content"
      rm "$2"
   else
      echo "Files have different content"
   fi
fi

