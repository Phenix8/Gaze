#ifndef PRESENCE_ADVERTISER_HPP
#define PRESENCE_ADVERTISER_HPP

#include "udp_broadcaster.hpp"

#include <thread>
#include <chrono>

class PresenceAdvertiser {
	private:
		std::thread thread;
		UDPBroadcaster broadcaster;
		bool started;

		template <typename TYPE, typename PERIOD>
		void advertisingLoop(
			const std::string &interfaceName,
			unsigned int port, 
			const std::string &message,
			const std::chrono::duration<TYPE, PERIOD> &delay,
			bool retryUntilInterfaceReady
		) {
			
			if (retryUntilInterfaceReady) {
				while (!broadcaster.isOpen()) {
					try {
						broadcaster.open(interfaceName, port);
					} catch (const std::exception &e) {
						std::this_thread::sleep_for(std::chrono::seconds(1));
					}
				}
			} else {
				broadcaster.open(interfaceName, port);
			}

			while (started) {
				broadcaster.broadcast(message);
				std::this_thread::sleep_for(delay);
			}

			broadcaster.close();
		}

	public:
		PresenceAdvertiser() {
			started = false;
		}

		PresenceAdvertiser(
			const std::string &interfaceName,
			unsigned int port,
			const std::string &message,
			bool retryUntilInterfaceReady = false
		) :PresenceAdvertiser() {
			start(interfaceName, port, message, retryUntilInterfaceReady);
		}

		template <typename TYPE, typename PERIOD>
		PresenceAdvertiser(
			const std::string &interfaceName,
			unsigned int port,
			const std::string &message,
			const std::chrono::duration<TYPE, PERIOD> &delay,
			bool retryUntilInterfaceReady = false
		) :PresenceAdvertiser() {
			start(interfaceName, port, message, delay, retryUntilInterfaceReady);
		}

		void start(
			const std::string &interfaceName,
			unsigned int port,
			const std::string &message,
			bool retryUntilInterfaceReady = false
		) {
			start(interfaceName, port, message, std::chrono::seconds(10), retryUntilInterfaceReady);
		}

		template <typename TYPE, typename PERIOD>
		void start(
			const std::string &interfaceName,
			unsigned int port,
			const std::string &message,
			const std::chrono::duration<TYPE, PERIOD> &delay,
			bool retryUntilInterfaceReady = false
		) {
			if (started == true) {
				throw std::runtime_error("Already started.");
			}

			started = true;
			thread = std::thread(
				&PresenceAdvertiser::advertisingLoop<TYPE, PERIOD>, this,
				interfaceName, port, message, delay, retryUntilInterfaceReady
			);
		}

		void stop() {
			started = false;
			thread.join();
		}
};

#endif