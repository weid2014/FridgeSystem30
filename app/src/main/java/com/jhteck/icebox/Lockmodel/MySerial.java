package com.jhteck.icebox.Lockmodel;



public class MySerial extends KuSerial {

    public Listener listener;

    public String[] list() throws Exception {
//        String cmd = "ls /dev|grep \"ttymxc\"";
        String cmd = "ls /dev|grep -E \"ttyS|ttyGS|ttyUSB|ttymxc\"";
//        String cmd = "sh -c \"su && ls /dev | grep -E \\\"ttyS|ttyGS|ttyUSB|ttymxc\\\"\"";
        String text = new KuProcess().executeHasResult(cmd);
        String[] arr = text.split("\n");
        for(int i = 0; i < arr.length; i++)
            arr[i] =  "/dev/" + arr[i].replace("\r", "");
        return arr;
    }

    @Override
    protected void onReceived( byte[] datas ) {
        super.onReceived(datas);
        listener.onReceived(datas);
    }

    @Override
    protected void onSent( byte[] datas ) {
        super.onSent(datas);
        listener.onSent(datas);
    }

    public interface Listener{
        void onReceived(byte[] datas);
        void onSent(byte[] datas);
    }
}
