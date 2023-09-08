package com.jhteck.icebox.Lockmodel;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ku.base.KuThread;

public class KuProcess {
    public boolean isRoot() {
        return new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
    }

    public void execute( String... cmds) throws Exception {
        Process process = null;
        DataOutputStream dos = null;
        try{
            process = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(process.getOutputStream());
            for (String tmp : cmds) dos.writeBytes(tmp + "  \n");
            dos.writeBytes("exit\n");
            dos.flush();
        } finally {
            if (dos != null) dos.close();
            if (process != null) process.destroy();
        }
    }
    public String executeHasResult(String... cmds) throws Exception {
        Process process = null;
        DataOutputStream dos = null;
        int ret = -1;
        try {
            ProcessResult cb = new ProcessResult();
            process = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(process.getOutputStream());
            for (String tmp : cmds) dos.writeBytes(tmp + "  \n");
            dos.flush();
            dos.close();                        // 不关闭该输出流将导致下一步阻塞
            ret = process.waitFor();
            cb.getResult(process);
            return cb.result;
        } catch (Exception ex) {
            if (dos != null) dos.close();
            String msg = String.format("execute %s failed, code:%d, info:%s",
                    cmds, ret, ex.getMessage());
            Log.e("process: ,", msg);
            throw(new Exception(msg));
        } finally {
            if (process != null) process.destroy();
        }
    }
    public String executeWaitResult(String cmd) throws Exception {
        Process process = null;
        int ret = -1;
        try {
            process = Runtime.getRuntime().exec(cmd);
            ProcessResult cb = new ProcessResult();
            ret = process.waitFor();
            cb.getResult(process);
            if (ret != 0) throw new Exception(cb.error);
            return cb.result;
        } catch (Exception ex) {
            String msg = String.format("execute %s failed, code:%d, info:%s",
                    cmd, ret, ex.getMessage());
            Log.e("process: ,", msg);
            throw(new Exception(msg));
        } finally {
            if (process != null) process.destroy();
        }
    }


    public static class ProcessResult {
        String result = "", error = "";
        boolean flag = false;
        public void invoke(Process p){
            new KuThread().run(() -> {
                flag = true;
                String s = "";
                try{
                    while(flag){
                        getResult(p);
                        Thread.sleep(10);
                    }
                } catch (Exception ex){
                    error = ex.getMessage();
                }
            });
        }
        public void stop(){
            flag = false;
        }

        public void getResult(Process p) throws IOException {
            String s;
            s = execResult(p.getInputStream());
            if (!s.isEmpty())
                result = s;
            s = execResult(p.getErrorStream());
            if (!s.isEmpty())
                error = s;
        }

        private String execResult( InputStream stream ) throws IOException {
            DataInputStream is = new DataInputStream(stream);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String s = new String(buffer);
            return fixResult(s);
        }
        private String fixResult(String s) {
            int i1, i2, temp = 0;
            temp = s.indexOf("\r\r\n");
            i1 = (temp > 0) ? (temp + 3) : 0;
            temp = s.lastIndexOf("root@");
            i2 = (temp > 0) ? temp : s.length();
            s = s.substring(i1, i2);
            return s;
        }
    }
}
