package com.ciaa_poncho.lucashour.transmisortcpjoystick.TCP;

import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ciaa_poncho.lucashour.transmisortcpjoystick.Calibration.CalibrationData;
import com.ciaa_poncho.lucashour.transmisortcpjoystick.R;

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
        // Inicialización de elementos de GUI
        TextView displayMessageMotor1 = (TextView) TcpSocketData.getInstance().getUI_context().findViewById(R.id.incoming_message);
        TextView displayMessageMotor2 = (TextView) TcpSocketData.getInstance().getUI_context().findViewById(R.id.incoming_message2);
        ProgressBar [] progressBars = new ProgressBar[2];
        progressBars[0] = (ProgressBar) TcpSocketData.getInstance().getUI_context().findViewById(R.id.motor_0_progress);
        progressBars[1] = (ProgressBar) TcpSocketData.getInstance().getUI_context().findViewById(R.id.motor_1_progress);
        // Lógica de actualización de elements de GUI
        switch (values[0].charAt(0)){
            case 'S':
                if ((values[0].charAt(1) == '0') && (displayMessageMotor1 != null))
                    displayMessageMotor1.setText(values[0].substring(1,values[0].length()));
                else if ((values[0].charAt(1) == '1') && (displayMessageMotor2 != null))
                    displayMessageMotor2.setText(values[0].substring(1,values[0].length()));
                break;
            case 'M':
                if ((values[0].charAt(1) == '0') && (progressBars[0] != null)) {
                    addValueToRpmCurve(values[0]);
                    progressBars[0].incrementProgressBy(1);
                }
                else if ((values[0].charAt(1) == '1') && (progressBars[1] != null)) {
                    addValueToRpmCurve(values[0]);
                    progressBars[1].incrementProgressBy(1);
                }
                break;
        }
    }

    private void receiveData() throws IOException {
        String incomingMessage = "";
        Character currentCharacter;
        int limitCounter = 0;
        if (TcpSocketData.getInstance().canReceiveData()) {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()), 50);
            while (TcpSocketData.getInstance().canReceiveData()){
                if ((currentCharacter = (char) input.read()) != null){
                    if (currentCharacter == '$')
                        limitCounter++;
                    else {
                        incomingMessage += currentCharacter;
                    }
                    if (limitCounter == 2){
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
        // Procesamiento de string de entrada
        String command = incomingMessage.substring(0, 5);
        String output;
        switch (command){
            case "SPEED":
                output = processMotorVelocity(incomingMessage.charAt(5), incomingMessage);
                break;
            case "MOTOR":
                output = processMotorCalibrationProcess(incomingMessage.charAt(6), incomingMessage);
                break;
            default:
                output = " ... ";
                break;
        }
        return output;

    }

    private String processMotorVelocity(char motorId, String message){
        String output, value, dataType;
        output = " Motor ";
        value = message.substring(7,message.length());
        dataType = "";
        switch (message.charAt(6)){
            case '0':
                dataType = "RPM";
                break;
            case '1':
                dataType = "RPS";
                break;
        }
        // Construcción de mensaje para mostrar
        return ("S" + motorId + dataType + output + motorId + ": " + value);
    }

    private String processMotorCalibrationProcess(char motorId, String message){
        // Estructura de 'message' => "MOTOR=id,dutyCycle,interrupciones"
        String [] messageData = message.split("\\,");
        String dutyCycle = messageData[1];
        String rpm = getRpmFromInterruptionAmount(messageData[2]);
        return ("M" + motorId + "," + dutyCycle + "," + rpm);
    }

    private String getRpmFromInterruptionAmount(String interruptions){
        int slots = CalibrationData.getInstance().getSlots(), time = CalibrationData.getInstance().getTime();
        int rpm = (Integer.valueOf(interruptions) * 1000 * 60) / (slots * time);
        return String.valueOf(rpm);
    }

    private void addValueToRpmCurve(String information){
        int motorId = Character.getNumericValue(information.charAt(1));
        String [] dataArray = information.split("\\,");
        int pwm = Integer.valueOf(dataArray[1]);
        int rpm = Integer.valueOf(dataArray[2]);
        CalibrationData.getInstance().getRpmCurves()[motorId][pwm] = rpm;

        // Una vez que la calibración es completada, el máximo valor de RPM alcanzado es almacenado.
        if (pwm == 100){
            if (motorId == 0) {
                CalibrationData.getInstance().setMax_rpm_motor0(rpm);
                int[][] temp = CalibrationData.getInstance().getRpmCurves();
            }
            else {
                CalibrationData.getInstance().setMax_rpm_motor1(rpm);
                int[][] temp = CalibrationData.getInstance().getRpmCurves();
            }
        }

        // Si los motores 0 y 1 ya han sido calibrados, el máximo valor de RPM al que el sistema debe trabajar es calculado
        if (CalibrationData.getInstance().getMax_rpm_motor0() != 0 && CalibrationData.getInstance().getMax_rpm_motor1() != 0)
            CalibrationData.getInstance().setMax_RPM();
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