package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

import java.util.List;

public class RemoveLower extends Command {
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;
    private final Console console;

    public RemoveLower(CollectionManager collectionManager, Console console, DatabaseManager databaseManager) {
        super("remove_lower", "удалить из коллекции все элементы, меньшие, чем заданный");
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
        this.console = console;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Выполняется команда: remove_lower, userId: " + userId);
        if (argument == null) {
            return new ExecutionResponse(false, "Ошибка: объект HumanBeing не предоставлен");
        }
        if (userId == null) {
            return new ExecutionResponse(false, "Ошибка: пользователь не авторизован");
        }

        List<HumanBeing> toRemove = collectionManager.getCollection().stream()
                .filter(h -> h.getUserId().equals(userId) && h.compareTo(argument) < 0)
                .toList();

        boolean success = true;
        for (HumanBeing human : toRemove) {
            if (!collectionManager.remove(human.getId(), userId)) {
                success = false;
            }
        }

        if (success && !toRemove.isEmpty()) {
            return new ExecutionResponse(true, "Элементы, меньшие заданного, удалены");
        } else if (toRemove.isEmpty()) {
            return new ExecutionResponse(true, "Нет элементов, меньших заданного");
        } else {
            return new ExecutionResponse(false, "Ошибка при удалении элементов");
        }
    }
}