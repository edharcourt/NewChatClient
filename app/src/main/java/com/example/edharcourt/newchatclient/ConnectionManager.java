package com.example.edharcourt.newchatclient;


import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by edharcourt on 10/4/16.
 */

public class ConnectionManager {

    private final String LOG_TAG = ConnectionManager.class.getName();

    MainActivity act = null;
    Handler uiThread = null;

    // If not null then this is the IP address
    // of the device we "called".
    String ipaddr = null;

    Socket sock = null;
    ServerSocket server_sock = null;

    BufferedReader from = null;
    PrintWriter to = null;

    boolean acting_as_server = false;
    boolean connected = false;

    /**
     *
     * @param act - Activity that created this Connection
     * @param uiThread - handler attached to the UI thread
     */
    public ConnectionManager(MainActivity act, Handler uiThread) {
        this.connected = false;
        this.act = act;
        this.uiThread = uiThread;
        this.ipaddr = null;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    Runnable server_thread = new Runnable() {

        @Override
        public void run() {

            try {

                uiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        act.receiving.append(act.getString(R.string.waiting));
                    }
                });

                server_sock = new ServerSocket(12347);
                sock = server_sock.accept();
                from = new BufferedReader(
                        new InputStreamReader(sock.getInputStream()));
                to = new PrintWriter(sock.getOutputStream(), true);
            }
            catch (SocketException e) {
                Log.w(LOG_TAG, e.getMessage());

                // fine, might have been closed because we
                // are running as the client and not the server
                if (connected)
                    return;
                else
                    Toast.makeText(act, "Something bad happened", Toast.LENGTH_LONG).show();
            }
            catch (IOException e) {
                Log.w(LOG_TAG, e.getMessage());
                uiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        act.receiving.append("Error creating server socket.");
                    }
                });
                e.printStackTrace();
                if (server_sock.isBound()) {
                    try {
                        server_sock.close();
                        sock.close();
                        from.close();
                        to.close();
                    } catch (IOException e1) {
                        Log.w(LOG_TAG, e1.getMessage());
                        uiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                act.receiving.append("Connection closed");
                            }
                        });
                        e1.printStackTrace();
                    }
                }
                return;
            }

            uiThread.post(new Runnable() {
                @Override
                public void run() {
                    act.receiving.append(act.getString(R.string.connection_established));
                }
            });

            acting_as_server = true;
            connected = true;
            synchronized (ConnectionManager.this) {
                ConnectionManager.this.notifyAll();
            }
        }
    };

    Runnable client_thread = new Runnable() {
        @Override
        public void run() {

            if (ipaddr == null)
                return;

            uiThread.post(new Runnable() {
                @Override
                public void run() {
                    act.receiving.append(act.getString(R.string.waiting));
                }
            });

            try {
                sock = new Socket(ipaddr, 12347);
                from = new BufferedReader(
                           new InputStreamReader(sock.getInputStream()));

                to = new PrintWriter(sock.getOutputStream(), true);

            } catch (IOException e) {

                Log.w(LOG_TAG, e.getMessage());
                uiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        act.receiving.append(act.getString(R.string.error_connecting));
                    }
                });

                e.printStackTrace();
                try {
                    sock.close();
                    from.close();
                    to.close();
                } catch (IOException e1) {
                    Log.w(LOG_TAG, e1.getMessage());
                }
                return;
            }

            uiThread.post(new Runnable() {
                @Override
                public void run() {
                    act.receiving.append(act.getString(R.string.connection_established));
                }
            });

            acting_as_server = false;
            connected = true;
            synchronized (ConnectionManager.this) {
                ConnectionManager.this.notifyAll();
            }
        }
    };

    Runnable reader = new Runnable() {
        @Override
        public void run() {
            while (!connected)
                synchronized (ConnectionManager.this) {
                    try {
                        ConnectionManager.this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            Log.v(LOG_TAG, "reader thread started");

            while (true) {

                try {
                    final String line = from.readLine();
                    uiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            act.receiving.append(line);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    Runnable writer = new Runnable() {
        @Override
        public void run() {
            while (!connected)
                synchronized (ConnectionManager.this) {
                    try {
                        ConnectionManager.this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            Log.v(LOG_TAG, "writer thread started");
            int i = 0;

            while (true) {
                // generate between a 0.5 sec and 2.5 sec delay
                int delay = (int) (Math.random()*2000) + 500;
                try {
                    Thread.sleep(delay);
                    to.println(i++);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


}
