#include <WinSock.h>
#include <Windows.h>
#include <iostream>
#include <list>
#include <deque>
#include <map>
#include <vector>
using namespace std;
bool _connected = false;
char* ip_address = "10.11.55.6";
int port = 1140;
//-----------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------
int pow2(int val){
	int ans = 1;
	for(int i = 0; i < val; ++i){
		ans *= 2;
	}
	return ans;
}
#include "Packet.h"
//-----------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------
class NetObject{
public:
	unsigned short address;
	NetObject(unsigned char pwm){}
	
	//IMPORTANT: ANY NETOBJECT METHODS MUST BE PROTOTYPED HERE BEFORE THEY CAN WORK
	virtual void recieve(unsigned char* data){}
	virtual void set(double val){}
	virtual double getY(){return 0;}
	virtual bool getButtonState(int val){return 0;}
};
map<unsigned short, NetObject*> _hardware_elements;

//-----------------------------------------------------------------------------------
#include "Network.h"
#include "Hardware.h"
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


DWORD WINAPI start_server(LPVOID params){
	_network_interface.SERVER();
	return -1;
}

void init_hardware(){
	//ASSIGN HARDWARE ADDRESSES HERE
	new Joystick(1);
	new Joystick(2);
	//TWO GEESE WITH ONE STONE
	for(int i = 1; i <= 4; ++i){
		new Jaguar(i);
	}
	unsigned short p = 5;
	p <<= 8;
	p += 1;
	while(1){
		_hardware_elements[p]->getY();
		Sleep(1000);
	}
}

DWORD WINAPI DRIVE(LPVOID lpParam){
#define leftjoy 1
#define rightjoy 2
#define startjags 3 //THE DRIVE JAGS IN CONFIGURATION: LEFT LEFT RIGHT RIGHT
	while(_connected){
		double left = _hardware_elements[leftjoy]->getY();
		double right = _hardware_elements[rightjoy]->getY();
		for(int i = startjags; i <= startjags+1; ++i){
			_hardware_elements[i]->set(left);
			_hardware_elements[i+2]->set(right);
		}
		//RECORDING
		bool _recording = false;
		deque<double*> left_record, right_record;
		while(_recording){
			//@TODO: CLEAR ANY MEMORY PREVIOUSLY USED
			while(!right_record.empty()){
				free(left_record[0]);
				free(right_record[0]);
				left_record.pop_front();
				right_record.pop_front();
			}
			double* d = (double*) calloc(1024, 8); // ENOUGH FOR 1024 DOUBLES
			double* b = (double*) calloc(1024, 8); // MEMORY LEAK IF NOT CLEANED UP
			left_record.push_back(d);
			right_record.push_back(b);
			for(int j = 0; j < 1024; ++j){
				d[j] = _hardware_elements[leftjoy]->getY();
				b[j] = _hardware_elements[rightjoy]->getY(); // 16 KB/s * 60 seconds = 960 KB per minute
				//Sleep(20); ALREADY LIMITED BY GET METHODS
			}
			_recording = _hardware_elements[leftjoy]->getButtonState(1);
		}
	}
	return 0;
}

int main(){
	HANDLE _server_thread = CreateThread(NULL,0,start_server,NULL,0,NULL);
	while(!_connected) Sleep(200); // WAIT FOR CLIENT
	init_hardware(); //@TODO INITIALIZE OBJECTS *AFTER* CLIENT CONNECTION (and reinitialize after connection lost/reestablished
	HANDLE heartbeat = CreateThread(0,0,HEARTBEAT,0,0,0);
	//HANDLE _drive_thread = CreateThread(NULL,0,DRIVE,NULL,0,NULL);
	


	/*
	LARGE_INTEGER _count;
	LARGE_INTEGER _freq; //Frequency of one second
	QueryPerformanceFrequency(&_freq);
	DWORD count = 0;
	DWORD freq = _freq.LowPart;
	DWORD _tStart = 0;
	unsigned char ping[] = {5,0,0,0};
	Packet status(ping);
	while(1){
		QueryPerformanceCounter(&_count);
		count = _count.LowPart;
		_tStart = count/freq;
		cin.ignore();
		unsigned char data[] = {3,1,1};
		Packet temp(data);
		int send = count;
		temp.AddData(send);
		Packet stat = status;
		temp.Finalize();
		stat.Finalize();
		_network_interface.SendPacket(&temp);
		_network_interface.SendPacket(&stat); //Automate the status packet send.
	}*/

	cout << "YO";
	Sleep(999999);
}