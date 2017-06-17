##Build procedure for different programs useful to detector training ##

all: imglab trainer

clean:
	cd build; rm -rf *;
	rm lib/libdlib.a;

## trainer ##
trainer: build/trainer.exe

build/trainer.exe: lib/libdlib.a build/trainer.o
	g++ build/trainer.o -static -static-libgcc -static-libstdc++ -o build/trainer.exe -I include -L lib -Wl,--start-group -ldlib -lImm32 -lGdi32 -lUser32 -lWinmm -lcomctl32 -Wl,--end-group

build/trainer.o: src/trainer++.cpp
	g++ -O3 -march=native -mavx2 -c src/trainer++.cpp -o build/trainer.o -I include


## imglab ##
imglab: build/imglab.exe

build/imglab.exe: lib/libdlib.a
	g++ imglab/src/*.cpp -static -static-libgcc -static-libstdc++ -O3 -march=native -mavx2 -o build/imglab.exe -I include -L lib -Wl,--start-group -ldlib -lImm32 -lGdi32 -lUser32 -lWinmm -lcomctl32 -Wl,--end-group

## Build dlib library ##
lib/libdlib.a: ../Android_app/app/dlib
	mkdir -p build/dlib;
	cd build/dlib; cmake -DCMAKE_BUILD_TYPE=RELEASE -DDLIB_JPEG_SUPPORT=ON -DDLIB_ENABLE_ASSERT=OFF ../../../Android_app/app/dlib; make -j5;
	cp build/dlib/dlib/libdlib.a lib/libdlib.a