package client;

import models.HumanBeing;
import models.Car;
import utility.Console;
import utility.ExecutionResponse;
import utility.Request;
import utility.HumanBeingAsker;
import utility.StandardConsole;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;
    private static Console console = new StandardConsole();

    public void run() {
        Socket socket = null;
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        Scanner scannerObj = new Scanner(System.in);
        Integer userId = null;

        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            console.println("Подключено к серверу: " + SERVER_HOST + ":" + SERVER_PORT);

            while (true) {
                console.println("Введите команду: ");
                String commandName = console.read().trim();
                HumanBeing humanBeing = null;

                if (commandName.equals("add")) {
                    try {
                        HumanBeingAsker asker = new HumanBeingAsker(console);
                        humanBeing = asker.askHumanBeing();
                    } catch (Exception e) {
                        console.println("Ошибка при создании объекта: " + e.getMessage());
                        continue;
                    }
                } else if (commandName.equals("update")) {
                    console.println("Введите id элемента для обновления (целое положительное число):");
                    String idInput = console.read().trim();
                    Long id;
                    try {
                        id = Long.parseLong(idInput);
                        if (id <= 0) {
                            console.println("Ошибка: id должен быть положительным числом");
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        console.println("Ошибка: введите корректное целое число для id");
                        continue;
                    }
                    try {
                        HumanBeingAsker asker = new HumanBeingAsker(console);
                        humanBeing = asker.askHumanBeing();
                        humanBeing.setId(id);
                    } catch (Exception e) {
                        console.println("Ошибка при создании объекта: " + e.getMessage());
                        continue;
                    }
                }

                output.writeObject(commandName);
                output.writeObject(humanBeing);
                output.writeObject(userId);
                output.flush();

                ExecutionResponse response = (ExecutionResponse) input.readObject();
                console.println(response.getMessage());

                if (("login".equals(commandName) || "register".equals(commandName)) && response.isSuccess()) {
                    userId = Integer.parseInt(response.getMessage().replaceAll(".*ID: (\\d+).*", "$1"));
                    console.println("Установлен userId: " + userId);
                }

                if (commandName.equals("exit")) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            console.println("Ошибка: " + e.getMessage());
        } finally {
            try {
                if (output != null) output.close();
                if (input != null) input.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                console.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}