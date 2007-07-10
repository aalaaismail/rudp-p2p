package chat.valueobjects;

import java.util.*;

public class StoredMessages {
	
	private String message;
	private String anNick;
	private boolean zugestellt;
	private ArrayList abgeholtVon;
	
	public String getMessage()	{
		return this.message;
	}
	
	public void setMessage(String message)	{
		this.message = message;
	}
	
	public String getAnNick()	{
		return this.anNick;
	}
	
	public void setAnNick(String anNick)	{
		this.anNick = anNick;
	}
	
	public boolean getZugestellt()	{
		return this.zugestellt;
	}
	
	public void setZugestellt(boolean zugestellt)	{
		this.zugestellt = zugestellt;
	}
	
	public ArrayList getAbgeholtVon()	{
		return this.abgeholtVon;
	}
	
	public void setAbgeholtVon(ArrayList abgeholtVon)	{
		this.abgeholtVon = abgeholtVon;
	}
	
}