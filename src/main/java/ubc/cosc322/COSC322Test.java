
package ubc.cosc322;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

/**
 * An example illustrating how to implement a GamePlayer
 * 
 * @author Yong Gao (yong.gao@ubc.ca)
 *         Jan 5, 2021
 *
 */
public class COSC322Test extends GamePlayer {

	private GameClient gameClient = null;
	private BaseGameGUI gamegui = null;

	private String userName = null;
	private String passwd = null;

	// To use the AI
	private COSC322AI ai = new COSC322AI();
	private boolean isWhite;
	private ArrayList<Integer> currentBoardState;

	/**
	 * The main method
	 * 
	 * @param args for name and passwd (current, any string would work)
	 */
	public static void main(String[] args) {

		try {
			COSC322Test player = new COSC322Test("dev1" + (int) (Math.random() * 10), "test");

			/*
			
			*/
			// HumanPlayer human1 = new HumanPlayer();
			// HumanPlayer human2 = new HumanPlayer();

			if (player.getGameGUI() == null) {
				player.Go();
			} else {
				BaseGameGUI.sys_setup();
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						player.Go();
						/* 
						
						*/
						// human1.Go();
						// human2.Go();
					}
				});
			}
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Error: Please run with arguments");
		}
	}

	/**
	 * Any name and passwd
	 * 
	 * @param userName
	 * @param passwd
	 */
	public COSC322Test(String userName, String passwd) {
		this.userName = userName;
		this.passwd = passwd;

		// To make a GUI-based player, create an instance of BaseGameGUI
		this.gamegui = new BaseGameGUI(this);
		// and implement the method getGameGUI() accordingly
		// this.gamegui = new BaseGameGUI(this);
	}

	@Override
	public void onLogin() {
		userName = gameClient.getUserName();
		if (gamegui != null) {
			gamegui.setRoomInformation(gameClient.getRoomList());
		}
	}

	@Override
	public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
		// This method will be called by the GameClient when it receives a game-related
		// message
		// from the server.

		// For a detailed description of the message types and format,
		// see the method GamePlayer.handleGameMessage() in the game-client-api
		// document.
		if (messageType.equalsIgnoreCase(GameMessage.GAME_STATE_BOARD)) {
			ArrayList<Integer> state = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE);
			if (state != null) {
				this.currentBoardState = new ArrayList<>(state); // Clone it to be safe
				gamegui.setGameState(this.currentBoardState);
			} else {
				System.err.println("Failed to load board: Message details returned null for GAME_STATE");
			}
		} else if (messageType.equalsIgnoreCase(GameMessage.GAME_ACTION_MOVE)) {
			System.out.println(msgDetails);
			gamegui.updateGameState(msgDetails);
			ArrayList<Integer> queenCurrent = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
			ArrayList<Integer> queenNext = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_NEXT);
			ArrayList<Integer> arrow = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);
			applyMove(queenCurrent, queenNext, arrow);
			makeMove();
		} else if (messageType.equalsIgnoreCase(GameMessage.GAME_ACTION_START)) {
			ArrayList<Integer> state = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE);

			// Only update the GUI if the state actually exists in this message
			if (state != null) {
				this.currentBoardState = new ArrayList<>(state);
				gamegui.setGameState(this.currentBoardState);
			}

			String blackPlayer = (String) msgDetails.get(AmazonsGameMessage.PLAYER_BLACK);
			String whitePlayer = (String) msgDetails.get(AmazonsGameMessage.PLAYER_WHITE);
			isWhite = userName.equals(whitePlayer);

			System.out.println("We are playing as: " + (isWhite ? "WHITE" : "BLACK"));

			if (!isWhite && this.currentBoardState != null) {
				makeMove();
			}
		}
		return true;
	}

	@Override
	public String userName() {
		return userName;
	}

	@Override
	public GameClient getGameClient() {
		// TODO Auto-generated method stub
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		// TODO Auto-generated method stub
		return this.gamegui;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
		gameClient = new GameClient(userName, passwd, this);
	}

	private void applyMove(ArrayList<Integer> queenCurrent, ArrayList<Integer> queenNext, ArrayList<Integer> arrow) {
		if (currentBoardState == null || queenCurrent == null || queenNext == null || arrow == null) {
        	return;
    	}
		int from = queenCurrent.get(0) * 11 + queenCurrent.get(1);
		int to = queenNext.get(0) * 11 + queenNext.get(1);
		int arr = arrow.get(0) * 11 + arrow.get(1);
		// Validate indices are within bounds
		if (from < 0 || from >= 121 || to < 0 || to >= 121 || arr < 0 || arr >= 121) {
			System.err.println("Invalid move coordinates: from=" + from + ", to=" + to + ", arr=" + arr);
			return;
		}
		int piece = currentBoardState.get(from);
		currentBoardState.set(from, 0);
		currentBoardState.set(to, piece);
		currentBoardState.set(arr, 3);
	}

	private void makeMove() {
		if (currentBoardState == null) {
			return;
		}

		COSC322Node move = ai.FindMove(isWhite, currentBoardState);
		if (move == null || move.parent == null) {
			// No valid moves available - we lost!
			System.out.println("========================================");
			System.out.println("GAME OVER - WE LOST! No valid moves remaining.");
			System.out.println("We played as: " + (isWhite ? "WHITE" : "BLACK"));
			System.out.println("========================================");
			return;
		}

		// Extract move by comparing parent/child int[] states
		int[] parent = move.parent.state;
		int[] child = move.state;

		int fromIdx = -1, toIdx = -1, arrowIdx = -1;
		int ourPiece = isWhite ? 1 : 2;

		for (int i = 0; i < 121; i++) {
			int p = parent[i];
			int c = child[i];

			if (p == ourPiece && c != ourPiece)
				fromIdx = i;
			if (p != ourPiece && c == ourPiece)
				toIdx = i;
			if (p != 3 && c == 3)
				arrowIdx = i;
		}

		if (fromIdx == -1 || toIdx == -1 || arrowIdx == -1) {
			return;
		}

		ArrayList<Integer> qCurr = new ArrayList<>();
		qCurr.add(fromIdx / 11);
		qCurr.add(fromIdx % 11);
		ArrayList<Integer> qNext = new ArrayList<>();
		qNext.add(toIdx / 11);
		qNext.add(toIdx % 11);
		ArrayList<Integer> arrow = new ArrayList<>();
		arrow.add(arrowIdx / 11);
		arrow.add(arrowIdx % 11);
		// Validate move indices
		if (fromIdx < 0 || fromIdx >= 121 || toIdx < 0 || toIdx >= 121 || arrowIdx < 0 || arrowIdx >= 121) {
			System.err.println("Invalid move indices detected");
			return;
		}

		// Sync currentBoardState (ArrayList) from the chosen child int[]
		for (int i = 0; i < 121; i++) currentBoardState.set(i, child[i]);
		gameClient.sendMoveMessage(qCurr, qNext, arrow);
		gamegui.updateGameState(qCurr, qNext, arrow);
	}

}// end of class
