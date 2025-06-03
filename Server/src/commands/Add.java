package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

public class Add extends Command {
    private final CollectionManager collectionManager;
    private final DatabaseManager dbManager;
    private final Console console;

    public Add(Console console, CollectionManager collectionManager, DatabaseManager dbManager) {
        super("add", "добавить новый элемент в коллекцию");
        this.collectionManager = collectionManager;
        this.dbManager = dbManager;
        this.console = console;
    }

    @Override
    public ExecutionResponse execute(HumanBeing humanBeing, Integer userId) {
        console.println("Выполняется команда: add, userId: " + userId);
        if (humanBeing == null) {
            return new ExecutionResponse(false, "Ошибка: объект не был создан. Попробуйте снова.");
        }
        if (userId == null) {
            return new ExecutionResponse(false, "Ошибка: пользователь не авторизован");
        }

        console.println("Добавление элемента: " + humanBeing + ", начальный id: " + humanBeing.getId());
        boolean success = collectionManager.add(humanBeing, userId);
        if (success) {
            console.println("Элемент добавлен с id: " + humanBeing.getId());
            return new ExecutionResponse(true, "Элемент успешно добавлен с id: " + humanBeing.getId());
        } else {
            console.println("Ошибка добавления: id остался " + humanBeing.getId());
            return new ExecutionResponse(false, "Ошибка добавления: не удалось сохранить элемент");
        }
    }
}