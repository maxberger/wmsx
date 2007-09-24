#!/bin/sh
echo "Start!"
for j in 0 1 2 3 4 5 6 7 8 9 ; do 
#for j in 0 1 2 3 4 ; do 
for i in 0 1 2 3 4 5 6 7 8 9; do 
sleep 10
echo $j$i
done
done
echo "End!"
exit 0
