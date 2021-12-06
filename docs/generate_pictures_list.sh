#!/bin/bash

# picture naming convention seperated by _
# los version
# category / view
# device codename
# blur/noblur
# theme: dark/light
# index: number counting up from 1 to be increased when multiple pictures have the same name

# blur and theme can be undefined if this property is irrelevant

cd images
echo "[" > pictures.json
for PICTURE in $(ls *.png *.jpg); do
  echo "  \"$PICTURE\"," >> pictures.json
done
echo "]" >> pictures.json
cd ..
mv -f images/pictures.json pictures.json
sed -i 'x;${s/,$//;p;x;};1d' pictures.json
