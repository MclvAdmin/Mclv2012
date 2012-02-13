class Packet{
	unsigned char* _final_packet, _packet_type, _params;
	bool _final;
public:
	deque<unsigned char> _data_table, _data, _packet;
	Packet(unsigned char *head);
	template <typename T> void AddData(T data);
	void Finalize();
	void Request_Data(unsigned char data);
	unsigned char *_get_packet();
};

Packet::Packet(unsigned char *head){
	_packet.push_back(191);//
	_packet.push_back(206);////
	_packet.push_back(204);////// NAND ASCII char values for "Pawel" and "Kasia"
	_packet.push_back(158);////
	_packet.push_back(223);//
	_packet.push_back(head[0]);// packet type
	if(head[0] != 6){
		_packet.push_back(head[1]);// hardware type
		_packet.push_back(head[2]);// hardware id
	}
	_params = 0;
	_final = false;
}

template <typename T> void Packet::AddData(T data){
	if(_final) return;
	if(typeid(T).name() == typeid(int).name()) _data_table.push_back(0);
	else if(typeid(T).name() == typeid(double).name()) _data_table.push_back(1);
	else if(typeid(T).name() == typeid(long).name()) _data_table.push_back(2);
	else if(typeid(T).name() == typeid(unsigned char).name()) _data_table.push_back(3);
	else return;
	++ _params;
	unsigned char buffer[sizeof(T)];
	memcpy(buffer, &data, sizeof(T));
	for(int i = 0; i < sizeof(T); ++i){
		_data.push_back(buffer[i]);
	}
}

void Packet::Finalize(){
	_final = true;
	_packet.push_back(_params);
	switch(_packet_type){
	case 0:
	default:
		for(unsigned int i = 0; i < _data_table.size() && _params != 0; ++i){
			_packet.push_back(_data_table[i]);// write the data type table to packet
		}
		for(unsigned int i = 0; i < _data.size() && _params != 0; ++i){
			_packet.push_back(_data[i]);// write data to packet
		}
	}
	_packet.push_back('m');
	_packet.push_back('c');
	_packet.push_back('l');
	_packet.push_back('v');
}

unsigned char* Packet::_get_packet(){
	_final_packet = (unsigned char*) malloc(_packet.size());
	for(unsigned int i = 0; i < _packet.size(); ++i){
		_final_packet[i] = _packet[i];
	}
	for(unsigned int i = 0; i < _packet.size(); ++i){
		cout << (int) _final_packet[i] << ' ';
	}
	return _final_packet;
}