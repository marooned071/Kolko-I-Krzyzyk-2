import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;





public class Server {
	static Game game;
	public static void main(String[] args) {
		int m=3;
		int t=7;
		try{
			m=Integer.parseInt(args[0]);
			t=Integer.parseInt(args[1]);
		}catch(Exception e){}
		boolean isDone=false;
		game = new Game(20,m,t);
		try{
			ServerSocket serverSocket= new ServerSocket(31415);
			System.out.println("Serwer start: "+serverSocket.getLocalSocketAddress());
			Socket incoming;
			while(!isDone){
				incoming = serverSocket.accept();
				System.out.println("Nowy klient!"+incoming.toString());
				new Thread(new ServerThread(incoming,game)).start();
			}	
			serverSocket.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
	
	}

}

class ServerThread implements Runnable, Observer{
	
	private Socket clientSocket;
	private ClientRequester clientRequester;
	private ClientListener clientListener;
	
	
	private BlockingQueue<Request> queueRequest;
	
	
	private static ArrayList<ServerThread> clientsList= new ArrayList<ServerThread>();
	private Game game;
	private String symbol;
	
	
	ServerThread(Socket clientSocket, Game game){
		this.game=game;
		this.clientSocket=clientSocket;
		clientsList.add(this);
		queueRequest=new ArrayBlockingQueue<Request>(100);
	}

	@Override
	public void run() {
		clientRequester = new ClientRequester();
		new Thread(clientRequester).start();
		
		clientListener = new ClientListener();	
		clientListener.addObserver(this);
		new Thread(clientListener).start();
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		Request request = (Request) arg1;
		if(request.getRequestType()==Request.TYPE_NAME){
			System.out.println("Gracz przesyla nam swoje imie: "+request.getName());
			onNameReceived(request.getName());
		}
		//gracz przysyla nam palnsze po swoim ruchu
		else if(request.getRequestType()==Request.TYPE_GAME_TAB_TO_SERVER){
			System.out.println("Gracz przesyla plansze z : "+request.getSymbol());
			onChoiceReceived(request.getChoice(),request.getSymbol());
		}
		else if(request.getRequestType()==Request.TYPE_EXIT){
			System.out.println("Gracz chce opuscic gre");
			onPlayerExit();
		}
		
	}
	
	/**
	 * Przchodzi imie od gracza (pierwszy kontant)
	 * Zostanie mu odeslany symbol i stan gry
	 */
	public void onNameReceived(String name){
		symbol=game.addPlayerAndGetSymbol(name);
		sendSymbolAndGameState(symbol);
	}
	
	/**
	 * Odsyla do klienta symbol i stan gry
	 * @param symbol
	 */
	public void sendSymbolAndGameState(String symbol){
		String[][] gameTab=game.getGameTab();
		boolean isYourTurn = isYourTurn();
		ArrayList<Player> playersList = game.getPlayersList();
		int currentPlayerIndex = game.getCurrentPlayerIndex();
		
		Request request = new Request(Request.TYPE_SYMBOL,symbol,gameTab,playersList,currentPlayerIndex,isYourTurn);
		sendRequest(request);
	}
	
	public void sendGameState(){
		String[][] gameTab=game.getGameTab(); //pobiera liste tablice gry
		boolean isYourTurn = isYourTurn(); //pobiera czy teraz jest kolej tego gracza
		ArrayList<Player> playersList = game.getPlayersList(); //pobiera liste graczy
		int currentPlayerIndex = game.getCurrentPlayerIndex(); //pobiera nr gracza ktorego jest teraz kolej
		Request request = new Request(Request.TYPE_GAME_TAB_TO_CLINET,gameTab,playersList,currentPlayerIndex,isYourTurn);
		
		
		sendRequest(request);

	}
	
