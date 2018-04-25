#include "server.hpp"
#include "presence_advertiser.hpp"
#include "jpgd.h"

#include <dlib/image_processing.h>
#include <dlib/data_io.h>
#include <dlib/cmd_line_parser.h>

#include <map>
#include <cstdint>
#include <iostream>
#include <sstream>
#include <algorithm>
#include <chrono>

#define CLAMP(value) value < 0 ? 0 : (value > 255 ? 255 : value)

template <typename DETECTOR_TYPE>
class DetectionTask : public PoolTask {
	using Time = std::chrono::time_point<std::chrono::system_clock>;
	using Duration = std::chrono::duration<float>;
	using Clock = std::chrono::system_clock;

	private:
		Time creationTime;
		std::string clientAddress;
		SOCK sock;
		DETECTOR_TYPE *detector;

		void read(std::string &message, std::size_t length) {
			int n, total = 0;
			message.clear();
			message.resize(length, 0);

			while(total < length) {
				n = recv(sock, &message[total], length-total, 0);
				if (sockCheckError(n)) {
					throw std::runtime_error("Network error");
				}
				total += n;
			}
		}

		int32_t readInt32() {
			int32_t network_byte_order_int;
			std::string data;

			read(data, 4);
			std::memcpy(&network_byte_order_int, data.c_str(), 4);
			return ntohl(network_byte_order_int);
		}

		void sendInt32(int32_t i) {
			i = htonl(i);
			send(sock, (const char *) &i, 4, 0);
		}

		static void decodeJPG(const char *compressedData, std::size_t compressedDataSize, dlib::matrix<dlib::rgb_pixel> &image) {
			int uncompressedWidth;
			int uncompressedHeight;
			int uncompressedNumChannels;
			
			unsigned char *decompressedImage =
				jpgd::decompress_jpeg_image_from_memory(
					(const unsigned char *) compressedData,
					compressedDataSize,
					&uncompressedWidth,
					&uncompressedHeight,
					&uncompressedNumChannels,
					3
				);

			if (decompressedImage == NULL) {
				throw std::runtime_error("Failed to decode JPEG image.");
			}

			//std::cout << "Width : " << uncompressedWidth << ", height : " << uncompressedHeight;
			//std::cout <<", channels : " << uncompressedNumChannels << std::endl;

			image = dlib::matrix<dlib::rgb_pixel>((int)uncompressedHeight, (int)uncompressedWidth);

			std::memcpy(&(image(0,0)), decompressedImage, uncompressedWidth * uncompressedHeight * uncompressedNumChannels);

			delete decompressedImage;
		}

		static void yuv420p2rgb888(
	        size_t width, size_t height,
	        size_t yRowStride,
	        size_t uvPixelStride,
	        size_t uvRowStride,
	        const uint8_t *yData,
	        const uint8_t *uData,
	        const uint8_t *vData,
	        uint8_t *rgbData
		) {
		    int b, g, r, yy, uu, vv, uvIndex;
		    for (int y = 0; y < height; y++) {
		        for (int x = 0; x < width; x++) {
		            yy = yData[(y * yRowStride) + x];
		            uvIndex = uvPixelStride * (x / 2) + uvRowStride * (y / 2),
		            uu = uData[uvIndex] - 128;
		            vv = vData[uvIndex] - 128;

		            r = yy + 1.403f * vv;
		            g = yy - 0.344f * uu - 0.714f * vv;
		            b = yy + 1.770f * uu;
		            *rgbData++ = CLAMP(r);
		            *rgbData++ = CLAMP(g);
		            *rgbData++ = CLAMP(b);
		        }
		    }
		}

		static void decodeYUV(
			size_t width, size_t height,
	        size_t yRowStride,
	        size_t uvPixelStride,
	        size_t uvRowStride,
	        const uint8_t *yData,
	        const uint8_t *uData,
	        const uint8_t *vData,
	        dlib::matrix<dlib::rgb_pixel> &image
        ) {
			image = dlib::matrix<dlib::rgb_pixel>((int)height, (int)width);

			yuv420p2rgb888(
				width, height,
				yRowStride,
				uvPixelStride, uvRowStride,
				yData, uData, vData,
				(uint8_t *) (&image(0, 0))
			);
		}

	public:
		DetectionTask(const SOCK sock, const std::string &clientAddress, const void *detectorPtr) {
			this->clientAddress = clientAddress;
			this->sock = sock;
			this->detector = (DETECTOR_TYPE *) detectorPtr;
			creationTime = Clock::now();
		}

