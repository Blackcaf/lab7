package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

public class Update extends Command {
    private final Console console;
    private final CollectionManager collectionManager;
    private final DatabaseManager dbManager;

    public Update(Console console, CollectionManager collectionManager, DatabaseManager dbManager) {
        super("update", "обновить элемент коллекции по его id");
        this.console = console;
        this.collectionManager = collectionManager;
        this.dbManager = dbManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing humanBeing, Integer userId) {
        console.println("Выполняется команда: update, userId: " + userId);
        
        if (humanBeing == null) {
            return new ExecutionResponse(false, "Ошибка: объект HumanBeing не предоставлен");
        }
        
        if (userId == null) {
            return new ExecutionResponse(false, "Ошибка: пользователь не авторизован");
        }

        Long id = humanBeing.getId();
        if (id == null) {
            return new ExecutionResponse(false, "Ошибка: id не указан");
        }
        
        if (id <= 0) {
            return new ExecutionResponse(false, "Ошибка: id должен быть положительным числом");
        }

        // Check if the element exists and belongs to the user
        HumanBeing existingHuman = collectionManager.getCollectionMap().get(id);
        if (existingHuman == null) {
            return new ExecutionResponse(false, "Ошибка: элемент с указанным id не найден");
        }

        if (!existingHuman.getUserId().equals(userId)) {
            return new ExecutionResponse(false, "Ошибка: вы не можете изменить элемент, созданный другим пользователем");
        }

        // Update the element
        boolean success = collectionManager.update(id, humanBeing, userId);
        if (success) {
            return new ExecutionResponse(true, "Элемент успешно обновлен");
        } else {
            return new ExecutionResponse(false, "Ошибка при обновлении элемента");
        }
    }
}