package com.ridango.game;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.net.*;
import java.io.*;

@SpringBootApplication
public class CocktailGameApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(CocktailGameApplication.class, args);
	}

	@Override public void run(String... args) throws Exception {
	}
}



public class GuessTheCocktailGame {

    public static void main(String[] args) {
        GuessTheCocktailGame game = new GuessTheCocktailGame();
        game.play();
    }


    private static final int GUESSES = 5;
    private int score = 0;

    public void play() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Getting a random cocktail from the website
            String cocktailName = fetchRandomCocktail();
            if (cocktailName == null) {
                System.out.println("Error retrieving cocktail.");
                break;
            }

            
            // Prepares the masked cocktail name
            String maskedName = maskName(cocktailName);
            int attempts = GUESSES;

            // Prints out the hidden cocktail name and shows the player's attempts left
            while (attempts > 0) {
                System.out.println("Cocktail: " + maskedName);
                System.out.println("Attempts left: " + attempts);
                System.out.print("Your guess: ");
                
                // Updates the player's score upon a successful guess
                String guess = scanner.nextLine();
                if (guess.equalsIgnoreCase(cocktailName)) {
                    score += attempts; 
                    System.out.println("Correct! Your score: " + score);
                    break;
                } else {
                    // Reveals one more character in the cocktail name
                    maskedName = revealCharacters(maskedName, cocktailName);
                    attempts--;
                    if (attempts == 0) {
                        System.out.println("Out of attempts. The cocktail was: " + cocktailName);
                        System.out.println("Final score: " + score);
                    } else {
                        System.out.println("Incorrect. Try again.");
                    }
                }
            }
        }
        scanner.close();
    }

    // URl and API (it says depricated but it works and im not sure how else to fetch it)
    private String fetchRandomCocktail() {
        try {
            URL url = new URL("https://www.thecocktaildb.com/api/json/v1/1/random.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Extracts cocktail name from the JSON response
            String responseString = response.toString();
            int nameStart = responseString.indexOf("\"strDrink\":\"") + 12;
            int nameEnd = responseString.indexOf("\"", nameStart);
            return responseString.substring(nameStart, nameEnd);
        } catch (Exception e) {
            return null; 
        }
    }

    // Hides the cocktail name with underscores
    private String maskName(String name) {
        return name.replaceAll(".", "_");
    }

    // Reveals one character in the masked name
    private String revealCharacters(String maskedName, String actualName) {
        Random random = new Random();
        char[] maskedArray = maskedName.toCharArray();
        List<Integer> unrevealedIndexes = new ArrayList<>();

        for (int i = 0; i < actualName.length(); i++) {
            if (maskedName.charAt(i) == '_') {
                unrevealedIndexes.add(i);
            }
        }

        if (!unrevealedIndexes.isEmpty()) {
            int indexToReveal = unrevealedIndexes.get(random.nextInt(unrevealedIndexes.size()));
            maskedArray[indexToReveal] = actualName.charAt(indexToReveal);
        }

        return new String(maskedArray);
    }
}
