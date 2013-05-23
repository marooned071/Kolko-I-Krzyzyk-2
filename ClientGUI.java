import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;


public class ClientGUI extends JFrame {
	private ButtonAdapter_accept buttonAdapter_accept;
	private ButtonAdapter_walkover buttonAdapter_walkover;
	private WindowAdapter windowAdapter;
	
	private JButton button_accept;
	private JButton button_walkover;
	
	private JList<String> list_players;
	
	private JLabel label_info;
	private JLabel label_figure;

	private int n; //ilosc kratek
	private String symbol; //figure Symbol ktorym gra gracz
	private final int k=20; //szerokosc kratki (px)
	
	private  Font gridDefaultFont;
	private  Font gridFontXO;
	private  String gameTab[][];
	private int choice[] = { -1, -1 }; //wybor (wspolrzedne) kliknietej kratki
	
	
	private boolean isMouseEnabled=true; //czy mozna klikac po planszy (czy wstawia sie figura)

	ClientGUI(int n, String symbol) {
		
		this.setSize(n*k+120, n*k+120);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		
		this.setTitle("Kolko i Krzyzyk");
		
		this.n=n;
		this.symbol=symbol;

		Grid grid = new Grid(n,symbol);		
		grid.setPreferredSize(new Dimension(n*k + k, n * k + k));

		button_accept= new JButton("Zatwierdz");
		button_walkover = new JButton("Poddaj sie");
		
		label_info = new JLabel("");
		label_figure= new JLabel("");
		label_figure.setFont(new Font("Arial Unicode MS", Font.BOLD, 30));
		

		list_players = new JList<String>();
		list_players.setEnabled(false);
		list_players.setBorder(BorderFactory.createLineBorder(Color.black));
		
		JPanel listPanel = new JPanel(new BorderLayout());
		listPanel.add(list_players,BorderLayout.WEST);
		

		
		JPanel controlPanel = new JPanel();
		controlPanel.add(button_accept);
		controlPanel.add(button_walkover);
		controlPanel.add(label_figure);
//		controlPanel.add(list_players,BorderLayout.EAST);
		
		JPanel mainGridPanel = new JPanel(new BorderLayout());
		mainGridPanel.add(grid, BorderLayout.CENTER);
		mainGridPanel.add(label_info, BorderLayout.SOUTH);
		mainGridPanel.add(listPanel,BorderLayout.EAST);
		
		
		add(controlPanel,BorderLayout.NORTH);
		add(mainGridPanel, BorderLayout.CENTER);

		buttonAdapter_accept = new ButtonAdapter_accept();
		buttonAdapter_walkover = new ButtonAdapter_walkover();
		windowAdapter=new WindowAdapter();
		this.addWindowListener(windowAdapter);
		
		button_accept.addActionListener(buttonAdapter_accept);
		button_walkover.addActionListener(buttonAdapter_walkover);
		
		this.setLocationByPlatform(true);
		this.setVisible(true);
	}
	
	public void setSymbol(String symbol){
		System.out.println("GUI: setSymbol:"+symbol);
		this.symbol=symbol;
		this.setText_labelFiure(symbol);
	}
	
	
	public void setText_labelInfo(String s){
		label_info.setText(s);
	}
	
	public void setText_labelFiure(String symbol){
		label_figure.setText(symbol);
	}
	
	public void setPlayerList(ArrayList<Player> playersList){
		String[] listData = new String[playersList.size()];

		for(int i=0;i<playersList.size();i++){
			listData[i]=playersList.get(i).getName()+" "+playersList.get(i).getSymbol()+" "+playersList.get(i).getPoints();
		}
		list_players.setListData(listData);
	}
	
	public void setCurrentPlayer(int index){
		list_players.setSelectedIndex(index);
	}
	
	public boolean isChoiced(){
		if((choice[0]==-1)&&(choice[1]==-1))
			return false;
		else
			return true;
	}
	
	public int[] getChoice(){
		return choice;
	}
	

	public void addObservers(Observer o) {
		buttonAdapter_accept.addObserver(o);
		buttonAdapter_walkover.addObserver(o);
		windowAdapter.addObserver(o);
	}
	
	public void setEnabled_buttonAccept(boolean b){
		button_accept.setEnabled(b);
	}
	
	public void setEnabled_buttonWalkover(boolean b){
		button_walkover.setEnabled(b);
	}
	
	public void setEnabled_mouse(boolean b){
		isMouseEnabled=b;
	}
	
	public void setExitButton(boolean b){
		if(b){
			this.button_walkover.setText("Wyjscie");
		}
		else{
			this.button_walkover.setText("Poddaj sie");
		}
	}
	
