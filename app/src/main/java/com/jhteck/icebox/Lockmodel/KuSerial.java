package com.jhteck.icebox.Lockmodel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;
import ku.base.KuThread;

public class KuSerial {

    protected static String TAG = "Serial";

    protected int _receivedMax = 1024;              //单词接收数据上限
    protected long _timeActived = 0;
    protected long _timeoutActived = 5000;

    private int _baudrate = 9600;
    private String _port="";
    private SerialPort _sp;
    private OutputStream _out;
    private InputStream _in;
    private boolean _isOpened=false;
    private KuThread _rRead;

    public String port() { return _port; }
    public void port( String port ) { _port=port; }
    public int baudrate() { return _baudrate; }
    public void baudrate( int value ) { _baudrate=value; }

    protected KuSerial() {
        baudrate(9600);
    }
    protected KuSerial( String port ) {
        this();
        port(port);
    }
    public boolean isActivated() { return (_timeActived + _timeoutActived) > System.currentTimeMillis(); }
    public boolean isOpened() { return _isOpened; }
    public void open() throws Exception {
        close();
        _sp=new SerialPort(new File(_port), _baudrate, 0);
        _out=_sp.getOutputStream();
        _in=_sp.getInputStream();
        onOpened();
    }

    public void open( String port ) throws Exception {
        port(port);
        open();
    }
    public void open( String port, int baudrate ) throws Exception {
        baudrate(baudrate);
        open(port);
    }
    public void close() {
        if (_sp != null) _sp.close();
        _sp=null;
        onClosed();
    }
    public void write( byte[] data ) throws Exception {
        _out.write(data);
        _out.flush();
        onSent(data);
    }
    private void read() {
        try {
            if (_in.available() <= 0) return;
            _timeActived=System.currentTimeMillis();
            byte[] b = new byte[_receivedMax];
            int len = _in.read(b);
            byte[] datas = new byte[len];
            System.arraycopy(b, 0, datas, 0, len);
            onReceived(datas);
        } catch (IOException ex) {
            onError(ex);
        }
    }

    protected void onOpened(){
        if (_rRead == null)_rRead = new KuThread();
        _rRead.loop(() -> read(), 100);
        _isOpened = true;
    }
    protected void onClosed(){
        if (_rRead != null)
            _rRead.stopLoop();
        _isOpened = false;
    }
    protected void onReceived( byte[] data ) {}
    protected void onSent( byte[] data ) {}
    protected void onError( Exception ex ) {}
}

