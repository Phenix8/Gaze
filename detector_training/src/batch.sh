#!/usr/bin/env bash

index=3
prefix=anamorphosis_
suffixSmall=_s.png
suffixLarge=_l.png

for file in $1/*.png
do
	convert "$file" -resize 142 "$prefix$index$suffixLarge"
	convert "$prefix$index$suffixLarge" -resize 42 "$prefix$index$suffixSmall"
	((index++))
done