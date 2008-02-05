if [ "$AFS" != "" ] ; then
  if ! [[ -e /afs/kfki.hu && -d /afs/kfki.hu ]] ; then
    echo Even though AFS was specified in the requirements, 
    echo AFS is not present on $(hostname)
    echo Please consider filing a bug against this site.
    exit 1
  fi
fi

if [ "$SOFTWARE" != "" ] ; then
  for PIECE in $SOFTWARE ; do
    echo -n Looking for ${PIECE}...
    if ( which $PIECE 2>&1 > /dev/null ) ; then
      echo found
    else
      echo failed
      echo $PIECE is not present on $(hostname)
      echo Please consider filing a bug against this site.
      exit 1
    fi
  done
fi

echo "Extracting ${ARCHIVE}..."
tar -xzf $ARCHIVE
rm -f $ARCHIVE
echo "Changing into $PROGRAMDIRECTORY ..."
cd $PROGRAMDIRECTORY
echo "Running $EXECUTABLE $ARGUMENTS"
PATH=.:$PATH
$EXECUTABLE $ARGUMENTS

echo "Changing back to super directory..."
cd ..
echo "Archiving $PROGGRAMDIRECTORY/$OUTPUTDIRECTORY to out.tar.gz ..."
mv $PROGRAMDIRECTORY/$OUTPUTDIRECTORY .
tar -czf out.tar.gz $OUTPUTDIRECTORY
rm -rf $OUTPUTDIRECTORY
echo "Cleaning up ..."
rm -rf $PROGRAMDIRECTORY
