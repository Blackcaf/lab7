package commands;

import managers.CollectionManager;
import models.HumanBeing;
import utility.Console;
import utility.ExecutionResponse;

import java.util.Set;
import java.util.stream.Collectors;

public class PrintUniqueImpactSpeed extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public PrintUniqueImpactSpeed(Console console, CollectionManager collectionManager) {
        super("print_unique_impact_speed", "вывести уникальные значения поля impactSpeed");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(HumanBeing argument, Integer userId) {
        console.println("Выполняется команда: print_unique_impact_speed, userId: " + userId);
        Set<Long> uniqueSpeeds = collectionManager.getCollection().stream()
                .filter(h -> h.getUserId().equals(userId))
                .map(HumanBeing::getImpactSpeed)
                .collect(Collectors.toSet());

        StringBuilder response = new StringBuilder("Уникальные значения impactSpeed:\n");
        if (uniqueSpeeds.isEmpty()) {
            response.append("Нет элементов, принадлежащих пользователю.");
        } else {
            uniqueSpeeds.forEach(speed -> response.append(speed).append("\n"));
        }

        return new ExecutionResponse(true, response.toString());
    }
}