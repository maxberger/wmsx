#!/bin/bash

SOURCE=$1
DEST=$2
SE=$3
VO=$4

if [ -z "$VO" ]; then
  echo "Usage: $0 SOURCE DEST SE VO"
  echo
  echo "Where SOURCE is a local one file/directory,"
  echo "DEST is an LFC namespace item, SE is an SE, VO is your VO."
  exit 1
fi

# slashes... replace them with backslash-slashes
HALFSLASHED_DEST=$( echo -n $DEST| sed -e "s/\//\\\\\//g" )
SLASHED_DEST=$( echo -n $HALFSLASHED_DEST| sed -e "s/\./\\\\\./g" )
HALFSLASHED_SOURCE=$( echo -n $SOURCE| sed -e "s/\//\\\\\//g" )
SLASHED_SOURCE=$( echo -n $HALFSLASHED_SOURCE| sed -e "s/\./\\\\\./g" )

export IFS=$'\n' # newline separates only

FIRST=true
for i in `ls -lR $SOURCE `; do # per-line run recursive listing on source
  FIRSTWORD=`echo -n $i | cut -d ' ' -f 1`
  if [ $FIRSTWORD == "total" ] ; then continue ; fi
  if [ "`echo -n $i|sed -e \"s/^\/.*$/\//\"`" = "/" ] # if the line starts with a / then
    then
    j=`echo -n $i|cut -d ':' -f 1` # get the first part until the :
    MYLASTDIR=$j
    MYDESTDIR=`echo -n $j|sed -e "s/$SLASHED_SOURCE/$SLASHED_DEST/"` # replace source with the destination dir
    echo "lfc-mkdir  $MYDESTDIR"
  else
    n=$((`echo -n $i|tr -c -d ' '|wc -c` + 1 )) # count spaces in input
    if [ "`echo -n $i|sed -e \"s/^d.*$/d/\"`" = "d" ] # if directory in filelist
      then
      /bin/true # do nothing
      else
        if [ "$n" -ne 1 ] # if more than one field present (not neccessary?)
        then
        j=`echo -n $i|cut -d ' ' -f $n` # that's the filename
        echo "lcg-cr -d $SE --vo $VO file:$MYLASTDIR/$j -l lfn:$MYDESTDIR/$j" # copy it to wherever it would go
      fi
    fi
  fi
done

