package com.ciaa_poncho.lucashour.transmisortcpjoystick;

import java.io.IOException;
import java.net.Socket;

public class TcpSocketManager {

    public static String connectToSocket(){
        String ipAddress = TcpSocketData.getInstance().getIpAddress();
        String result;
        int portNumber = TcpSocketData.getInstance().getPortNumber();
        if (!isSocketConnected()){
            try {
                TcpSocketData.getInstance().setSocket(new Socket(ipAddress, portNumber));
                result = "Conexión establecida con " + ipAddress + ":" + String.valueOf(portNumber) + ".";
            } catch (IOException e) {
                e.printStackTrace();
                result = "Error al intentar establecer conexión.";
            }
            return result;
        }
        else
            return "Ya existe una conexión. Es necesario cerrar la conexión existente.";
    }

    public static String disconnectFromSocket(){
        String result;
        if (isSocketConnected()){
            try {
                Socket socket = TcpSocketData.getInstance().getSocket();
                socket.close();
                result = "Conexión cerrada exitosamente.";
            } catch (IOException e) {
                e.printStackTrace();
                result = "Error al intentar finalizar conexión.";
            }
            return result;
        }
        else
            return "No existe conexión.";
    }

    public static String sendDataToSocket(String message){
        if (isSocketConnected()){
            Socket socket = TcpSocketData.getInstance().getSocket();
            TcpAsyncSend tcpCommunication = new TcpAsyncSend(socket,message);
            tcpCommunication.executeOnExecutor(TcpAsyncSend.THREAD_POOL_EXECUTOR);
            return "";
        }
        else
            return "Datos no enviados por no existir conexión.";
    }

    private static boolean isSocketConnected(){
        Socket socket = TcpSocketData.getInstance().getSocket();
        if (socket != null){
            if (socket.isConnected())
                return true;
        }
        return false;
    }

}
