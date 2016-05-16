package ro.pub.cs.systems.eim.practicaltest02var02;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ro.pub.cs.systems.eim.practicaltest02var02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02var02.general.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientFragment extends Fragment {

    private EditText serverPortEditText;
    private EditText op1, op2;
    private TextView serverMessageTextView;
    private Button addButton;
    private Button multiplyButton;

    private class ClientThread extends Thread {
        private Socket socket;
        String operation = "";

        public ClientThread(String operation) {
            this.operation = operation;
        }

        @Override
        public void run() {
            try {
                socket = new Socket("localhost", Integer.valueOf(serverPortEditText.getText().toString()));
                if (socket == null) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                }

                PrintWriter printWriter = Utilities.getWriter(socket);
                printWriter.println(op1.getText());
                printWriter.println(op2.getText());
                printWriter.println(operation);


                Log.v(Constants.TAG, "Connection opened with " + socket.getInetAddress() + ":" + socket.getLocalPort());
                final BufferedReader bufferedReader = Utilities.getReader(socket);


                String weatherInformation;
                while ((weatherInformation = bufferedReader.readLine()) != null) {
                    final String finalizedWeatherInformation = weatherInformation;
                    serverMessageTextView.post(new Runnable() {
                        @Override
                        public void run() {

                            serverMessageTextView.append(finalizedWeatherInformation + "\n");
                        }
                    });
                }

                    socket.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + e.getMessage());
            }
        }

//        @Override
//        protected Void doInBackground(String... params) {
//            Socket socket = null;
//            try {
//                String serverAddress = params[0];
//                int serverPort = Integer.parseInt(params[1]);
//                socket = new Socket("localhost", serverPort);
//                if (socket == null) {
//                    return null;
//                }
//
//                PrintWriter printWriter = Utilities.getWriter(socket);
//                printWriter.println(op1.getText());
//                printWriter.println(op2.getText());
//                printWriter.println(params[2]);
//
//
//                Log.v(Constants.TAG, "Connection opened with " + socket.getInetAddress() + ":" + socket.getLocalPort());
//                BufferedReader bufferedReader = Utilities.getReader(socket);
//                String currentLine;
//                while ((currentLine = bufferedReader.readLine()) != null) {
//                    publishProgress(currentLine);
//                }
//            } catch (IOException ioException) {
//                Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
//                if (Constants.DEBUG) {
//                    ioException.printStackTrace();
//                }
//            } finally {
//                try {
//                    if (socket != null) {
//                        socket.close();
//                    }
//                    Log.v(Constants.TAG, "Connection closed");
//                } catch (IOException ioException) {
//                    Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
//                    if (Constants.DEBUG) {
//                        ioException.printStackTrace();
//                    }
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
////            serverMessageTextView.setText("");
//        }
//
//        @Override
//        protected void onProgressUpdate(String... progress) {
//            serverMessageTextView.append(progress[0] + "\n");
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        return inflater.inflate(R.layout.client_fragment, parent, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

//        serverAddressEditText = (EditText) getActivity().findViewById(R.id.server_address_edit_text);
        serverPortEditText = (EditText) getActivity().findViewById(R.id.server_port_edit_text);
        serverMessageTextView = (TextView) getActivity().findViewById(R.id.server_message_text_view);

        op1 = (EditText) getActivity().findViewById(R.id.first_operator);
        op2 = (EditText) getActivity().findViewById(R.id.second_operator);

        addButton = (Button) getActivity().findViewById(R.id.add_button);
        addButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClientThread clientAsyncTask = new ClientThread("add");
                clientAsyncTask.start();
            }
        });

        multiplyButton = (Button) getActivity().findViewById(R.id.multiply_button);
        multiplyButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClientThread clientAsyncTask = new ClientThread("multiply");
                clientAsyncTask.start();
            }
        });

    }


}