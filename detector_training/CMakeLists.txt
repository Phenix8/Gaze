#
# This is a CMake makefile.  You can find the cmake utility and
# information about it at http://www.cmake.org
#

#This project require cmake version 3.2
cmake_minimum_required(VERSION 3.2 FATAL_ERROR)

#Our project is named "GazeTrainers"
#This is the version 0.2
#And it is written in C++
project(GazeTrainers VERSION 0.2 LANGUAGES CXX)

#This project requires C++ 11
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

#This project requires the library Dlib at least in version 19.9
find_package(dlib 19.9 REQUIRED)

#It includes a first program named hog_trainer
#The source code of this program is in the file src/hog_trainer.cpp
add_executable(hog_trainer "src/hog_trainer.cpp")

#Because it uses Dlib, hog_trainer needs to be linked against it.
target_link_libraries(hog_trainer dlib::dlib)
	
	
