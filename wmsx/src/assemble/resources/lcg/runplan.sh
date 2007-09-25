#!/bin/bash

PLAN=$1
STATUSFILE=$PLAN.status
LOG=$PLAN.log
ERRLOG=$PLAN.errlog

if [ -z "$PLAN" ]; then
  echo "Usage: $0 PLANFILE"
  echo
  echo "Where PLANFILE is an execution plan"
  exit 1
fi

if ! [ -f "$STATUSFILE" ]; then
  STATUS=1
  echo $STATUS > $STATUSFILE
else
  STATUS=`cat $STATUSFILE|head -n 1`
fi

if [ $STATUS == "Done" ] ; then
  echo "Task already done."
  exit 0
fi

LASTLINE=`cat $PLAN | wc -l`

EXEC_LINE=`cat $PLAN | head -n $STATUS | tail -n 1` # the STATUS-th line
while [ -n "$EXEC_LINE" ]; do
  if [ $STATUS -gt $LASTLINE ]; then
    echo "Plan finished."
    echo "Done" >$STATUSFILE
    exit 0
  else
    echo "$EXEC_LINE" >>$LOG 2>>$ERRLOG
    echo $STATUS > $STATUSFILE
### Execute command in a checker loop:
    TRIES=0
    RESULT=1
    while (( $RESULT != 0 )) ; do
      $EXEC_LINE
      RESULT=$?
      TRIES=$(($TRIES+1))
      if (( $TRIES >= 5 )) ; then
        echo "The line"
        echo "$EXEC_LINE"
        echo "failed. Giving up after 5 tries!"
        exit 1
      fi
      if (( $RESULT != 0 )) ; then sleep 1m ; fi
    done
### Update execline:
    EXEC_LINE=`cat $PLAN | head -n $((STATUS+1)) | tail -n 1`
    STATUS=$(( $STATUS + 1 ))
  fi
done
