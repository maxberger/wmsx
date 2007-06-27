#!/bin/sh


PROGRAM=$1
PARAMS="$2"
OUTDIRS="$3"
PROFILE="$4"
AFS="$5"

if [ "$AFS" != "" ] ; then
  if ! [[ -e /afs/kfki.hu && -d /afs/kfki.hu ]] ; then 
    echo "No AFS!"
    sleep 1m
    exit 1
  fi
fi
if ( which g++ 2>&1 | grep "/usr/bin/which: no g++ in " >&/dev/null ) ; then echo "No g++!" ; sleep 1m ; exit 1 ; fi
if ! [[ -e /usr/X11R6/lib && -d /usr/X11R6/lib ]] ; then echo "No libX11.a!" ; sleep 1m ; exit 1 ; fi

echo "Sourcing profile ..."
[ "$PROFILE" != "" ] && [ -e $PROFILE ] && . $PROFILE 
export TMPDIR=$PWD

echo "Extracting $PROGRAM.tar.gz ..."
tar -xzf $PROGRAM.tar.gz
rm -f $PROGRAM.tar.gz
echo "Changing directory to $PROGRAM ..."
cd $PROGRAM

PROGRAMSORIG=`cd src ; ls *.c *.cc *.cpp *.cxx *.c++ *.C *.f *.F 2>/dev/null ; cd ..`
PROGRAMS=""
for P in $PROGRAMSORIG ; do
 NFIELDS=$((`echo -n $P | tr -c -d '.' | wc -c`))
 PROGRAMS="$PROGRAMS `echo -n $P | cut --delimiter='.' --fields=1-$NFIELDS`"
done

echo "Cleaning up source ..."
make -s Clean

for P in $PROGRAMS ; do
 echo "Compiling $P ..."
 make -s bin/$P
 if (( $? != 0 )) ; then echo -e "job.sh:\tCompilation of $P failed!" ; cd .. ; sleep 1m ; exit 1 ; fi
done

echo "Removing old outputs ..."
for F in $OUTDIRS ; do
 rm -f $F/*
done

for P in $PROGRAMS ; do
 echo "Starting $P $PARAMS ..."
 ./bin/$P $PARAMS
 if (( $? != 0 )) ; then echo -e "job.sh:\tRunning of $P $PARAMS failed!" ; cd .. ; sleep 1m ; exit 1 ; fi
done

echo "Changing directory to original directory ..."
cd ..
echo "Archiving output to out.tar.gz ..."
mv $PROGRAM/out .
tar -czf out.tar.gz out
rm -rf out
echo "Cleaning up ..."
rm -rf $PROGRAM
