package commands;

import models.HumanBeing;
import utility.ExecutionResponse;
import managers.CollectionManager;

public class Add extends Command {
    private final CollectionManager collectionManager;

    public Add(CollectionManager collectionManager) {
        super("add", "добавить новый элемент в коллекцию");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing humanBeing, Integer userId) {
        if (humanBeing == null) {
            return new ExecutionResponse(false, "Ошибка: объект не может быть null");
        }
        humanBeing.setUserId(userId);
        boolean success = collectionManager.add(humanBeing, userId);
        if (success) {
            return new ExecutionResponse(true, "Элемент успешно добавлен с id: " + humanBeing.getId());
        } else {
            return new ExecutionResponse(false, "Не удалось добавить элемент");
        }
    }
}