	/**
	 * Zwraca czy teraz bedzie ruch tego gracza
	 * @return
	 */
	private boolean isYourTurn(){
		boolean b;
		try{
			b= symbol.equals(game.getPlayersList().get(game.getCurrentPlayerIndex()).getSymbol());
		}
		catch(Exception e){
			return true;
		}
		return b;
	}

	
	public void onChoiceReceived(int[] choice, String symbol){
		game.updateGameTab(choice, symbol); // zaktualizuj tablice z gra
		int points = game.checkPoints(choice, symbol); //zobacz czy przytym ruchu gracz zdobyl punkty
		if(points>0){ //jesli zdobyl
			game.removeAllSymols(symbol); //usun wszystkie symbole z planszy
			game.addPointsToPlayer(symbol, points); //dodaj graczowi punkty
		}
		
		ServerThread.sendGameStateToAllClients(); //wyslij info do innych graczy
	}
	
	/**
	 * Wysyla inforamcje do gracza ze gra jest skonczona i wygral winnerSymbol
	 * metoda jest wylowyna tylko tedy gdy gra musi zostac zakonczona (wygrana lub remis)
	 * @param winnerSymbol
	 */
	public void sendDrawInfo(){
		sendRequest(new Request(Request.TYPE_DRAW_INFO));
	}
	
	/**
	 * Wywoluje metode sendWinnerInfo dla wszystkich podlaczonych klientow do wszystkich podlaczonych klientow
	 * metoda jest wylowyna tylko tedy gdy gra musi zostac zakonczona (wygrana lub remis)
	 * @param winnerSymbol
	 */
	public static void sendDrawInfoToAllPlayers(){
		Iterator<ServerThread> it = clientsList.iterator();
		while(it.hasNext()){
			it.next().sendDrawInfo();
		}
	}
	
	/**
	 * Wywoluje metode sendGameState dla wszystkich podlaczonych klientow do wszystkich podlaczonych klientow
	 */
	public static void sendGameStateToAllClients(){
		Iterator<ServerThread> it = clientsList.iterator();
		while(it.hasNext()){
			it.next().sendGameState();
		}

	}
	
	public synchronized void onPlayerExit(){
		System.out.println("onPlayerExit");
		
		if(symbol.equals(game.getPlayersList().get(game.getCurrentPlayerIndex()).getSymbol())){ //sprawdza czy gracz ktory odchodzi ma teraz ruch
			System.out.println("OSZEDL TEN CO MA TERAZ!");
			
		}
		
		game.removePlayer(symbol);
		game.toLowerCase(symbol);

		try{
			clientRequester.close();
			clientListener.close();
			clientSocket.close();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		clientsList.remove(this); //usuwanie z listy klientow
		
		sendGameStateToAllClients();

	}
	
	private void sendRequest(Request request){
		try{
			queueRequest.put(request);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	private class ClientRequester implements Runnable{
		private ObjectOutputStream output;
		private boolean isDone=false;
		private Request request;
		
		ClientRequester(){
			try {
				System.out.println("Stworzony clientRequester");
				output = new ObjectOutputStream(clientSocket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void run() {
			while(!isDone){
				try{
					request=queueRequest.take();
					output.writeObject(request);
					System.out.println("Wysylam request do klienta");
				}
				catch(Exception e){
					e.printStackTrace();
					isDone=true;
				}
			}	
		}
		
		public void close(){
			try{
				isDone=true;
				output.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}


	/**
	 * Klasa nadsluchujaca rzadania od klienta
	 * @author Piotrek
	 *
	 */
	private class ClientListener extends Observable implements Runnable {
		private ObjectInputStream input;
		private boolean isDone=false;
		private Request request;
		
		ClientListener(){
			try {
				System.out.println("Stworzony clientListener");
				input = new ObjectInputStream(clientSocket.getInputStream());
			} catch (IOException e) {
//				e.printStackTrace();
				onPlayerExit();
			}
		}
		
		
		@Override
		public void run() {
			Object inputObject;
			while(!isDone){
				try{
					inputObject=input.readObject();
					request=(Request) inputObject;
					System.out.println("Nowy request od klienta.");
					this.setChanged();
					this.notifyObservers(request);
				}
				catch(Exception e){
					isDone=true;
//					e.printStackTrace();
					onPlayerExit();
				}

			}
			
		}
		
		public void close(){
			try{
				isDone=true;
				input.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}


