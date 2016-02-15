package com.ciaa_poncho.lucashour.transmisortcpjoystick;

public class GlobalData {

    private static GlobalData singleton_instance = null;
    private int time;
    private int slots;
    private int [][] rpmCurves;
    private int max_rpm_motor0 = 0;
    private int max_rpm_motor1 = 0;
    private int max_pwm_motor0 = 0;
    private int max_pwm_motor1 = 0;
    private int max_rpm = 0;

    static final int CANT_MOTORES = 2;
    static final int NIVELES_PWM = 200;

    protected GlobalData(){
        /* Aplicación de patrón Singleton para mantener una única instancia de la clase
		 * accesible desde cualquier sector de la applicación */
    }

    public static GlobalData getInstance() {
        if(singleton_instance == null) {
            singleton_instance = new GlobalData();
            singleton_instance.setRpmCurves(new int [CANT_MOTORES][NIVELES_PWM]);
        }
        return singleton_instance;
    }

    public int getTime() { return time; }

    public void setTime(int time) { this.time = time; }

    public int getSlots() {return slots; }

    public void setSlots(int slots) { this.slots = slots; }

    public int[][] getRpmCurves() { return rpmCurves; }

    public void setRpmCurves(int[][] rpmCurves) { this.rpmCurves = rpmCurves; }

    public int getMax_rpm_motor0() {return max_rpm_motor0; }

    public void setMax_rpm_motor0(int rpm) { this.max_rpm_motor0 = rpm; }

    public int getMax_rpm_motor1() {return max_rpm_motor1; }

    public void setMax_rpm_motor1(int rpm) { this.max_rpm_motor1 = rpm; }


    // In the following function the system maximum RPM is set. Also, it is calculated and stored the
    // maximum value of PWM for each motor.
    public void setMax_RPM(){
        if (max_rpm_motor0 > max_rpm_motor1) {
            max_rpm = max_rpm_motor1;
            max_pwm_motor1 = 100;
            max_pwm_motor0 = calculatePWM(0);
        }else {
            max_rpm = max_rpm_motor0;
            max_pwm_motor0 = 100;
            max_pwm_motor1 = calculatePWM(1);
        }
    }

    public int getMax_RPM(){ return this.max_rpm; }

    private int calculatePWM(int motor){
        int pwm = 0;

        if (motor == 0){
            for(int i = 0; i < 100; i++){
                if (GlobalData.getInstance().getRpmCurves()[0][i+1]<= max_rpm)
                    pwm = i+1;
            }
        }else{
            for(int i = 0; i < 100; i++){
                if (GlobalData.getInstance().getRpmCurves()[1][i+1]<= max_rpm)
                    pwm = i+1;
            }
        }

        return pwm;
    }

    public int getMax_pwm_motor0(){ return this.max_pwm_motor0; }

    public int getMax_pwm_motor1(){ return this.max_pwm_motor1; }

}