		virtual void run() {

			try {
				dlib::matrix<dlib::rgb_pixel> image;

				int32_t detectorNameSize;
				std::string detectorName, imageType;

				detectorNameSize = readInt32();

				if (detectorNameSize > 20) {
					throw std::runtime_error("Received detector name too big");
				}

				std::cout << "Detector name size : " << detectorNameSize << std::endl;

				read(detectorName, detectorNameSize);

				std::cout << "Detector name : " << detectorName << std::endl;

				read(imageType, 3);

				std::cout << "Img type : " << imageType << std::endl;

				if (imageType.compare("jpg") == 0) {
					int imageSize = readInt32();

					//if annonced image size > 10 Mo
					if (imageSize > 10485760) {
						throw std::runtime_error("Received image size is too big");
					}

					std::string imageData;
					read(imageData, imageSize);
    			
    				decodeJPG(imageData.c_str(), imageData.size(), image);
    			} else if (imageType.compare("yuv") == 0) {
    				int imageWidth = readInt32();
    				int imageHeight = readInt32();

    				std::cout << imageWidth << " " << imageHeight << std::endl;

    				if (imageWidth > 640 || imageHeight > 640) {
    					throw std::runtime_error("Received image dimensions are too big");
    				}

    				int yRowStride = readInt32();
    				int uvPixelStride = readInt32();
    				int uvRowStride = readInt32();

    				int ySize = readInt32();

    				if (ySize > 10485760) {
    					throw std::runtime_error("Received y plane size is too big");
    				}

    				std::string yData;
    				read(yData, ySize);

    				int uvSize = readInt32();

    				if (uvSize > 10485760) {
    					throw std::runtime_error("Received uv planes size is too big");
    				}

    				std::string uData, vData;
    				read(uData, uvSize);
    				read(vData, uvSize);

    				decodeYUV(
    					imageWidth, imageHeight,
    					yRowStride,
    					uvPixelStride, uvRowStride,
    					(const uint8_t*) yData.c_str(), (const uint8_t*) uData.c_str(), (const uint8_t*) vData.c_str(), 
						image
					);
				} else {
    				throw std::runtime_error("Unsupported image format");
    			}

			    const std::vector<dlib::rectangle> dets = detector->detect(detectorName, image);

			    std::cout << dets.size() << " shape(s) detected" << std::endl;

				sendInt32(dets.size() > 0 ? 1 : 0);

				std::cout << "Handled detection request for " << clientAddress;
				std::cout << " in " << Duration(Clock::now() - creationTime).count() << "s" << std::endl;

			} catch (const std::exception &e) {
				std::cout << "Error handling detection request from " << clientAddress << " : " << e.what() << std::endl;
				sendInt32(-1);
			}

			sockClose(sock);
		}
};

class FHOGDetector {
	using image_scanner_type = dlib::scan_fhog_pyramid< dlib::pyramid_down<6> >;
	using Detector = dlib::object_detector<image_scanner_type>;
	
	private:
		std::map<const std::string, Detector> detectors;

	public:
		void loadDetectors(const std::string &directoryName) {
			Detector detector;
			dlib::directory dir(directoryName);

			std::vector<dlib::file> files = dir.get_files();

			std::cout << "-----------------" << std::endl;
			for (const auto &file : files) {
				dlib::deserialize(file) >> detector;
				detectors[file.name()] = detector;
				std::cout << "Loaded detector " << file.name() << std::endl;
			}
			std::cout << "-----------------" << std::endl;
		}

		std::vector<dlib::rectangle> detect(const std::string &detectorName, const dlib::matrix<dlib::rgb_pixel> &image) {
			if (detectors.find(detectorName) == detectors.end()) {
        		throw std::runtime_error("Unknown detector name");
    		}

    		Detector detector_copy = detectors[detectorName];

    		int zoomLevel = 4;

			dlib::matrix<unsigned char> rsz_img(480 * zoomLevel, 640 * zoomLevel);

			//std::cout << "Resizing image..." << std::endl;
		    dlib::resize_image(image, rsz_img);

			return detector_copy(rsz_img);
		}
};

int main(int argc, char **argv) {

	if (argc != 2) {
		std::cout << "Usage :\r\n    " << argv[0] << " INTERFACE_NAME" << std::endl;
		std::cout << "    " << argv[0] << " -no_broadcasting" << std::endl << std::endl;
		std::cout << "INTERFACE_NAME is the name of your Wifi interface :" << std::endl;
		std::cout << "  - On Linux, it is probably wlan0, otherwise type ifconfig, or ip address in a terminal to find its name." << std::endl;
		std::cout << "  - On Windows, open regedit.exe, navigate though" << std::endl;
		std::cout << "    \\HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\NetworkCards" << std::endl;
		std::cout << "    find the one which is your Wifi card by looking the description of the different items, then" << std::endl;
		std::cout << "    copy the value of the field ServiceName (it is of the form {xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxx})." << std::endl;
		std::cout << "  - Finally use -no_broadcasting option to disable advertise of this detection server." << std::endl;
		return 1;
	}

	try {
		PresenceAdvertiser advertiser;

		if (strncmp("-no_broadcasting", argv[1], 16) != 0) {
			advertiser.start(argv[1], 5151, "DETECTION SERVER HERE:8000");
			std::cout << "Broadcasting on port 5151" << std::endl;
		}

		FHOGDetector scanner;
		scanner.loadDetectors("../detector");
		Server< DetectionTask<FHOGDetector> > server(8000, &scanner);
		std::cout << "Listening on port 8000..." << std::endl;
		server.wait();
	} catch (const std::exception &e) {
		std::cout << "An error occured : " << e.what() << std::endl;
	}

	return 0;
}
