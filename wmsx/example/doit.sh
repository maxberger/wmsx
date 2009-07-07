old=$(cat counter)
new=$(($old +1))
echo $new > counter
exit 0
