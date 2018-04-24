#include "../lib/sockets/cross_platform_socket.h"

#include <string>
#include <vector>
#include <fstream>
#include <cstdint>
#include <iostream>
#include <algorithm>

std::string toLower(const std::string &str) {
	std::string result(str.length(), '\0');
	std::transform(str.begin(), str.end(), result.begin(), ::tolower);
	return result;
}

std::vector<std::string> explode(const std::string& s, const std::string& delim, bool firstOnly = false)
{
	std::string buff = s;
	std::vector<std::string> v;

	std::size_t pos = buff.find(delim);

	while (pos != std::string::npos) {
		std::string str = buff.substr(0, pos);
		//std::cout << "str : " << str << std::endl;
		v.push_back(str);
		buff = buff.substr(pos + delim.size());
		if (firstOnly) {
			break;
		}
		//std::cout << "buff : " << buff << std::endl;
		pos = buff.find(delim);
	}

	v.push_back(buff);

	return v;
}

void read_file_contents(const char *filename, std::string &contents)
{
  std::ifstream in(filename, std::ios::in | std::ios::binary);
  if (in)
  {
    contents.clear();
    in.seekg(0, std::ios::end);
    contents.resize(in.tellg());
    in.seekg(0, std::ios::beg);
    in.read(&contents[0], contents.size());
    in.close();
    return;
  }
  throw(errno);
}

int main(int argc, char **argv) {
	if (argc != 3) {
		std::cout << "Usage : " << argv[0] << " detectorName imageFileName" << std::endl;
		return 1;
	}

	std::vector<std::string> parts = explode(toLower(argv[2]), ".");

	std::string type;

	if (parts.back().compare("jpg") == 0 || parts.back().compare("jpeg")) {
		type = "jpg";
	}

	if (parts.back().compare("png") == 0 ) {
		type = "png";
	}

	if (type.empty()) {
		std::cout << "Unknown image type" << std::endl;
		return 1;
	}

	struct sockaddr_in addr;

	memset (&addr, 0, sizeof (addr));
	addr.sin_family = AF_INET;
	addr.sin_addr.s_addr = inet_addr ("127.0.0.1");
	addr.sin_port = htons(8000);

	sockInit();

	SOCK sock = socket(addr.sin_family, SOCK_STREAM, 0);

	if (!sockIsValid(sock)) {
		throw std::runtime_error("Error creating socket.");
	}

	std::cout << "Connecting remote host... ";

	int error = connect(sock, (struct sockaddr *) &addr, sizeof(addr));

	if (sockCheckError(error)) {
		throw std::runtime_error("Error connecting remote host.");
	}

	std::string fileContent;

	try {
		read_file_contents(argv[2], fileContent);
	} catch (const int &e) {
		std::cout << "Error reading file : " << e << std::endl;
		return 1;
	}

	int32_t detectorNameSize = htonl((int32_t) strlen(argv[1]));
	send(sock, (const char *) &detectorNameSize, 4, 0);

	if (sockCheckError(error)) {
		throw std::runtime_error("Error connecting remote host.");
	}

	send(sock, argv[1], strlen(argv[1]), 0);

	if (sockCheckError(error)) {
		throw std::runtime_error("Error connecting remote host.");
	}

	send(sock, type.c_str(), type.size(), 0);

	if (sockCheckError(error)) {
		throw std::runtime_error("Error connecting remote host.");
	}

	uint32_t size = htonl((uint32_t)fileContent.size());
	send(sock, (const char *)&size, 4, 0);

	if (sockCheckError(error)) {
		throw std::runtime_error("Error connecting remote host.");
	}

	send(sock, fileContent.c_str(), fileContent.size(), 0);

	if (sockCheckError(error)) {
		throw std::runtime_error("Error connecting remote host.");
	}

	int32_t result;
	recv(sock, (char *)&result, 4, 0);

	result = htonl(result);

	switch (result) {
		case 1:
			std::cout << "Something detected" << std::endl;
		break;

		case 0:
			std::cout << "Nothing detected" << std::endl;
		break;

		default:
			std::cout << "Server returned an error" << std::endl;
	}

	return 0;
}