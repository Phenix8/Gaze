#ifndef SERVER_HPP
#define SERVER_HPP

#include "../lib/sockets/cross_platform_socket.h"
#include "../lib/thread_pool/thread_pool.hpp"

#include <thread>

template <typename CLIENT_MANAGER_TYPE>
class Server {
	private:
		bool listening;
		std::thread listeningThread;
		ThreadPool<> pool;
		
		void *handlersData;

		void listeningLoop(SOCK listeningSock) {
			listening = true;

			SOCK sockClient;
			struct sockaddr_storage clientAddress;
			socklen_t clientAddressLength = sizeof(struct sockaddr_storage);

			while (listening) {
				sockClient = accept(listeningSock, (struct sockaddr *) &clientAddress, &clientAddressLength);
				//SOCK sockClient = accept(listeningSock, NULL, NULL);

				if (sockCheckError(sockClient)) {
					sockPrintLastError("accept()");
					throw std::runtime_error("Error calling accept()");
				}

				char addressString[256];
				
				switch (clientAddressLength) {
					case sizeof(struct sockaddr_in):
						inet_ntop(AF_INET, (void *) &((struct sockaddr_in *)&clientAddress)->sin_addr, addressString, 256);
					break;

					case sizeof(struct sockaddr_in6):
						inet_ntop(AF_INET, (void *) &((struct sockaddr_in6 *)&clientAddress)->sin6_addr, addressString, 256);
					break;

					default:
						throw std::runtime_error("Invalid address.");
					break;
				}

				pool.addTask(new CLIENT_MANAGER_TYPE(sockClient, addressString, handlersData));
			}
		}

		SOCK setupConnection(int port) {
			int result;
			SOCK listeningSock = socket(AF_INET, SOCK_STREAM, 0);

			if (sockCheckError(listeningSock)) {
				sockPrintLastError("socket()");
				throw std::runtime_error("Error creating socket");
			}

			//#ifdef DEBUG_MODE
				result = sockSetReuseAddress(listeningSock);

				if (sockCheckError(result)) {
					sockPrintLastError("setSockOpt()");
					throw std::runtime_error("Set socket in reuse address mode");
				}
			//#endif

			struct sockaddr_in listeningAddr;
			listeningAddr.sin_family = AF_INET;
			listeningAddr.sin_port = htons(port);
			listeningAddr.sin_addr.s_addr = htonl(INADDR_ANY);

			result = bind(listeningSock, (struct sockaddr *) &listeningAddr, sizeof(listeningAddr));

			if (sockCheckError(result)) {
				sockPrintLastError("bind()");
				throw std::runtime_error("Can't bind given address");
			}

			result = listen(listeningSock, 100);

			if (sockCheckError(result)) {
				sockPrintLastError("listen()");
				throw std::runtime_error("Error calling listen()");
			}

			return listeningSock;
		}

	public:

		Server(
			int port = 8080,
			void *handlersData = NULL
		) {
			this->handlersData = handlersData;

			sockInit();

			SOCK listeningSock = setupConnection(port);

			listeningThread = std::thread(&Server::listeningLoop, this, listeningSock);
		}

		void wait() {
			listeningThread.join();
		}
};

#endif