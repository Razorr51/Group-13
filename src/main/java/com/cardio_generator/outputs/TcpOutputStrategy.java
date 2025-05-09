package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * {@code TcpOutputStrategy} is an implementation of the {@code OutputStrategy} interface that sends data
 * over a TCP connection. It starts a TCP server on a specified port and waits for a client to connect.
 * Once a client is connected, it transmits formatted patient data to the client.
 *
 * <p>Data is formatted as: {@code patientId,timestamp,label,data}
 * <p>The server accepts a single client connection in a separate thread to avoid blocking the main thread.
 *
 */

public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Prints out all filled data
     *
     * @param patientId the unique ID of the patient
     * @param timestamp the time when the data was generated in milliseconds
     * @param label the type or category of health data (e.g., "ECG", "Blood Pressure")
     * @param data the actual health data in string format
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
