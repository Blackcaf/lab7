package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

import java.util.List;

public class RemoveFirst extends Command {
    private final CollectionManager collectionManager;
    private final DatabaseManager dbManager;
    private final Console console;

    public RemoveFirst(CollectionManager collectionManager, Console console, DatabaseManager dbManager) {
        super("remove_first", "удалить первый элемент из коллекции");
        this.collectionManager = collectionManager;
        this.dbManager = dbManager;
        this.console = console;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Выполняется команда: remove_first, userId: " + userId);
        if (userId == null) {
            return new ExecutionResponse(false, "Ошибка: пользователь не авторизован");
        }

        List<HumanBeing> collection = collectionManager.getCollection();
        if (collection.isEmpty()) {
            return new ExecutionResponse(false, "Коллекция пуста");
        }

        HumanBeing first = collection.stream()
                .filter(h -> h.getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        if (first == null) {
            return new ExecutionResponse(false, "Нет элементов, принадлежащих пользователю с ID " + userId);
        }

        boolean success = collectionManager.remove(first.getId(), userId);
        if (success) {
            return new ExecutionResponse(true, "Первый элемент успешно удален");
        } else {
            return new ExecutionResponse(false, "Ошибка при удалении первого элемента");
        }
    }
}