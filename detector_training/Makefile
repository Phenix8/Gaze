##Build procedure for different programs useful to detector training ##

LIBS =-ldlib
LINUX_LIBS =-lpthread -lX11 -lXext -ljpeg -lpng -lz -lxcb -ldl -lXau -lXdmcp
WINDOWS_LIBS =-lImm32 -lGdi32 -lUser32 -lWinmm -lcomctl32

## Check if we are on Linux or on Windows matching libraries if it isn't the case ##
## Don't hesitate to change that test if your system is not Linux nor Windows     ##
UNAME=$(shell uname)
ifeq (Linux, $(UNAME))
	LIBS += $(LINUX_LIBS)
else
	LIBS += $(WINDOWS_LIBS)
endif

all: imglab trainer

clean:
	cd build; rm -rf *;
	rm lib/libdlib.a;

## trainer ##
trainer: build/trainer.exe

build/trainer.exe: dlib build/trainer.o
	g++ build/trainer.o -static -static-libgcc -static-libstdc++ -o build/trainer.exe -I include -L lib -Wl,--start-group $(LIBS) -Wl,--end-group

build/trainer.o: dlib src/trainer++.cpp
	g++ -std=c++11 -O3 -march=native -mavx -c src/trainer++.cpp -o build/trainer.o -I include


## imglab ##
imglab: build/imglab.exe

build/imglab.exe: dlib
	g++ -std=c++11 imglab/src/*.cpp -static -static-libgcc -static-libstdc++ -O3 -march=native -mavx -o build/imglab.exe -I include -L lib -Wl,--start-group $(LIBS) -Wl,--end-group

## Build dlib library ##
dlib: lib/libdlib.a

lib/libdlib.a: ../Android_app/app/dlib
	mkdir -p build/dlib;
	cd build/dlib; cmake -DCMAKE_BUILD_TYPE=RELEASE -DDLIB_JPEG_SUPPORT=ON -DDLIB_ENABLE_ASSERT=OFF ../../../Android_app/app/dlib; make -j5;
	cp build/dlib/dlib/libdlib.a lib/libdlib.a
