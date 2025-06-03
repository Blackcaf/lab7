package commands;

import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;
import managers.CommandManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class ExecuteScript extends Command {
    private final Console console;
    private final CommandManager commandManager;
    private final Set<String> executedFiles = new HashSet<>();

    public ExecuteScript(Console console, CommandManager commandManager) {
        super("execute_script", "исполнить скрипт из файла");
        this.console = console;
        this.commandManager = commandManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing humanBeing, Integer userId) {
        if (humanBeing == null || humanBeing.getName() == null || humanBeing.getName().trim().isEmpty()) {
            return new ExecutionResponse(false, "Требуется путь к файлу скрипта");
        }

        String scriptPath = humanBeing.getName().trim();
        console.println("Получен аргумент: " + scriptPath);

        try {
            String absolutePath = Paths.get(scriptPath).toAbsolutePath().toString();
            console.println("Путь к скрипту: " + scriptPath);
            console.println("Абсолютный путь: " + absolutePath);

            if (executedFiles.contains(absolutePath)) {
                return new ExecutionResponse(false, "Обнаружена рекурсия: файл " + scriptPath + " уже выполняется");
            }

            executedFiles.add(absolutePath);
            StringBuilder scriptOutput = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new FileReader(absolutePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;

                    String[] parts = line.split("\\s+", 2);
                    String commandName = parts[0].toLowerCase();
                    HumanBeing commandHumanBeing = null;

                    if (commandName.equals("execute_script")) {
                        commandHumanBeing = new HumanBeing();
                        commandHumanBeing.setName(parts.length > 1 ? parts[1] : "");
                    } else if (parts.length > 1) {
                        commandHumanBeing = new HumanBeing();
                        commandHumanBeing.setName(parts[1]);
                    }

                    scriptOutput.append("Выполняется команда: ").append(line).append(", userId: ").append(userId).append("\n");
                    ExecutionResponse response = commandManager.executeCommand(commandName, commandHumanBeing, userId);
                    scriptOutput.append(response.getMessage()).append("\n");
                }
            } finally {
                executedFiles.remove(absolutePath);
            }

            return new ExecutionResponse(true, scriptOutput.toString().trim());
        } catch (IOException e) {
            return new ExecutionResponse(false, "Ошибка при чтении файла: " + e.getMessage());
        }
    }
}