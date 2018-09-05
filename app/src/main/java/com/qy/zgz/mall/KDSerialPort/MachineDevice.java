package com.qy.zgz.mall.KDSerialPort;

/**
 * Created by Administrator on 2018/4/2.
 */

public class MachineDevice {
    private int id = -1;
    private int machineKind = -1;
    private String device;
    private int baudrate = -1;
    private int parity = -1;
    private int dataBits = -1;
    private int stopBit = -1;
    private int motorKind = 3;

    public MachineDevice() {

    }

    public MachineDevice(int machineKind, String device, int baudrate, int parity, int dataBits, int stopBit, int motorKind) {
        this.machineKind = machineKind;
        this.device = device;
        this.baudrate = baudrate;
        this.parity = parity;
        this.dataBits = dataBits;
        this.stopBit = stopBit;
        this.motorKind = motorKind;
    }

    public MachineDevice(int id, int machineKind, String device, int baudrate, int parity, int dataBits, int stopBit, int motorKind) {
        this.id = id;
        this.machineKind = machineKind;
        this.device = device;
        this.baudrate = baudrate;
        this.parity = parity;
        this.dataBits = dataBits;
        this.stopBit = stopBit;
        this.motorKind = motorKind;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getBaudrate() {
        return baudrate;
    }

    public void setBaudrate(int baudrate) {
        this.baudrate = baudrate;
    }

    public int getParity() {
        return parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }

    public int getDataBits() {
        return dataBits;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public int getStopBit() {
        return stopBit;
    }

    public void setStopBit(int stopBit) {
        this.stopBit = stopBit;
    }

    public int getMachineKind() {
        return machineKind;
    }

    public void setMachineKind(int machineKind) {
        this.machineKind = machineKind;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMotorKind() {
        return motorKind;
    }

    public void setMotorKind(int motorKind) {
        this.motorKind = motorKind;
    }
}
