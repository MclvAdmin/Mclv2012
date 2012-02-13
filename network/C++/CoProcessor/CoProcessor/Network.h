class Network{
#define DEFAULT_BUFF_SIZE 128
	int retval, fromlen, i;
	WSADATA wsaData;
	SOCKET server, client;
	sockaddr_in local, from;
	int pack_count;
public:
	Network(){
		pack_count = 0;
		retval = (int) WSAStartup(0x202, &wsaData);
		if(retval != 0){
			std::cout << "Error::Server: WSAStartup() failed: " << retval << '\n';
			WSACleanup();
		}

		local.sin_family = AF_INET;
		local.sin_addr.s_addr = inet_addr(ip_address);
		local.sin_port = htons(port);
		//
		server = (SOCKET) socket(AF_INET, SOCK_STREAM, 0);
		
		if(server == INVALID_SOCKET){
			std::cout << "Error::Server: socket() failed with error: " << WSAGetLastError() << '\n';
			WSACleanup();
		}

		if(bind(server, (sockaddr*) &local, sizeof(local)) == SOCKET_ERROR){
			std::cout << "ERROR:SERVER: bind() failed with error: " << WSAGetLastError();
			cout << " And IP: " << ip_address << '\n';
			WSACleanup();
		}
	}

	void SERVER(){	
		
		if((int)listen(server, 5) == SOCKET_ERROR){
			std::cout << "Error::Server: listen() failed with error: " << WSAGetLastError() << '\n';
			WSACleanup();
		}
		std::cout << "Listening and awaiting connection on address: " << ip_address << ':' << port << '\n';
		fromlen = sizeof(from);
		client = (SOCKET) accept(server, (struct sockaddr*)&from, &fromlen);
		cout << "Client Connected!\n";
		_connected = true;
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		unsigned char expected_head[] = {191,206,204,158,223};
			while(1){
			//unsigned char * _recv_Buffer = (unsigned char *) malloc(128);
				unsigned char _recv_Buffer[128];
			retval = (int) recv(client, (char*) _recv_Buffer, DEFAULT_BUFF_SIZE, 0);
			if(retval == SOCKET_ERROR){
				int err = WSAGetLastError();
				if(err==10054){
					cout << "Client Disconnected\n";
				}
				else if(err == 10060){
					cout << "Client failed to respond in reasonable amount of time; therefore: disconnected\n";
				}
				else std::cout << "Error::Server: recv() failed!\n" << WSAGetLastError() << '\n';
				closesocket(client);
				_connected = false;
				client = (SOCKET) accept(server, (struct sockaddr*)&from, &fromlen);
				cout << "Client Connected!\n";
				_connected = true;
			}
			
			if(memcmp(expected_head, _recv_Buffer, 5) == 0){
				cout << "HEAD FOUND!\n";
				unsigned short hardware;
				memcpy(&hardware, &_recv_Buffer[6], 2);
				switch(_recv_Buffer[5]){
				case 0: cout << "NULL packet recieved\n"; break;
				case 1:
					cout << "Send Data Packet Recieved\n";
					_hardware_elements[ hardware ] -> recieve( &_recv_Buffer[12] );
						break;
				case 6:
					cout << "Client-Server KEEPALIVE\n";
					//cout << 
					break;
				default:
					cout << "Other packet type: "<< _recv_Buffer[5] << '\n';
					ReconstructPacket(_recv_Buffer);
					}
				}
			else cout << "Bogous Data Stream Recieved\n";
			for(int j = 0; j < 32; ++j){
				cout << (int)_recv_Buffer[j] << ' ';
			}
			cout << endl;
			}
	}

	void SendPacket(Packet *letter){
		if( _connected == false){
			cout << "Packet not sent as cliented is disconnected\n";
			return;
		}
		int err = send(client, (const char*) letter->_get_packet(), letter->_packet.size(), 0);
		if(err == SOCKET_ERROR){
			cout << "ERROR: send() failed: " << WSAGetLastError() << endl;
		}
		else cout << "Packet Sent! " << ++pack_count << endl;
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	template <typename t> t ReadData(t super_useless_parameter, byte *data){
			t temp;
			memcpy(&temp, data, sizeof(t));
			return temp;
		}


		void ReconstructPacket(byte *data){
			byte expected_head[] = {191,206,204,158,223}; int index = 0, parameters = 0;
			if(memcmp(expected_head, data, 5) == 0){
				cout << "\nHEAD FOUND!" << endl;
				index += 5;
			}
			cout << "Packet Type: " << (int) data[index] << endl; ++index;
			cout << "Hardware Adress: " << (int) data[index] << endl; ++index;
			cout << "Number of Parameters: " << (int) data[index] << endl; parameters = data[index]; ++index;
			unsigned int _data_offset = 0;


			list<int> integers;
			list<double> doubles;
			list<long> longs;
			list<unsigned char> bytes;

			for(int i = 0; i < parameters; ++i){
				switch(data[index+i]){
				case 0:
					integers.push_back(ReadData(23, &data[index+parameters+_data_offset]));
					_data_offset += sizeof(int);
					break;
				case 1:
					doubles.push_back(ReadData(23.0, &data[index+parameters+_data_offset]));
					_data_offset += sizeof(double);
					break;
				case 2:
					longs.push_back(ReadData(23L, &data[index+parameters+_data_offset]));
					_data_offset += sizeof(long);
					break;
				case 3:
					bytes.push_back(ReadData((byte)23, &data[index+parameters+_data_offset]));
					_data_offset += sizeof(unsigned char);
					break;
				}
			}
			cout << "Itegers: ";
			while(!integers.empty()){
				cout << integers.front() << " ";
				integers.pop_front();
			}
			cout << endl;
			cout << "Doubles: ";
			while(!doubles.empty()){
				cout << doubles.front() << " ";
				doubles.pop_front();
			}
			cout << endl;
			cout << "Longs: ";
			while(!longs.empty()){
				cout << longs.front() << " ";
				longs.pop_front();
			}
			cout << endl;
			
		}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

} _network_interface;

DWORD WINAPI HEARTBEAT(LPVOID lpParam){
	while(1){
		unsigned char head[] = {6};
		Packet *p = new Packet(head);
		p->Finalize();
		_network_interface.SendPacket(p);
		Sleep(1000);
	}
}