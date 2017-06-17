##Build procedure for different programs useful to detector training ##

all: imglab trainer

## trainer ##
trainer: build/trainer.exe

build/trainer.exe: build/trainer.o
	g++ build/trainer.o -static -static-libgcc -static-libstdc++ -o build/trainer.exe -I include -L lib -Wl,--start-group -ldlib -lImm32 -lGdi32 -lUser32 -lWinmm -lcomctl32 -Wl,--end-group

build/trainer.o: src/trainer++.cpp
	g++ -O3 -march=native -mavx2 -c src/trainer++.cpp -o build/trainer.o


## imglab ##
imglab: build/imglab.exe

build/imglab.exe:
	g++ imglab/*.cpp -static -static-libgcc -static-libstdc++ -o build/imglab.exe -I include -L lib -Wl,--start-group -ldlib -lImm32 -lGdi32 -lUser32 -lWinmm -lcomctl32 -Wl,--end-group
