#!/bin/bash

# picture naming convention seperated by _
# los verion
# category / view
# device codename
# blur/noblur
# index: number counting up from 1 to be increased when multiple pictures have the same name

cd images
echo "[" > pictures.json
for PICTURE in $(ls *.png *.jpg); do
  echo "  \"$PICTURE\"," >> pictures.json
done
echo "]" >> pictures.json
cd ..
mv -f images/pictures.json pictures.json
sed -i 'x;${s/,$//;p;x;};1d' pictures.json
