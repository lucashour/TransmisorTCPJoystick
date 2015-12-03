package com.ciaa_poncho.lucashour.transmisortcpjoystick;

import android.os.AsyncTask;
//import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class TcpAsyncReceive extends AsyncTask<Void, String, Void> {

    private Socket socket;


    public TcpAsyncReceive(Socket socket){
        this.socket = socket;
    }

    protected Void doInBackground(Void... params) {
        while (TcpSocketData.getInstance().canReceiveData()){
            try {
                receiveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected void onProgressUpdate(String... values){
        TextView displayMessageMotor1 = (TextView) TcpSocketData.getInstance().getUI_context().findViewById(R.id.incoming_message);
        TextView displayMessageMotor2 = (TextView) TcpSocketData.getInstance().getUI_context().findViewById(R.id.incoming_message2);
        if ((values[0].charAt(0) == '0') && (displayMessageMotor1 != null))
            displayMessageMotor1.setText(values[0].substring(1,values[0].length()));
        if ((values[0].charAt(0) == '1') && (displayMessageMotor2 != null))
            displayMessageMotor2.setText(values[0].substring(1,values[0].length()));
    }

    private void receiveData() throws IOException {
        String incomingMessage = "";
        Character currentCharacter;
        int limitCounter = 0;
        if (TcpSocketData.getInstance().canReceiveData()) {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()), 50);
            while (TcpSocketData.getInstance().canReceiveData()){
                if ((currentCharacter = (char) input.read()) != null){
                //if((currentCharacter = TcpSocketManager.receiveCharFromSocket()) != 0){
                    if (currentCharacter == '$')
                        limitCounter++;
                    else {
                        incomingMessage += currentCharacter;
                    }
                    if (limitCounter == 2){
                        //Log.i("TCP", processIncomingMessage(incomingMessage));
                        publishProgress(processIncomingMessage(incomingMessage));
                        limitCounter = 0;
                        incomingMessage = "";
                    }
                }
            }
        }
        cancel(true);
    }

    private String processIncomingMessage(String incomingMessage){
        /* Procesamiento de string de entrada */
        String command = incomingMessage.substring(0, 5);
        String output;
        switch (command){
            case "SPEED":
                output = " Motor ";
                break;
            default:
                output = " ... ";
                break;
        }
        char motorId = incomingMessage.charAt(5);
        String dataType = "";
        switch (incomingMessage.charAt(6)){
            case '0':
                dataType = "RPM";
                break;
            case '1':
                dataType = "RPS";
                break;
        }
        String value = incomingMessage.substring(7,incomingMessage.length());
        /* Construcción de mensaje para mostrar */
        return (motorId + dataType + output + motorId + ": " + value);
    }
}
/*
"$SPEED[0-1][0-1]<rpm>$"
  -----> Primer corchete: ID del motor -> 0 ó 1
  -----> Segundo corchete: Tipo de dato -> 0: RPM
                                        -> 1: RPS
  Ejemplo:
  -----> $SPEED015400$ -> incomingMessage: SPEED015400 -> Mensaje a mostrar: RPM Motor 0: 5400
*/