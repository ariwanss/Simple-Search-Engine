package search;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static List<String> people = new ArrayList<>();
    private static Map<String, List<Integer>> invertedIndex = new HashMap<>();

    public static void main(String[] args) {

        String filename = args[1];

        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                people.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < people.size(); i++) {
            String[] words = people.get(i).toLowerCase().split("\\s");
            for (String word : words) {
                List<Integer> index = invertedIndex.getOrDefault(word, new ArrayList<>());
                index.add(i);
                invertedIndex.put(word, index);
            }
        }

        while (true) {
            System.out.println("=== Menu ===\n" +
                    "1. Find a person\n" +
                    "2. Print all people\n" +
                    "0. Exit");
            int response = Integer.parseInt(scanner.nextLine());
            System.out.println();

            switch (response) {
                case 0:
                    System.out.println("Bye!");
                    return;
                case 1:
                    search();
                    break;
                case 2:
                    printAll();
                    break;
                default:
                    System.out.println("Incorrect option! Try again.");
                    break;
            }

            System.out.println();
        }
    }

    private static void search() {
        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        String strategy = scanner.nextLine();
        System.out.println();
        System.out.println("Enter a name or email to search all suitable people:");
        String searchTerm = scanner.nextLine().toLowerCase();
        Set<Integer> foundItems = new HashSet<>();
        System.out.println();

        switch (strategy) {
            case "ALL":
                foundItems = searchAll(searchTerm);
                break;
            case "ANY":
                foundItems = searchAny(searchTerm);
                break;
            case "NONE":
                foundItems = searchNone(searchTerm);
                break;
        }

        System.out.println();
        System.out.println(foundItems.size() + " persons found:");
        foundItems.forEach(x -> System.out.println(people.get(x)));
    }

    private static void printAll() {
        System.out.println("=== List of people ===");
        people.forEach(System.out::println);
    }

    private static Set<Integer> searchAny(String searchTerm) {
        String[] terms = searchTerm.split("\\s");
        Set<Integer> foundItems = new HashSet<>();
        for (String term : terms) {
            foundItems.addAll(invertedIndex.getOrDefault(term, new ArrayList<>()));
        }
        return foundItems;
    }

    private static Set<Integer> searchAll(String searchTerm) {
        String[] terms = searchTerm.split("\\s");
        Set<Integer> foundItems = new HashSet<>();
        for (String term : terms) {
            if (foundItems.isEmpty()) {
                foundItems.addAll(invertedIndex.getOrDefault(term, new ArrayList<>()));
            } else {
                foundItems.retainAll(invertedIndex.getOrDefault(term, new ArrayList<>()));
            }
        }
        return foundItems;
    }

    private static Set<Integer> searchNone(String searchTerm) {
        Set<Integer> foundItems = searchAny(searchTerm);
        Set<Integer> allItems = new HashSet<>();
        invertedIndex.values().forEach(allItems::addAll);
        allItems.removeAll(foundItems);
        return allItems;
    }
}
