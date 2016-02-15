package com.ciaa_poncho.lucashour.transmisortcpjoystick.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ciaa_poncho.lucashour.transmisortcpjoystick.R;
import com.ciaa_poncho.lucashour.transmisortcpjoystick.TCP.TcpSocketData;
import com.ciaa_poncho.lucashour.transmisortcpjoystick.TCP.TcpSocketManager;
import com.ciaa_poncho.lucashour.transmisortcpjoystick.GeneralGUI.ToastManager;

public class ConfigurationFragment extends Fragment implements View.OnClickListener {

    private EditText ip_address;
    private EditText port_number;
    private Button default_config_button;
    private Button modify_button;
    private Button connect_button;
    private Button disconnect_button;
    private Toast toast;

    public ConfigurationFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_config, container, false);

        if (v != null) {

            default_config_button = ((Button) v.findViewById(R.id.default_config_button));
            modify_button = ((Button) v.findViewById(R.id.modify_button));
            connect_button = ((Button) v.findViewById(R.id.connect_button));
            disconnect_button = ((Button) v.findViewById(R.id.disconnect_button));
            ip_address = ((EditText) v.findViewById(R.id.ip_address));
            ip_address.setText(TcpSocketData.getInstance().getIpAddress());
            port_number = ((EditText) v.findViewById(R.id.port_number));
            port_number.setText(String.valueOf(TcpSocketData.getInstance().getPortNumber()));
            toast = new Toast(getActivity().getApplicationContext());
        }

        return v;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        default_config_button.setOnClickListener(this);
        modify_button.setOnClickListener(this);
        connect_button.setOnClickListener(this);
        disconnect_button.setOnClickListener(this);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false); //Indicamos que este Fragment no tiene su propio menu de opciones
    }

    /* CLick listener */

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connect_button:
                ToastManager.displayInformationMessage(TcpSocketManager.connectToSocket(), toast, getActivity());
                break;
            case R.id.disconnect_button:
                ToastManager.displayInformationMessage(TcpSocketManager.disconnectFromSocket(), toast, getActivity());
                break;
            case R.id.default_config_button:
                ip_address.setText("192.168.4.1");
                port_number.setText("8080");
                break;
            case R.id.modify_button:
                if (ip_address.getText().toString().isEmpty() || port_number.getText().toString().isEmpty())
                    Toast.makeText(view.getContext(), "Error al configurar datos", Toast.LENGTH_SHORT).show();
                else{
                    TcpSocketData.getInstance().setIpAddress(ip_address.getText().toString());
                    TcpSocketData.getInstance().setPortNumber(Integer.parseInt(port_number.getText().toString()));
                    Toast.makeText(view.getContext(), "Datos configurados correctamente", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}