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

static int nbDetection = 0;

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

	public:
		DetectionTask(const SOCK sock, const std::string &clientAddress, const void *detectorPtr) {
			this->clientAddress = clientAddress;
			this->sock = sock;
			this->detector = (DETECTOR_TYPE *) detectorPtr;
			creationTime = Clock::now();
		}

		virtual void run() {

			try {
				int32_t detectorNameSize, imageSize;
				std::string detectorName, imageType, imageData;

				detectorNameSize = readInt32();

				if (detectorNameSize > 20) {
					throw std::runtime_error("Received detector name too big");
				}

				read(detectorName, detectorNameSize);

				read(imageType, 3);

				imageSize = readInt32();

				//if annonced image size > 10 Mo
				if (imageSize > 10485760) {
					throw std::runtime_error("Received image size is too big");
				}

				read(imageData, imageSize);

    			dlib::matrix<dlib::rgb_pixel> image;

    			if (imageType.compare("jpg") == 0) {
    				decodeJPG(imageData.c_str(), imageData.size(), image);
    			} else {
    				throw std::runtime_error("Unsupported image format");
    			}

			    const std::vector<dlib::rectangle> dets = detector->detect(detectorName, image);

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
		std::cout << "  - On Linux, it is probably wlan0" << std::endl;
		std::cout << "  - On Windows, open regedit.exe, navigate though" << std::endl;
		std::cout << "    \\HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\NetworkCards" << std::endl;
		std::cout << "    find the one which is your Wifi card by looking the description of the different items, then" << std::endl;
		std::cout << "    copy the value of the field ServiceName (it is of the form {xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxx})." << std::endl;
		std::cout << "  - Otherwise use -no_broadcasting to disable the advertising of this detection server." << std::endl;
		return 1;
	}

	try {
		PresenceAdvertiser advertiser;

		if (strncmp("-no_broadcasting", argv[1], 16) != 0) {
			advertiser.start(argv[1], 5151, "DETECTION SERVER HERE");
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