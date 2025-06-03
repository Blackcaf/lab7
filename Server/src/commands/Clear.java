package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

public class Clear extends Command {
    private final Console console;
    private final CollectionManager collectionManager;
    private final DatabaseManager dbManager;

    public Clear(Console console, CollectionManager collectionManager, DatabaseManager dbManager) {
        super("clear", "очистить коллекцию");
        this.console = console;
        this.collectionManager = collectionManager;
        this.dbManager = dbManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Выполняется команда: clear, userId: " + userId);
        if (userId == null) {
            return new ExecutionResponse(false, "Ошибка: пользователь не авторизован");
        }

        boolean success = collectionManager.clear(userId);
        if (success) {
            return new ExecutionResponse(true, "Коллекция успешно очищена для пользователя с ID " + userId);
        } else {
            return new ExecutionResponse(false, "Ошибка при очистке коллекции");
        }
    }
}