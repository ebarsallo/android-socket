package edu.purdue.socket;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    private static final String TAG = "SocketServer";

    private static ServerSocket server;
    private static int port = 7330;
    boolean accepting = false;
    Socket socket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();

        Log.d(TAG, "Instantiating server thread");
        try {
            server = new ServerSocket(port);
            new AcceptingThreads().start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    void disconnect() {
        try {
            Log.d(TAG, "Closing socket");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class AcceptingThreads extends Thread {

        @SuppressWarnings("InfiniteLoopStatement")
        @Override
        public void run() {

            try {

                accepting = true;
                Log.d(TAG, String.format("Start accepting socket clients {%b}", accepting));
                while (accepting) {
                    Log.d(TAG, "in the loop");
                    socket = server.accept();
                    Log.d(TAG, String.format("Accepted socket client: {%d}",
                            socket.getReceiveBufferSize()));

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream())
                    );
                    String line;

                    if (in.readLine() == null)
                        Log.d(TAG, "No data received!");

                    while ((line = in.readLine()) != null) {
                        Log.d(TAG, String.format(" => %s", line));
                    }
                    if (line == null)
                        Log.d(TAG, "No more data");
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                disconnect();
            }

        }
    }

}
