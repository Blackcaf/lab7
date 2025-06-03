package commands;

import managers.CollectionManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

public class FilterStartsWithName extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public FilterStartsWithName(Console console, CollectionManager collectionManager) {
        super("filter_starts_with_name", "вывести элементы, значение поля name которых начинается с заданной подстроки");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Выполняется команда: filter_starts_with_name, userId: " + userId);
        if (argument == null || argument.getName() == null) {
            return new ExecutionResponse(false, "Ошибка: необходимо указать подстроку для поля name");
        }

        String prefix = argument.getName();
        StringBuilder response = new StringBuilder("Элементы с именем, начинающимся с \"" + prefix + "\":\n");
        boolean found = false;

        for (HumanBeing human : collectionManager.getCollection()) {
            if (human.getName().startsWith(prefix) && human.getUserId().equals(userId)) {
                response.append(human.toString()).append("\n");
                found = true;
            }
        }

        if (!found) {
            response.append("Элементы не найдены.");
        }

        return new ExecutionResponse(true, response.toString());
    }
}