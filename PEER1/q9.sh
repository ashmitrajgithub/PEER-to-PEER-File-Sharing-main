#!/bin/sh
read -p "Enter a day in Capital Letter" day

case "$day" in
"MONDAY") echo "DOS Class is on";;
"TUESDAY") echo "DOS CLASS IS ON";;
"WEDNESDAY") echo "DOS WED";;
*)echo "no";;
esac

