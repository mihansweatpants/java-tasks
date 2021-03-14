package org.example;

import org.example.wiki.WikiApiClient;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        WikiApiClient wikiApiClient = new WikiApiClient();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Search Wikipedia (\\q to quit): ");

        while(scanner.hasNextLine()) {
            String input = scanner.nextLine();

            if (input.equals("\\q")) {
                break;
            }

            try {
                System.out.printf("\nSearching for '%s'...\n", input);
                var result = wikiApiClient.searchWiki(input);

                if (result.getSearchInfo().getTotal() == 0) {
                    System.out.println("\nNothing found :(");
                }
                else {
                    System.out.printf("\nFound %d articles\nHere are some of them:\n", result.getSearchInfo().getTotal());
                    for (var article : result.getSearchResults()) {
                        System.out.printf("- %s\n", article.getTitle());
                    }
                }
            } catch (Exception e) {
                System.out.printf("Something went wrong :(\n %s", e.getMessage());
            }

            System.out.println("\nSearch Wikipedia for something else (\\q to quit): ");
        }
    }
}
