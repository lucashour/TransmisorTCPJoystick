package com.ciaa_poncho.lucashour.transmisortcpjoystick;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import java.net.Socket;

public class TcpSocketData {

	private static TcpSocketData singleton_instance = null;
	private String ip_address;
	private int port_number;
	private Socket socket;
	private boolean receive_flag;
	private Activity ui_context;

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
	
	public void setPortNumber(int num){ port_number = num; }

	public int getPortNumber(){ return port_number;	}

	public String getPortNumberAsString(){ return String.valueOf(port_number); }

	public void setIpAddress(String address){ ip_address = address;	}

	public String getIpAddress(){ return ip_address; }

	public boolean isDataCompleted() { return this.ip_address != null; }

    public void setSocket(Socket tcpSocket){ socket = tcpSocket; }

    public Socket getSocket(){ return socket; }

	public void startDataRequest(){ this.receive_flag = true; }

	public void stopDataRequest(){ this.receive_flag = false; }

	public boolean canReceiveData(){ return receive_flag; }

	public Activity getUI_context(){ return ui_context; }

	public void setUI_context(Activity context){ ui_context = context; }

}
