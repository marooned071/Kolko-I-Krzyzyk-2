import java.io.Serializable;


public class Player implements Serializable{
	private String name;
	private String symbol;
	private int points;
	
	Player(String name, String symbol, int points){
		this.name=name;
		this.symbol=symbol;
		this.points=points;
	}
	
	
	public void addPoint(){
		this.points++;
	}
	public int getPoints(){
		return points;
	}
	public String getName() {
		return name;
	}
	public void setPoints(int points){
		this.points=points;
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
}
