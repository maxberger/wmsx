#!/bin/sh

#PROGRAM=$1
#PARAMS="$2"
#OUTDIR="$3"
#AFS="$5"

if [ "$AFS" != "" ] ; then
  if ! [[ -e /afs/kfki.hu && -d /afs/kfki.hu ]] ; then
    echo Even though AFS was specified in the requirements, 
    echo AFS is not present on $(hostname)
    echo Please consider filing a bug against this site.
    exit 1
  fi
fi
if ( which g++ 2>&1 > /dev/null ) ; then
  echo -n
else
  echo g++ is not present on $(hostname)
  echo Please consider filing a bug against this site.
  exit 1
fi

echo "Extracting $PROGRAM.tar.gz..."
tar -xzf $PROGRAM.tar.gz
rm -f $PROGRAM.tar.gz
echo "Changing into $PROGRAM ..."
cd $PROGRAM
echo "Running $PROGRAM..."
./$PROGRAM $PARAMS

echo "Changing back to super directory..."
cd ..
echo "Archiving output to out.tar.gz ..."
mv $PROGRAM/$OUTDIR .
tar -czf out.tar.gz out
rm -rf $OUTDIR
echo "Cleaning up ..."
rm -rf $PROGRAM
