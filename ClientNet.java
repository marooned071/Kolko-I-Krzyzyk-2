import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class ClientNet {

	
	private ServerRequester serverRequester;
	private ServerListener serverListener;
	private BlockingQueue<Request> queueRequest;
	
	private Socket serverSocket;
	
	ClientNet(){
		
		try{ 
			serverSocket = null;
			
			serverSocket =new Socket("127.0.0.1", 31415);
			queueRequest=new ArrayBlockingQueue<Request>(100); //kolejka rozkazow - tym beda one przesylane do ServerRequester
			
			
			serverRequester =new ServerRequester();
			new Thread(serverRequester).start();
			
			serverListener=new ServerListener();
			new Thread(serverListener).start();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void addObservers(Observer o){
		serverListener.addObserver(o);
	}
	
	public ServerListener getServerListener(){
		return serverListener;
	}
	
	public void sendRequest(Request request){
		try{
			queueRequest.put(request);
		}catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	public void exit(){
		
		try {
			serverRequester.close();
			serverListener.close();
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class ServerRequester implements Runnable{
		private ObjectOutputStream output;
		private boolean isDone=false;
		Request request;

		ServerRequester(){
			try{
				System.out.println("Storzono SerwerRequetser");
				output = new ObjectOutputStream(serverSocket.getOutputStream());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		@Override
		public void run() {
			while(!isDone){
				try{
					request=queueRequest.take();
					System.out.println("SR: wysylam request na serwer");
					output.writeObject(request);
				}
				catch(Exception e){
					isDone=true;
					//e.printStackTrace();
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


	private class ServerListener extends Observable implements Runnable{
		private ObjectInputStream input;
		private boolean isDone=false;
		Request request;
		

		ServerListener(){
			try{
				System.out.println("Storzono serwer listener");
				input = new ObjectInputStream(serverSocket.getInputStream());
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			Object inputObject;
			while(!isDone){
				try{
					inputObject=input.readObject();
					request=(Request) inputObject;
					System.out.println("Odbieram od klienta.");
					this.setChanged();
					this.notifyObservers(request);
				}catch(Exception e){
					isDone=true;
					//e.printStackTrace();
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




