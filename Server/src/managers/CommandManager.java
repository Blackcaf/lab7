package managers;

import commands.*;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final Console console;

    public CommandManager(Console console, CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.console = console;
        commands.put("add", new Add(collectionManager));
        commands.put("update", new Update(console, collectionManager, databaseManager));
        commands.put("remove_head", new RemoveHead(collectionManager, console, databaseManager));
        commands.put("clear", new Clear(console, collectionManager, databaseManager));
        commands.put("info", new Info(console, collectionManager));
        commands.put("show", new Show(console, collectionManager));
        commands.put("login", new Login(databaseManager, console));
        commands.put("register", new Register(databaseManager, console));
        commands.put("exit", new Exit(console));
        commands.put("help", new Help(console, this));
        commands.put("filter_starts_with_name", new FilterStartsWithName(console, collectionManager));
        commands.put("print_unique_impact_speed", new PrintUniqueImpactSpeed(console, collectionManager));
        commands.put("count_less_than_impact_speed", new CountLessThanImpactSpeed(console, collectionManager));
        commands.put("execute_script", new ExecuteScript(console, this));
        commands.put("removebyid", new RemoveById(console, collectionManager));
    }

    public ExecutionResponse executeCommand(String commandName, HumanBeing humanBeing, Integer userId) {
        Command command = commands.get(commandName);
        if (command == null) {
            return new ExecutionResponse(false, "Команда '" + commandName + "' не найдена. Введите 'help' для списка команд.");
        }
        return command.execute(humanBeing, userId);
    }

    public Map<String, Command> getCommands() {
        return commands;
    }
}