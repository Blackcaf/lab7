package commands;

import managers.CommandManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

public class Help extends Command {
    private final Console console;
    private final CommandManager commandManager;

    public Help(Console console, CommandManager commandManager) {
        super("help", "вывести справку по доступным командам");
        this.console = console;
        this.commandManager = commandManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Выполняется команда: help, userId: " + userId);
        StringBuilder response = new StringBuilder("Доступные команды:\n");
        commandManager.getCommands().forEach((name, command) ->
                response.append(name).append(": ").append(command.getDescription()).append("\n")
        );
        return new ExecutionResponse(true, response.toString());
    }
}