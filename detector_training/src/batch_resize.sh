#!/usr/bin/env bash

folder=$1
extension=$2

if [ "$extension" = "" ]; then
	extension=jpg
fi

trainingFile=/training.xml

if ! [ -f "$folder$trainingFile" ]; then
	for file in $folder/*.$extension
	do
		convert $file -resize 640 $file 
	done

	./build/imglab.exe -c $folder/training.xml $folder
fi

./build/imglab.exe $folder/training.xml

./build/trainer.exe -tv -u3 $folder/training.xml

testingFile=/testing/testing.xml

if ! [ -f "$folder$testingFile" ]; then
	mkdir $folder/testing
	cp $folder/*.$extension $folder/testing

	x2=_x2.
	x4=_x4.

	for file in $folder/testing/*.$extension
	do
		filename=$(echo $file | cut -f1 -d.)
		convert $file -resize 1280 "$file$x2$extension"
		convert $file -resize 2560 "$file$x4$extension"
	done

	./build/imglab.exe -c $folder/testing/testing.xml $folder/testing
fi

./build/trainer.exe $folder/testing/testing.xml