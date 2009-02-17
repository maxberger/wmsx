#!/bin/bash

SOURCE=$1
TARGET=$SOURCE
DEST=$2
SE=$DEST
VO=$3

if [ -z "$VO" ]; then
  echo "Usage: $0 TARGET SE VO"
  echo
  echo "Where TARGET is an LFC namespace item, "
  echo "SE is an SE or the word 'all', VO is your VO."
  exit 1
fi

if [ $SE == "all" ] ; then SE="-a" ; else SE="-s $SE" ; fi

# slashes... replace them with backslash-slashes
HALFSLASHED_DEST=$( echo -n $DEST| sed -e "s/\//\\\\\//g" )
SLASHED_DEST=$( echo -n $HALFSLASHED_DEST| sed -e "s/\./\\\\\./g" )
HALFSLASHED_SOURCE=$( echo -n $SOURCE| sed -e "s/\//\\\\\//g" )
SLASHED_SOURCE=$( echo -n $HALFSLASHED_SOURCE| sed -e "s/\./\\\\\./g" )

export IFS=$'\n' # newline separates only

for i in `lfc-ls -lR $SOURCE `; do # per-line run recursive listing on source
  if [ "`echo -n $i|sed -e \"s/^\/.*$/\//\"`" = "/" ] # if the line starts with a / then
    then
    j=`echo -n $i|cut -d ':' -f 1` # get the first part until the :
    MYLASTDIR=$j
    MYDESTDIR=`echo -n $j|sed -e "s/$SLASHED_SOURCE/$SLASHED_DEST/"` # replace source with the destination dir
#    echo "mkdir $MYDESTDIR"
  else
    n=$((`echo -n $i|tr -c -d ' '|wc -c` + 1 )) # count spaces in input
    if [ "`echo -n $i|sed -e \"s/^d.*$/d/\"`" = "d" ] # if directory in filelist
      then
      /bin/true # do nothing
      else
        if [ "$n" -ne 1 ] # if more than one field present (not neccessary?)
        then
        j=`echo -n $i|cut -d ' ' -f $n` # that's the filename
        echo "lcg-del $SE --vo $VO lfn:$MYLASTDIR/$j" # copy it to wherever it would go
      fi
    fi
  fi
done

if [ $SE == "-a" ] && ( lfc-ls $TARGET >&/dev/null ) ; then echo "lfc-rm -r $TARGET" ; fi
