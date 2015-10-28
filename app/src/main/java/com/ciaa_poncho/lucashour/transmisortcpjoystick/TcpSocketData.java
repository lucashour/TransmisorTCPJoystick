package com.ciaa_poncho.lucashour.transmisortcpjoystick;

import java.net.Socket;

public class TcpSocketData {

	private static TcpSocketData singleton_instance = null;
	private String ip_address;
	private int port_number;
	private Socket socket;

	protected TcpSocketData(){
		/* Aplicación de patrón Singleton para mantener una única instancia de la clase
		 * accesible desde cualquier sector de la applicación */
	}

	public static TcpSocketData getInstance() {
		if(singleton_instance == null) {
			singleton_instance = new TcpSocketData();
		}
		return singleton_instance;
	}
	
	public void setPortNumber(int num){
		port_number = num;
	}

	public int getPortNumber(){ return port_number;	}

	public String getPortNumberAsString(){ return String.valueOf(port_number); }

	public void setIpAddress(String address){
		ip_address = address;
	}

	public String getIpAddress(){
		return ip_address;
	}

    public void setSocket(Socket tcpSocket){
        socket = tcpSocket;
    }

    public Socket getSocket(){ return socket; }

}
