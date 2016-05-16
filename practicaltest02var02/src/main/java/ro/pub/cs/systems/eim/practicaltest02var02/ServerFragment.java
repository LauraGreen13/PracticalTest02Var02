package ro.pub.cs.systems.eim.practicaltest02var02;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ro.pub.cs.systems.eim.practicaltest02var02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02var02.general.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFragment extends Fragment {
    private EditText serverTextEditText;

    private Button connectButton;

    private EditText serverPortEditText;


    private class CommunicationThread extends Thread {

        private Socket socket;
//        WeatherInfo weatherInfo = new WeatherInfo();

        public CommunicationThread(Socket socket) {
            if (socket != null) {
                this.socket = socket;
                Log.d(Constants.TAG, "[SERVER] Created communication thread with: " + socket.getInetAddress() + ":" + socket.getLocalPort());
            }
        }

        @Override
        public void run() {
            try {

                /////////////////////
//                if (socket == null) {
//                    return;
//                }
//                boolean isRunning = true;
//                InputStream requestStream = null;
//                OutputStream responseStream = null;
//                try {
//                    requestStream = socket.getInputStream();
//                } catch (IOException ioException) {
//                    Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
//                    if (Constants.DEBUG) {
//                        ioException.printStackTrace();
//                    }
//                }
                BufferedReader bufferedReader = Utilities.getReader(socket);
                int op1 = Integer.valueOf(bufferedReader.readLine());
                int op2 = Integer.valueOf(bufferedReader.readLine());
                String operation = bufferedReader.readLine();

                int result = 0;

                if (operation.equals("add")) {
                    result = op1 + op2;
                } else {
                    Thread.sleep(5000);
                    result = op1 * op2;
                }

                //////////////////////
//                Log.v(Constants.TAG, "Connection opened with " + socket.getInetAddress() + ":" + socket.getLocalPort());
                PrintWriter printWriter = Utilities.getWriter(socket);
                printWriter.println("Result is: " + result);
                socket.close();
                Log.v(Constants.TAG, "Connection closed");
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            } catch (InterruptedException e) {
                Log.e(Constants.TAG, "Sleep ERROR" + e.getMessage());
            }
        }
    }

    private ServerThread serverThread;

    private class ServerThread extends Thread {

        private boolean isRunning;

        private ServerSocket serverSocket;

        public void startServer() {
            isRunning = true;
            start();
            Log.v(Constants.TAG, "startServer() method invoked");
        }

        public void stopServer() {
            isRunning = false;
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
            Log.v(Constants.TAG, "stopServer() method invoked");
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(Constants.SERVER_PORT);
                while (isRunning) {
                    Socket socket = serverSocket.accept();
                    if (socket != null) {
                        CommunicationThread communicationThread = new CommunicationThread(socket);
                        communicationThread.start();
                    }
                }
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        return inflater.inflate(R.layout.server_fragment, parent, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        serverPortEditText = (EditText) getActivity().findViewById(R.id.server_port_edit_text);
        connectButton = (Button)getActivity().findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverThread = new ServerThread();
                serverThread.startServer();
                Log.v(Constants.TAG, "Starting server...");
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serverThread != null) {
            serverThread.stopServer();
        }
    }

}