package trivia;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

public class Game implements IGame {
   // Constantes
   private static final int MAX_PLAYERS = 6;
   private static final int BOARD_SIZE = 12;
   private static final int WINNING_COINS = 6;

   private final ArrayList<Player> players = new ArrayList<>();
   private final Map<Categories, LinkedList<String>> questionMap = new HashMap<>();

   private int currentPlayerIndex = 0;
   private boolean isGettingOutOfPenaltyBox;
   private boolean gameStarted = false;

   public Game() {
      initializeQuestions();
   }

   private void initializeQuestions() {
      for (Categories category : Categories.values()) {
         questionMap.put(category, loadQuestionsForCategory(category));
      }
   }

   private LinkedList<String> loadQuestionsForCategory(Categories category) {
      LinkedList<String> questions = new LinkedList<>();
      try (InputStream input = getClass().getClassLoader().getResourceAsStream(category.getFilename())) {
         if (input == null) {
            System.out.println("Fichier introuvable : " + category.getFilename());
            return questions;
         }
         Properties props = new Properties();
         props.load(input);
         int i = 0;
         while (true) {
            String question = props.getProperty(String.valueOf(i));
            if (question == null) break;
            questions.addLast(question);
            i++;
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      return questions;
   }

   public boolean add(String playerName) {
      if (gameStarted) {
         System.out.println("La partie a déjà commencé. Impossible d'ajouter de nouveaux joueurs.");
         return false;
      }
      if (players.size() >= MAX_PLAYERS) {
         System.out.println("Impossible d'ajouter plus de " + MAX_PLAYERS + " joueurs.");
         return false;
      }
      if (players.stream().anyMatch(p -> p.getName().equals(playerName))) {
         System.out.println("Un joueur portant le nom " + playerName + " existe déjà.");
         return false;
      }
      players.add(new Player(playerName));
      System.out.println(playerName + " a bien été ajouté. Nombre de joueurs : " + players.size() + ".");
      return true;
   }

   public int howManyPlayers() {
      return players.size();
   }

   public void startGame() {
      if (players.size() < 2) {
         System.out.println("Au moins 2 joueurs sont nécessaires pour démarrer la partie.");
         return;
      }
      gameStarted = true;
      System.out.println("La partie commence !");
   }

   public void roll(int roll) {
      if (!gameStarted) {
         System.out.println("La partie n'a pas encore commencé. Veuillez démarrer la partie avec startGame().");
         return;
      }
      Player current = getCurrentPlayer();
      System.out.println(current.getName() + ", c'est à toi de jouer !");
      System.out.println("Tu as lancé un " + roll + ".");
      if (current.isInPenaltyBox()) {
         handlePenaltyBoxRoll(roll);
      } else {
         playTurn(roll);
      }
   }

   private void handlePenaltyBoxRoll(int roll) {
      isGettingOutOfPenaltyBox = (roll % 2 != 0);
      Player current = getCurrentPlayer();
      if (isGettingOutOfPenaltyBox) {
         System.out.println(current.getName() + " sort de la prison !");
         playTurn(roll);
      } else {
         System.out.println(current.getName() + " ne sort pas de la prison.");
         moveToNextPlayer();
      }
   }

   private void playTurn(int roll) {
      Player current = getCurrentPlayer();
      movePlayerPosition(current, roll);
      announcePositionAndCategory(current);
      askQuestion();
   }

   private void movePlayerPosition(Player player, int roll) {
      int newPosition = (player.getPosition() + roll - 1) % BOARD_SIZE + 1;
      player.setPosition(newPosition);
   }

   private void announcePositionAndCategory(Player player) {
      System.out.println("La nouvelle position de " + player.getName() + " est " + player.getPosition() + ".");
      System.out.println("La catégorie est " + currentCategory(player) + ".");
   }

   private void askQuestion() {
      Categories category = currentCategory(getCurrentPlayer());
      LinkedList<String> questions = questionMap.get(category);
      if (questions.isEmpty()) {
         System.out.println("Plus de questions dans la catégorie " + category + " !");
      } else {
         System.out.println(questions.removeFirst());
      }
   }

   private Categories currentCategory(Player player) {
      int posIndex = player.getPosition() - 1;
      return Categories.values()[posIndex % Categories.values().length];
   }

   public boolean handleCorrectAnswer() {
      Player current = getCurrentPlayer();
      if (current.isInPenaltyBox() && !isGettingOutOfPenaltyBox) {
         moveToNextPlayer();
         return true;
      }
      System.out.println("La réponse est correcte !!!!");
      current.clearLastWrongCategory();
      current.incrementStreak();
      current.addCoin();
      System.out.println(current.getName() + " a maintenant " + current.getCoins() + " pièces d'or.");
      boolean continuer = didPlayerWin(current);
      moveToNextPlayer();
      return continuer;
   }

   public boolean wrongAnswer() {
      Player current = getCurrentPlayer();
      System.out.println("La réponse est incorrecte.");
      if (current.getStreak() > 0) {
         System.out.println("La série de " + current.getName() + " est terminée.");
         current.resetStreak();
         current.clearLastWrongCategory();
      } else {
         Categories currentCat = currentCategory(current);
         if (current.getLastWrongCategory() == null || !current.getLastWrongCategory().equals(currentCat)) {
            current.setLastWrongCategory(currentCat);
            System.out.println("Seconde chance offerte dans la catégorie " + currentCat + ".");
         } else {
            System.out.println(current.getName() + " est envoyé(e) en prison.");
            current.setInPenaltyBox(true);
            current.clearLastWrongCategory();
         }
      }
      moveToNextPlayer();
      return true;
   }

   private void moveToNextPlayer() {
      currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
   }

   private boolean didPlayerWin(Player player) {
      if (player.getCoins() < WINNING_COINS) {
         return true;
      }
      for (Player p : players) {
         if (p != player && player.getCoins() < 2 * p.getCoins()) {
            return true;
         }
      }
      return false;
   }

   private Player getCurrentPlayer() {
      return players.get(currentPlayerIndex);
   }
}
