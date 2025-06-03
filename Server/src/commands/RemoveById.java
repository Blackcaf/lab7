package commands;

import managers.CollectionManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

public class RemoveById extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public RemoveById(Console console, CollectionManager collectionManager) {
        super("removebyid", "удалить элемент из коллекции по ID");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        try {
            if (userId == null) {
                return new ExecutionResponse(false, "Требуется авторизация");
            }

            Long id;
            if (argument == null || argument.getName() == null || argument.getName().trim().isEmpty()) {
                return new ExecutionResponse(false, "Требуется указать ID элемента");
            }

            try {
                id = Long.parseLong(argument.getName().trim());
            } catch (NumberFormatException e) {
                return new ExecutionResponse(false, "ID должен быть числом");
            }

            if (id <= 0) {
                return new ExecutionResponse(false, "ID должен быть положительным числом");
            }

            if (collectionManager.remove(id, userId)) {
                return new ExecutionResponse(true, "Элемент с ID " + id + " успешно удален");
            } else {
                return new ExecutionResponse(false, "Элемент с ID " + id + " не найден или у вас нет прав на его удаление");
            }
        } catch (Exception e) {
            return new ExecutionResponse(false, "Ошибка при удалении элемента: " + e.getMessage());
        }
    }
}