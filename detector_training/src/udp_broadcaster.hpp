#ifndef UDP_BROADCASTER
#define UDP_BROADCASTER

#include "../lib/sockets/cross_platform_socket.h"

#include <stdexcept>
#include <sstream>

class UDPBroadcaster {

	private:
		SOCK udpSock;
		struct sockaddr_in broadcastAddress;
		bool opened;

	public:
		UDPBroadcaster() {
			sockInit();
			udpSock = 0;
			opened = false;
		}

		UDPBroadcaster(const std::string &interfaceName, unsigned short port) :UDPBroadcaster() {
			open(interfaceName, port);
		}

		void open(const std::string &interfaceName, unsigned short port) {
			if (port > 65565u) {
				throw std::runtime_error("Given port must be in range 0-65535.");
			}

			udpSock = socket(AF_INET, SOCK_DGRAM, 0);

			if (sockCheckError(udpSock)) {
				throw std::runtime_error("Error creating socket.");
			}

			int result = sockEnableBroadcast(udpSock);

			if (sockCheckError(result)) {
				throw std::runtime_error("Can't use UDP broadcasting.");
			}

			struct sockaddr_in address;
			struct sockaddr_in netmask;

			result = sockGetInterfaceAddress(interfaceName.c_str(), AF_INET, (struct sockaddr *) &address, (struct sockaddr *) &netmask);

			if (result == -1) {
				throw std::runtime_error("Error getting interfaces infos.");
			}

			if (result == -2) {
				std::stringstream str;
				str << "Can't find IPv4 compatible interface named \"" << interfaceName << "\"";
				throw std::runtime_error(str.str());
			}

			sockGetBroadcastAddress(&address.sin_addr, &netmask.sin_addr, &address.sin_addr);
			address.sin_port = htons(port);

			broadcastAddress = address;
			opened = true;
		}

		void broadcast(const std::string &message) const {
			sendto(udpSock, message.c_str(), message.length(), 0, (struct sockaddr *) &broadcastAddress, sizeof(struct sockaddr_in));
		}

		void close() {
			int result = sockClose(udpSock);
			if (sockCheckError(result)) {
				throw std::runtime_error("Error closing UDP socket.");
			}
			opened = false;
		}

		bool isOpen() const {
			return opened;
		}
};

#endif