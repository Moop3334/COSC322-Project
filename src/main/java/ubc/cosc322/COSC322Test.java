
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
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class COSC322Test extends GamePlayer{

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;
	
    private String userName = null;
    private String passwd = null;
 
	
    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {
				 
    	try {
			COSC322Test player = new COSC322Test("dev", "test");

			/*
			
			*/
			HumanPlayer human1 = new HumanPlayer();
			HumanPlayer human2 = new HumanPlayer();

			if(player.getGameGUI() == null) {
    			player.Go();
    		}
    		else {
    			BaseGameGUI.sys_setup();
            	java.awt.EventQueue.invokeLater(new Runnable() {
                	public void run() {
                		player.Go();
						/* 
						
						*/ 
						human1.Go();
						human2.Go();
            	    }
           		});
    		}
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Error: Please run with arguments");
		}
    }
	
    /**
     * Any name and passwd 
     * @param userName
      * @param passwd
     */
    public COSC322Test(String userName, String passwd) {
    	this.userName = userName;
    	this.passwd = passwd;

    	
    	//To make a GUI-based player, create an instance of BaseGameGUI
		this.gamegui = new BaseGameGUI(this);
    	//and implement the method getGameGUI() accordingly
    	//this.gamegui = new BaseGameGUI(this);
    }
 


    @Override
    public void onLogin() {
		userName = gameClient.getUserName(); 
    	if(gamegui != null) { 
   			gamegui.setRoomInformation(gameClient.getRoomList()); 
    	} 
    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
    	//This method will be called by the GameClient when it receives a game-related message
    	//from the server.
	
    	//For a detailed description of the message types and format, 
    	//see the method GamePlayer.handleGameMessage() in the game-client-api document. 
		if (messageType.equalsIgnoreCase(GameMessage.GAME_STATE_BOARD)) {
			gamegui.setGameState((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE));
		} else if (messageType.equalsIgnoreCase(GameMessage.GAME_ACTION_MOVE)) {
			System.out.println(msgDetails);
			gamegui.updateGameState(msgDetails);
		} else if (messageType.equalsIgnoreCase(GameMessage.GAME_ACTION_START)) {
			gamegui.setGameState((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE));
			
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
		return  this.gamegui;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
    	gameClient = new GameClient(userName, passwd, this);		
	}

 
}//end of class
