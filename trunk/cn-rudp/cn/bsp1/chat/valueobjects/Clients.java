package chat.valueobjects;

import java.net.*;

public class Clients {
	
	private String nickName;
	private boolean nickSchonVorhanden;
	private InetSocketAddress socketAddress;
	
	public String getNickName()	{
		return this.nickName;
	}
	
	public void setNickName(String nickName)	{
		this.nickName = nickName;
	}
	
	public boolean getNickSchonVorhanden()	{
		return this.nickSchonVorhanden;
	}
	
	public void setNickSchonVorhanden(boolean nickSchonVorhanden)	{
		this.nickSchonVorhanden = nickSchonVorhanden;
	}
	
	public InetSocketAddress getSocketAddress()	{
		return this.socketAddress;
	}
	
	public void setSocketAddress(InetSocketAddress socketAddress)	{
		this.socketAddress = socketAddress;
	}
	
}