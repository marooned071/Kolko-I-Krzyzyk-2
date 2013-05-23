import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;


public class Client implements Observer{

	public static void main(String[] args) {
		new Client();
	}
	
	private ClientGUI gui;
	private ClientNet clientNet;
	private String symbol;
	
	Client(){
		
		clientNet = new ClientNet();
		clientNet.addObservers(this);

		String nick=JOptionPane.showInputDialog(null, "Podaj swoj nick:", "Seba");
		ClientGUI gui = new ClientGUI(20,"");
		this.gui=gui;
		if(nick!=null){
			//clientNet.sendNick(nick);
			clientNet.sendRequest(new Request(Request.TYPE_NAME,nick));
		}
			
		else{
			System.out.println("Gracz zrezygnowal.");
			onExit();

		}
		gui.addObservers(this);
		
	}
	

	public void update(Observable arg0, Object arg1) {
		if(arg0.equals(clientNet.getServerListener())){
			System.out.println("Powiadomienie od serwera!");
			Request request = (Request) arg1;
			//przyszedl symbol
			if(request.getRequestType()==Request.TYPE_SYMBOL){
				System.out.println("Client: up :Symbol:  "+request.getSymbol());
				this.symbol=request.getSymbol();
				onSymolReceived(symbol,request.getGameTab(),request.getPlayersList(),request.getCurrentPleyerIndex(),request.isYourTurn());
			//	onUpdateGameState(request.getGameTab(),request.getPlayersList(),request.getCurrentPleyerIndex(),request.isYourTurn());
			}
			//nadchodzi update z tablica gry itp
			else if(request.getRequestType()==Request.TYPE_GAME_TAB_TO_CLINET){
				onUpdateGameState(request.getGameTab(),request.getPlayersList(),request.getCurrentPleyerIndex(),request.isYourTurn());
			}
			// informacja ze ktos wygral
			else if(request.getRequestType()==Request.TYPE_WINNER_INFO){

			}
			//informacja ze jest remis
			else if(request.getRequestType()==Request.TYPE_DRAW_INFO){
				onDrawReceived();
			}
		}
		
		 //powiadomienie przychodzi od GUI	
		else if(arg0.equals(gui.getButtonAdapter_accept())){
			int[] choice = (int[]) arg1;
			System.out.println("GUI: akceptuje ruch:"+choice[0]+" "+choice[1]);
			Request request=new Request(Request.TYPE_GAME_TAB_TO_SERVER,symbol,choice);
			clientNet.sendRequest(request);
			
		}
		else if(arg0.equals(gui.getButtonAdapter_walkover())){
			System.out.println("GUI podaje sie");
			onExit();

		}
		
		else if(arg0.equals(gui.getWindowAdapter())){
			System.out.println("GUI wyjscie za pomoca przycisku 'x'");
			 onExit();
		}
	}
	
	
	
	public void onDrawReceived(){
		
	}
	
	public void onUpdateGameState(String[][] gameTab, ArrayList<Player> playersList, int currentPleyerIndex, boolean isYourTurn){
		gui.setGameTab(gameTab);
		gui.setPlayerList(playersList);
		gui.setCurrentPlayer(currentPleyerIndex);
//		if(isYourTurn){ //runda tego gracza
//			gui.setEnabled_buttonAccept(true);
//			gui.setText_labelInfo("Twoj ruch!");
//		}
//		else{ //runda innego gracza
//			gui.setEnabled_buttonAccept(false);
//			gui.setText_labelInfo("Ruch przeciwnika!");
//		}
	}
	
	public void onSymolReceived(String symbol,String[][] gameTab, ArrayList<Player> playersList, int currentPleyerIndex, boolean isYourTurn){
		gui.setGameTab(gameTab);
		gui.setPlayerList(playersList);
		gui.setCurrentPlayer(currentPleyerIndex);
//		if(isYourTurn){ //runda tego gracza
//			gui.setEnabled_buttonAccept(true);
//			gui.setText_labelInfo("Twoj ruch!");
//		}
//		else{ //runda innego gracza
//			gui.setEnabled_buttonAccept(false);
//			gui.setText_labelInfo("Ruch przeciwnika!");
//		}
		gui.setSymbol(symbol);	
	}
	
	//to co sie dzieje po wytjsciu z gry (zamknieciu clienta)
	public void onExit(){
		
		clientNet.sendRequest(new Request(Request.TYPE_EXIT));
		clientNet.exit();
		System.exit(0);
	}

}


