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

#If we are building in debug mode, some code will be added using
#an "#ifdef DEBUG_MODE" preprocessor test
#So we define this macros here
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -DDEBUG_MODE")

#Project includes a first program named hog_trainer
#The source code of this program is in the file src/hog_trainer.cpp
add_executable(hog_trainer "src/hog_trainer.cpp")

#Project includes a second program named detection_server
#The source code of this program is in the file src/detection_server.cpp
add_executable(detection_server "src/detection_server.cpp" "src/jpgd.cpp")

#Project includes a third program named client
#The source code of this program is in the file src/client.cpp
add_executable(client "src/client.cpp")

#Project includes a program to train detector using deep learning
add_executable(deep_trainer "src/deep_trainer.cpp")

#Because they use Dlib, hog_trainer and deep_trainer need to be linked against it.
target_link_libraries(hog_trainer dlib::dlib)
target_link_libraries(deep_trainer dlib::dlib)

#detection_server needs Dlib too but also different system libraries
#according to the targetted OS (Linux or Windows).
#Same way client need system libraries on Windows.
if(WIN32)
	target_link_libraries(detection_server ws2_32 IphLpapi dlib::dlib)
	target_link_libraries(client ws2_32 IphLpapi)
endif(WIN32)

if(UNIX)
	target_link_libraries(detection_server pthread dlib::dlib)
endif(UNIX)
	
	
