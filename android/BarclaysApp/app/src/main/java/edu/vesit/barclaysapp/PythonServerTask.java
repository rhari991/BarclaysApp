package edu.vesit.barclaysapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InterfaceAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class PythonServerTask extends AsyncTask<Void, Void, Void> {

    String address;
    int port;
    String message;
    String response;

    public PythonServerTask(String message) {
        address = MainActivity.getStringResource(R.string.server_address);
        port = Integer.parseInt(MainActivity.getStringResource(R.string.server_port));
        this.message = message;
        Log.e("Sending ", message);
    }

    @Override
    protected Void doInBackground(Void... params) {
        Socket socket = null;
        try {
            socket = new Socket(address, port);
            DataOutputStream writeOut = new DataOutputStream(socket.getOutputStream());
            writeOut.writeUTF(message);
            writeOut.flush();

            ByteArrayOutputStream writeBuffer = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream writeIn = socket.getInputStream();

            while((bytesRead = writeIn.read(buffer)) != -1) {
                writeBuffer.write(buffer,0,bytesRead);
                response += writeBuffer.toString("UTF-8");
            }
            if(response != null)
                response = response.substring(4);   //Server sends extra "Null" string in front of data. This cuts it out
        } catch (UnknownHostException e){
            e.printStackTrace();
            response = "Unknown HostException: " + e.toString();
            System.out.println(response);
        } catch (IOException e) {
            response = "IOException: " + e.toString();
            System.out.println(response);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.e("response ", "" + response);
        EventBus.getDefault().post(new ServerResponseEvent(response));
        super.onPostExecute(result);
    }
}