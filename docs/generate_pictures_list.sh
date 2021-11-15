#!/bin/bash
cd images
echo "[" > pictures.json
for PICTURE in $(ls *.png *.jpg); do
  echo "  \"$PICTURE\"," >> pictures.json
done
echo "]" >> pictures.json
cd ..
mv -f images/pictures.json pictures.json
sed -i 'x;${s/,$//;p;x;};1d' pictures.json
