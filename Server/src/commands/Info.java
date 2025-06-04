package commands;

import managers.CollectionManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

import java.util.List;

public class Info extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public Info(Console console, CollectionManager collectionManager) {
        super("info", "вывести информацию о коллекции");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Выполняется команда: info, userId: " + userId);
        if (userId == null) {
            return new ExecutionResponse(false, "Ошибка: необходимо авторизоваться (login) или зарегистрироваться (register)");
        }

        List<HumanBeing> collection = collectionManager.getCollection();
        int totalElements = collection.size();
        int userElements = 0;

        for (HumanBeing human : collection) {
            if (human.getUserId().equals(userId)) {
                userElements++;
            }
        }

        StringBuilder response = new StringBuilder();
        response.append("Тип коллекции: ").append(collection.getClass().getSimpleName()).append("\n");
        response.append("Дата инициализации: ").append(collectionManager.getInitializationDate()).append("\n");
        response.append("Всего элементов в коллекции: ").append(totalElements).append("\n");
        response.append("Ваших элементов в коллекции: ").append(userElements);

        return new ExecutionResponse(true, response.toString());
    }
}