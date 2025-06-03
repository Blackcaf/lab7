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
        StringBuilder response = new StringBuilder("Элементы коллекции:\n");
        
        List<HumanBeing> collection = collectionManager.getCollection();
        if (collection.isEmpty()) {
            response.append("Коллекция пуста");
        } else {
            for (HumanBeing human : collection) {
                response.append(human.toString()).append("\n");
            }
        }

        return new ExecutionResponse(true, response.toString());
    }
}