	public void setGameTab(String[][] tab) {
		gameTab=tab;
		choice[0] = -1;
		choice[1] = -1;
		repaint();
	}

	public String[][] getGameTabWithMove() { 
		String[][] tab = gameTab;
		tab[choice[0]][choice[1]] = symbol;
		return tab;
	}
	
	public void showInformationAboutWin(){
		JOptionPane.showMessageDialog(this, "Wygrales! Gratulacje!");
	}
	
	public void showInformationAboutLose(String winnerName, String winnerSymbol){
		JOptionPane.showMessageDialog(this, "Przegrales.\nWygral gracz: "+winnerName+" ("+winnerSymbol+").");
	}
	public void showInformationAboutDraw(){
		JOptionPane.showMessageDialog(this, "Rozgrywka konczy sie remisem.");
	}
	
	public void showNotifyAboutServerError(){
		JOptionPane.showMessageDialog(this, "Blad serwera. Program konczy dzialanie.");
	}
	
	public ButtonAdapter_accept getButtonAdapter_accept(){
		return buttonAdapter_accept;
	}
	
	public ButtonAdapter_walkover getButtonAdapter_walkover(){
		return buttonAdapter_walkover;
	}
	
	public WindowAdapter getWindowAdapter(){
		return windowAdapter;
	}
	
	private class ButtonAdapter_accept extends Observable implements ActionListener {
		public void actionPerformed(ActionEvent event) {				
			setChanged();
			notifyObservers(choice);
		}
	}
	
	private class ButtonAdapter_walkover extends Observable implements ActionListener {
		public void actionPerformed(ActionEvent event) {				
			setChanged();
			notifyObservers();
		}
	}
	
	private class Grid extends JComponent {

		Grid(int n, String symbol) {
			addMouseListener(new MouseHandler());
			gameTab = new String[n][n];
			gridDefaultFont = this.getFont();
			gridFontXO = new Font("Arial Unicode MS", Font.BOLD, k);
		}

		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;

			// rysowanie siatki
			Point2D start;
			Point2D end;
			Line2D line;


			int j = 1;
			int size = n * k;

			for (int i = 0; i < n * k; i += k) {
				g2.drawString(j + "", size + (k/6), i + k - (k/3));
				j++;
			}

			for (int i = 0; i <= n * k; i += k) {
				start = new Point2D.Double(i  , 0);
				end = new Point2D.Double(i , size );
				line = new Line2D.Double(start, end);
				g2.draw(line);
			} 
			for (int i =0; i <= n * k ; i += k) {
				start = new Point2D.Double(0, i );
				end = new Point2D.Double(size , i);
				line = new Line2D.Double(start, end);
				g2.draw(line);
			}
			j = 65;
			for (int i = 0; i < n * k; i += k) {
				g2.drawString((char) j + "", i + k -(2*k/3), size + (2*k/3));
				j++;
			}

			g2.setFont(gridFontXO);
			for (int i = 0; i < n; i++) {
				for (int p = 0; p < n; p++) {
					if(gameTab[i][p]!=null)
						g2.drawString(gameTab[i][p], i * k, p * k + k);
				}
			}

			g2.setColor(Color.RED);

			g2.drawString(symbol, choice[0] * k, choice[1] * k + k);


			g2.setColor(Color.BLACK);

			g2.setFont(gridDefaultFont);
		}

		private class MouseHandler extends MouseAdapter {

			public void mouseClicked(MouseEvent event) {
				if(isMouseEnabled){
					Point p = event.getPoint();
//					System.out.println("mouseClicked");
//					System.out.println(p.toString());
					int x = (int) p.getX() / k;
					int y = (int) p.getY() / k;
//					System.out.println(x + " " + y);
					if (x < n && y < n)
						if (gameTab[x][y]==null) {
							choice[0] = x;
							choice[1] = y;
						}
					repaint();
				}

			}
		}
	}
	
	private class WindowAdapter extends Observable implements WindowListener{

		@Override
		public void windowClosing(WindowEvent e) {
			setChanged();
			this.notifyObservers();
		}
		
		@Override
		public void windowActivated(WindowEvent e) {}
		@Override
		public void windowClosed(WindowEvent e) {}
		@Override
		public void windowDeactivated(WindowEvent e) {}
		@Override
		public void windowDeiconified(WindowEvent e) {}
		@Override
		public void windowIconified(WindowEvent e) {}
		@Override
		public void windowOpened(WindowEvent e) {}

	}
}

