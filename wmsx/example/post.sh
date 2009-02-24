cd $2
current=$(cat counter)
echo $current
[ $current -lt 3 ] && exit 1
exit 0
