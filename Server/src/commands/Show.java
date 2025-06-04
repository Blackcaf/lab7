package commands;

import managers.CollectionManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;
import java.util.List;

public class Show extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public Show(Console console, CollectionManager collectionManager) {
        super("show", "вывести все элементы коллекции");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Выполняется команда: show, userId: " + userId);
        if (userId == null) {
            return new ExecutionResponse(false, "Ошибка: необходимо авторизоваться (login) или зарегистрироваться (register)");
        }

        StringBuilder response = new StringBuilder("Элементы коллекции:\n");
        
        List<HumanBeing> collection = collectionManager.getCollection();
        boolean found = false;
        int totalElements = 0;
        int userElements = 0;

        for (HumanBeing human : collection) {
            totalElements++;
            if (human.getUserId().equals(userId)) {
                response.append(human.toString()).append("\n");
                found = true;
                userElements++;
            }
        }

        console.println("Всего элементов в коллекции: " + totalElements);
        console.println("Ваших элементов: " + userElements);

        if (!found) {
            response.append("У вас нет элементов в коллекции.");
        }

        return new ExecutionResponse(true, response.toString());
    }
}