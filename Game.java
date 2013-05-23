import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Klasa oblugujaca plansze. Sprawdza czy ktos wygral.
 * @author Piotrek
 *
 */
public class Game {
	
	public static final String DARW="DRAW";
	public static final String NOBODY="NOBODY";
	
	private String[][] gameTab;

	private int m;
	private List<Player> playersList;
	private int playerCounter=0;
	private int currentPlayerIndex=0;
	
	private LinkedList<int[]> pointsToDeleteList;
	
	private final int[] NULL_POINT = {-1,-1};
	
	/**
	 * 
	 * @param n - liczba kratek
	 * @param m - liczba symboli w jednej lini potrzeba do wygrania
	 */
	Game(int n,int m, int t){
		this.gameTab=new String[n][n];
		this.m=m;
		playersList=Collections.synchronizedList(new ArrayList<Player>());
		pointsToDeleteList= new LinkedList<int[]>();
		
		for(int i=0;i<t;i++){
			pointsToDeleteList.offer(NULL_POINT);
		}
		
	}
	
	public void printGameTab(){
		for(int i=0;i<gameTab.length;i++){
			for(int j=0;j<gameTab.length;j++){
				if(gameTab[i][j]==null){
					System.out.print("   ");
				}
				else
					System.out.print(" "+gameTab[i][j]+" ");
			}
			
			System.out.println();
			
		}
	}
	
	public void addPlayer(Player player){
		playersList.add(player);
	}
	
	public void addPointsToPlayer(String symbol, int points){

		Player player;
		for(int i=0;i<playersList.size();i++){
			player=playersList.get(i);
			if(player.getSymbol().equals(symbol)){
				Player p2 = new Player(player.getName(), player.getSymbol(),player.getPoints()+points);
				playersList.set(i, p2);

			}
		}
	}
	
	/**
	 * Usuwa wszystkie symbole gracza z planszy
	 * @param symbol
	 */
	public void removeAllSymols(String symbol){
		for(int i=0;i<gameTab.length;i++){
			for(int j=0;j<gameTab.length;j++){
				if(gameTab[i][j]!=null &&gameTab[i][j].equals(symbol)){
					gameTab[i][j]=null;
				}
			}
		}
	}
	
	/**
	 * Zamienia na planszy wszystkie duze litery symbolu na male oraz dodaje je na liste z symbolami do powolnego usuwania
	 * Za kazdym dodaniem nowych punktow lista jest mieszana
	 * @param symbol
	 */
	public void toLowerCase(String symbol){
		String lowerCase = symbol.toLowerCase();
		int[] pointToDelete= new int[2];
		for(int i=0;i<gameTab.length;i++){
			for(int j=0;j<gameTab.length;j++){
				if(gameTab[i][j]!=null &&gameTab[i][j].equals(symbol)){
					gameTab[i][j]=lowerCase;
					pointToDelete=new int[2];
					pointToDelete[0]=i;
					pointToDelete[1]=j;
					pointsToDeleteList.offer(pointToDelete);
				}
			}
		}
		Collections.shuffle(pointsToDeleteList);
		
	}
	
	/**
	 * Dodaje gracza i zwraca jego symbol
	 * @param name
	 * @return
	 */
	public String addPlayerAndGetSymbol(String name){
		playerCounter++;
		String symbol = (char) (64 +playerCounter)+"";
		Player player = new Player(name, symbol,0);
		playersList.add(player);
		
		System.out.println("GAME: dodano gracza: "+name+" i zwrocono symbol: "+symbol);
		
		return symbol;
	}
	

	public synchronized void removePlayer(String symbol){
		System.out.println("Remove Player (symbol)");
		synchronized (playersList){
			Iterator<Player> it = playersList.iterator();
			Player p;
			while(it.hasNext()){
				p=it.next();
				if(p.getSymbol().equals(symbol)){
					playersList.remove(p);
					return;
				}
			}
		}	
	}
	
	/**
	 * Dodaje do tablicy gry ruch z choice dla symbolu symbol
	 * @param choice
	 * @param symbol
	 */
	public synchronized  void updateGameTab(int[] choice, String symbol){
		String[][] tab = gameTab;
		
		tab[choice[0]][choice[1]] = symbol;
		this.gameTab=tab;
		
		pointsToDeleteList.offer(choice);
		
		removeNextPoint();
		
		
		

	}
	
	
	public void removeNextPoint(){
		int[] point = pointsToDeleteList.poll();
		if(!point.equals(this.NULL_POINT)){
			gameTab[point[0]][point[1]]=null;
		}
	}
	


	

	
	/**
	 * Sprawdza czy dla podanego punktu (choice) znajduje sie m symboli w jendej lini. zwraca ilosc takich lini
	 * Algorytm sprawdza 8 lini, w kazdym kirunku od zadanego punktu
	 * @param choice
	 * @param symbol
	 * @return
	 */
	public int checkPoints(int[] choice, String symbol){
		int counter;
		
		int winCounter=0;

		int a = 0; // krok w wspolrzednych poziomych
		int b = 0; // krok w wsporzednych pionowych
		
		int w;
		int z;
		
		for (int p = 0; p < 8; p++) { 
			counter = 0;

			if (p == 0) { a = -1; b = -1;}
			else if (p == 1){ a = -1; b = 0;}
			else if (p == 2){ a = -1; b = 1;}
			
			else if (p == 3){ a = 0; b = -1;}
			else if (p == 4){ a = 0; b = 1;}
			
			else if (p == 5){ a = 1; b = -1;}
			else if (p == 6){ a = 1; b = 0;}
			else if (p == 7){ a = 1; b = 1;}
			
			w = choice[0];
			z = choice[1];
			try {
				while (gameTab[w][z].equals(symbol)) {
					w += a;
					z += b;		
					counter++;
					if (counter == m){
						winCounter++;
					}
				}
			}catch (Exception e) {}
			
		}
		
		return winCounter;
	}
	
	
	public String[][] getGameTab() {
		return gameTab;
	}

	public int getM() {
		return m;
	}

	public void setM(int m) {
		this.m = m;
	}

	public ArrayList<Player> getPlayersList() {
		return new ArrayList<Player>(playersList);
	}
	
	public int getCurrentPlayerIndex(){
		return currentPlayerIndex;
	}

}
