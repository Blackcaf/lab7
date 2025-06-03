package server;

import managers.CommandManager;
import models.HumanBeing;
import utility.ExecutionResponse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final int port;
    private final CommandManager commandManager;

    public Server(int port, CommandManager commandManager) {
        this.port = port;
        this.commandManager = commandManager;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту " + port);
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Клиент подключен: " + clientSocket.getInetAddress());
                    handleClient(clientSocket);
                } catch (IOException e) {
                    System.err.println("Ошибка обработки клиента: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        try (ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream())) {

            while (!clientSocket.isClosed()) {
                try {
                    // Read command and data
                    String commandName = (String) input.readObject();
                    HumanBeing humanBeing = null;
                    Integer userId = null;

                    try {
                        humanBeing = (HumanBeing) input.readObject();
                        userId = (Integer) input.readObject();
                    } catch (InvalidClassException e) {
                        System.err.println("Критическая ошибка в handleClient: " + e.getMessage());
                        output.writeObject(new ExecutionResponse(false, "Ошибка десериализации: " + e.getMessage()));
                        continue;
                    }

                    System.out.println("Получен запрос: command=" + commandName + ", userId=" + userId);
                    if (humanBeing != null) {
                        System.out.println("Аргумент: " + humanBeing.getName());
                    }

                    // Execute command
                    ExecutionResponse response = commandManager.executeCommand(commandName, humanBeing, userId);

                    // Send response
                    output.writeObject(response);
                    output.flush();

                    if (commandName.equals("exit")) {
                        System.out.println("Клиент отключен: " + clientSocket.getInetAddress());
                        break;
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Ошибка чтения данных: " + e.getMessage());
                    output.writeObject(new ExecutionResponse(false, "Ошибка чтения данных: " + e.getMessage()));
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка обработки клиента: " + e.getMessage());
            throw e;
        }
    }
}