#!/bin/bash

# Keep you last sync date here and update after a merge
# 2020-04-28

# This script finds '*.js', '*.css', '*.json' and '*.html' files in Arkcase core/foia project that are overriden by an extension and have changed after some date.
# To check for updated extension files in core files from the last checked date, from your extension project execute (second line needed if it's foia extension):
#   $ ./check-ui-updates.sh $pathToArkcaseCode/acm-standard-applications/acm-law-enforcement 2020-04-28
#   $ ./check-ui-updates.sh $pathToArkcaseCode/acm-standard-applications/acm-foia 2020-04-28

# The script can be also used to check foia extension against core code. From Arkcase/acm-standard-application/acm-foia directory execute:
#   $ ./check-ui-updates.sh ../acm-law-enforcement 2020-04-28

comparePath=$1
lastSyncDate=$(date -d $2 +%s)

echo "Files changed since $2:"
echo "-----------------------"

for i in $(find . -type f \( -iname \*.js -o -iname \*.css -o -iname \*.json -o -iname \*.html \) -not -path "./target/*"); do
  filename="$(basename -- $i)"
  foundFile=$(find $comparePath -type f -name "$filename" -not -path "$comparePath/target/*"  | head -n 1)
  
  if [ ! -z "$foundFile" ] 
  then
    extensionFile=${i#*directives}
    coreFile=${foundFile#*directives}
    
    foundMatch=""
    if [ $extensionFile = $coreFile ]
    then
      foundMatch=true
    else
      extensionFile=${i#*modules}
      coreFile=${foundFile#*modules}
      if [ $extensionFile = $coreFile ]
      then
        foundMatch=true
      else
        extensionFile=${i#*config}
        coreFile=${foundFile#*config}
        if [ $extensionFile = $coreFile ]
        then
          foundMatch=true
        else
          extensionFile=${i#*services}
          coreFile=${foundFile#*services}
          if [ $extensionFile = $coreFile ]
          then
            foundMatch=true
          else
            extensionFile=${i#*assets}
            coreFile=${foundFile#*assets}
            if [ $extensionFile = $coreFile ]
            then
              foundMatch=true
            fi
          fi
        fi
      fi
    fi
    
    if [ $foundMatch ]
    then
      #echo "Checking for changes: $i"
      foundFileRelativePath=${foundFile#*$comparePath/}
      cd $comparePath
      lastChangedDate=$(git log -1 --pretty="format:%at" $foundFileRelativePath)
      cd - > /dev/null
      if [ $lastChangedDate -gt $lastSyncDate ]
      then
        echo $i
      fi
    fi
  fi
done
