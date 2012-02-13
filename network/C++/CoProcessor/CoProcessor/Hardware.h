class Joystick : public NetObject{
	double Yaxis, Xaxis;
	unsigned int button_flags;
public:
	Joystick(unsigned char pwm) : NetObject(pwm){
		address = 5;
		address <<= 8;
		address += pwm;
		_hardware_elements[address] = this;
	}

	void recieve(unsigned char *data){
		if (data == nullptr){
			cout << "Recieved nullptr\n";
			return;
		}
		cout << "Joystick got data!\n";
		memcpy(&button_flags, data, 4);
		memcpy(&Yaxis, &data[4], 8);
		memcpy(&Xaxis, &data[12], 8);
		cout << "Got: " << button_flags << ' ' << Yaxis << ' ' << Xaxis << '\n';
	}

	bool getButtonState(int val){
		_ask_data();
		if((button_flags & pow2(val)) == 0) return true;
		else return false;
	}

	double getY(){
		_ask_data();
		return Yaxis;
	}
	double getX(){
		_ask_data();
		return Xaxis;
	}
	void _ask_data(){
		unsigned char head[] = {2,5,address};// packet type, hardware type, hardware id
		Packet *temp = new Packet(head);
		temp->AddData(0);
		temp->Finalize();
		_network_interface.SendPacket(temp);
		delete temp;
		Sleep(1);//should be enough time for data to come back from cRio
	}
};
//------------------------------------------------
//------------------------------------------------
class Jaguar : public NetObject{
	double value;
public:
	Jaguar(unsigned char pwm) : NetObject(pwm){
		address = 1;
		address <<= 8;
		address += pwm;
		_hardware_elements[address] = this;
	}
	void set(double val){
		value = val;
		unsigned char head[] = {1,1,address};
		Packet *temp = new Packet(head);
		temp->AddData(value);
		temp->Finalize();
		_network_interface.SendPacket(temp);
		delete temp;
	}

};