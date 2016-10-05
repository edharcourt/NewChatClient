package com.example.edharcourt.newchatclient;


import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by edharcourt on 10/4/16.
 */

public class ConnectionManager implements Runnable {

    MainActivity act = null;
    Handler uiThread = null;
    String ipaddr = null;

    Socket sock = null;
    BufferedReader fromServer = null;
    PrintWriter toServer = null;

    public ConnectionManager(MainActivity act, Handler uiThread, String ipaddr) {
        this.act = act;
        this.uiThread = uiThread;
        this.ipaddr = ipaddr;
    }

    @Override
    public void run() {

        uiThread.post(new Runnable() {
            @Override
            public void run() {
                act.receiving.append(act.getString(R.string.waiting));
            }
        });

        try {
            sock = new Socket(ipaddr, 12346);
            fromServer =
                new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));

            toServer = new PrintWriter(sock.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
            try {
                sock.close();
                fromServer.close();
                toServer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        finally {

        }
    }

    Socket getSocket() { return sock; }


}
