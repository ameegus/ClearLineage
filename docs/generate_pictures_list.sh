#!/bin/bash
echo "[" > pictures.json
for PICTURE in $(ls *.png *.jpg); do
  echo "  \"$PICTURE\"," >> pictures.json
done
echo "]" >> pictures.json
sed -i 'x;${s/,$//;p;x;};1d' pictures.json
