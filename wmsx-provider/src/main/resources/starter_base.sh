#!/bin/sh

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

echo "Extracting $ARCHIVE.tar.gz..."
tar -xzf $ARCHIVE.tar.gz
rm -f $ARCHIVE.tar.gz
echo "Changing into $PROGDIR ..."
cd $PROGDIR
echo "Running $PROGRAM..."
PATH=.:$PATH
$PROGRAM $PARAMS

echo "Changing back to super directory..."
cd ..
echo "Archiving $PROGDIR/$OUTDIR to out.tar.gz ..."
mv $PROGDIR/$OUTDIR .
tar -czf out.tar.gz $OUTDIR
rm -rf $OUTDIR
echo "Cleaning up ..."
rm -rf $PROGDIR
