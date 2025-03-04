package trivia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Game implements IGame {
   // Constantes
   private static final int MAX_PLAYERS = 6;
   private static final int BOARD_SIZE = 12;
   private static final int WINNING_COINS = 6;
   private static final int QUESTIONS_PER_CATEGORY = 50;
   private static final String[] CATEGORIES = {"Pop", "Science", "Sports", "Rock"};

   // État du jeu
   private final ArrayList<String> players = new ArrayList<>();
   private final int[] places = new int[MAX_PLAYERS];
   private final int[] purses = new int[MAX_PLAYERS];
   private final boolean[] inPenaltyBox = new boolean[MAX_PLAYERS];

   // Questions par catégorie
   private final Map<String, LinkedList<String>> questionMap;

   // État du tour actuel
   private int currentPlayer = 0;
   private boolean isGettingOutOfPenaltyBox;

   public Game() {
      questionMap = initializeQuestionMap();
      initializeQuestions();
   }

   private Map<String, LinkedList<String>> initializeQuestionMap() {
      Map<String, LinkedList<String>> map = new HashMap<>();
      for (String category : CATEGORIES) {
         map.put(category, new LinkedList<>());
      }
      return map;
   }

   private void initializeQuestions() {
      for (int i = 0; i < QUESTIONS_PER_CATEGORY; i++) {
         final int questionNumber = i;
         questionMap.forEach((category, questions) -> {
            String questionText = category + " Question " + questionNumber;
            questions.addLast(questionText);
         });
      }
   }

   public boolean isPlayable() {
      return howManyPlayers() >= 2;
   }

   public boolean add(String playerName) {
      if (howManyPlayers() >= MAX_PLAYERS) {
         System.out.println("Cannot add more than " + MAX_PLAYERS + " players");
         return false;
      }

      players.add(playerName);
      int playerIndex = howManyPlayers() - 1;
      places[playerIndex] = 1;  // Position initiale
      purses[playerIndex] = 0;  // Pas de pièces au début
      inPenaltyBox[playerIndex] = false;  // Pas dans la penalty box au début

      System.out.println(playerName + " was added");
      System.out.println("They are player number " + players.size());
      return true;
   }

   public int howManyPlayers() {
      return players.size();
   }

   public void roll(int roll) {
      String playerName = players.get(currentPlayer);
      System.out.println(playerName + " is the current player");
      System.out.println("They have rolled a " + roll);

      if (inPenaltyBox[currentPlayer]) {
         handlePenaltyBoxRoll(roll);
      } else {
         movePlayerPosition(roll);
         announcePositionAndCategory();
         askQuestion();
      }
   }

   private void handlePenaltyBoxRoll(int roll) {
      boolean isOdd = roll % 2 != 0;
      isGettingOutOfPenaltyBox = isOdd;

      if (isOdd) {
         System.out.println(players.get(currentPlayer) + " is getting out of the penalty box");
         movePlayerPosition(roll);
         announcePositionAndCategory();
         askQuestion();
      } else {
         System.out.println(players.get(currentPlayer) + " is not getting out of the penalty box");
         currentPlayer = (currentPlayer + 1) % players.size();
      }
   }

   private void movePlayerPosition(int roll) {
      places[currentPlayer] = (places[currentPlayer] + roll - 1) % BOARD_SIZE + 1;
   }

   private void announcePositionAndCategory() {
      System.out.println(players.get(currentPlayer) + "'s new location is " + places[currentPlayer]);
      System.out.println("The category is " + currentCategory());
   }

   private void askQuestion() {
      String category = currentCategory();
      LinkedList<String> questions = questionMap.get(category);
      if (questions.isEmpty()) {
         System.out.println("No more " + category + " questions!");
      } else {
         System.out.println(questions.removeFirst());
      }
   }

   private String currentCategory() {
      int position = places[currentPlayer] - 1;
      return CATEGORIES[position % CATEGORIES.length];
   }

   public boolean handleCorrectAnswer() {
      if (inPenaltyBox[currentPlayer] && !isGettingOutOfPenaltyBox) {
         moveToNextPlayer();
         return true;
      }

      System.out.println("Answer was correct!!!!");
      purses[currentPlayer]++;

      String playerName = players.get(currentPlayer);
      int coins = purses[currentPlayer];
      System.out.println(playerName + " now has " + coins + " Gold Coins.");

      boolean winner = didPlayerWin();
      moveToNextPlayer();
      return winner;
   }

   private void moveToNextPlayer() {
      currentPlayer = (currentPlayer + 1) % players.size();
   }

   public boolean wrongAnswer() {
      System.out.println("Question was incorrectly answered");
      System.out.println(players.get(currentPlayer) + " was sent to the penalty box");
      inPenaltyBox[currentPlayer] = true;

      moveToNextPlayer();
      return true;
   }

   private boolean didPlayerWin() {
      return purses[currentPlayer] != WINNING_COINS;
   }
}