import java.io.Serializable;
import java.util.ArrayList;

public class Request implements Serializable{
	
	public static final int TYPE_NAME=1; //idzie imie (klient->serwer)
	public static final int TYPE_SYMBOL=2; //idzie symbol (serwer->klient)
	public static final int TYPE_GAME_TAB_TO_CLINET=3; //idzie plansza z gry do klienta
	public static final int TYPE_GAME_TAB_TO_SERVER=4;//idzie plansza z gry do do serwera
	public static final int TYPE_EXIT=5; //gracz informuje serwer ze wychodzi
	public static final int TYPE_WINNER_INFO=6;
	public static final int TYPE_DRAW_INFO=7;
	
	private int requestType; //typ requestu (czy idzie nick, czy symbol(odpowiedz na nick) czy plansza z gra oraz z lista graczy
	
	//1//
	private String name; //nazwa gracza
	//2//
	private String symbol; //symbol ktory przydziela mu serwer
	
	//3//
	private String[][] gameTab; //tablca z gra
	private ArrayList<Player> playersList; //lista graczy ()
	private int currentPleyerIndex; //nr gracza ktory ma ruch w danej chwili
	private boolean isYourTurn; //czy to kolejka gracza
	
	private int[] choice;
	

	// tylko requestType np przy wychodzeniu z gry
	Request(int requestType){
		this.requestType=requestType;
	}

	//wysyla to klient do serwera po polaczeniu: wysyla swoje imie;
	Request(int requestType, String name){
		this.requestType=requestType;
		this.name=name;
	}
	

	
	
	//wysyla to klient po zatwierdzonym ruchu do serwera 
	Request(int requestType, String symbol, String[][] gameTab){
		this.requestType=requestType;
		this.symbol=symbol;
		this.gameTab=gameTab.clone();
		cloneTab(gameTab);
	}
	
	//wysyla to klient po zatwierdzonym ruchu do serwera 
	Request(int requestType, String symbol, int[] choice){
		this.requestType=requestType;
		this.symbol=symbol;
		this.choice=choice.clone();
	}	
	
	//wysyla to klient po zatwierdzonym ruchu do serwera 
	Request(int requestType, String symbol, String[][] gameTab, int[] choice){
		this.requestType=requestType;
		this.symbol=symbol;
		this.choice=choice.clone();
		cloneTab(gameTab);
		
	}	
	
	
	//wysyla to serwer do klienta po pierwszym polaczeniu: symbol i aktualny stan gry oraz wyedy gry
	//nastepuje koniec gry (symbol wtedy jest symbolem gracza wygranego)
	Request(int requestType, String symbol, String[][] gameTab, ArrayList<Player> playersList, int currentPleyerIndex, boolean isYourTurn){
		this.requestType=requestType;
		this.symbol=symbol;
		cloneTab(gameTab);
		this.playersList=new ArrayList<Player>(playersList);
		this.currentPleyerIndex=currentPleyerIndex;
		this.isYourTurn=isYourTurn;
		

	}
	
	//wysyla to serwer do klienta po za kazdym razem gry jakis gracz wysle swoj ruch
	Request(int requestType, String[][] gameTab, ArrayList<Player> playersList, int currentPleyerIndex, boolean isYourTurn){
		this.requestType=requestType;
		this.gameTab=gameTab.clone();
		this.playersList=new ArrayList<Player>(playersList);
		this.currentPleyerIndex=currentPleyerIndex;
		this.isYourTurn=isYourTurn;
		cloneTab(gameTab);
	}
	
	
	public int getRequestType() {
		return requestType;
	}


	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getSymbol() {
		return symbol;
	}


	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}


	public String[][] getGameTab() {
		return gameTab;
	}


	public void setGameTab(String[][] gameTab) {
		this.gameTab = gameTab;
	}


	public ArrayList<Player> getPlayersList() {
		return new ArrayList<Player>(playersList);
	}


	public void setPlayersList(ArrayList<Player> playersList) {
		this.playersList = playersList;
	}


	public int getCurrentPleyerIndex() {
		return currentPleyerIndex;
	}


	public void setCurrentPleyerIndex(int currentPleyerIndex) {
		this.currentPleyerIndex = currentPleyerIndex;
	}


	public boolean isYourTurn() {
		return isYourTurn;
	}


	public void setYourTurn(boolean isYourTurn) {
		this.isYourTurn = isYourTurn;
	}
	
	public int[] getChoice() {
		return choice;
	}

	public void setChoice(int[] choice) {
		this.choice = choice;
	}
	
	private void cloneTab(String[][] tab){
		this.gameTab=new String[tab.length][tab.length];
		for (int i = 0; i < gameTab.length; i++) {
			for (int j = 0; j < gameTab.length; j++) {
				this.gameTab[i][j]=tab[i][j];

			}
		}
	}

}
