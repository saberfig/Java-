package com.bupt.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class MyChannel extends Thread{
	
	private DataInputStream dis;
	private DataOutputStream dos;
	private boolean flag=true;
	
	public MyChannel(Socket client) {
		try {
			dis=new DataInputStream(client.getInputStream());
			dos=new DataOutputStream(client.getOutputStream());
		}catch(IOException e) {
			flag=false;
			CloseUtils.closeAll(dis,dos);
			Server.list.remove(this);
		}
	}
	
	//接收数据方法
	private String receive() {
		String str="";
		try {
			str=dis.readUTF();
		} catch (IOException e) {
			flag=false;
			CloseUtils.closeAll(dis,dos);
			Server.list.remove(this);
		}
		return str;
	}
	
	//发送数据的方法
	private void send(String str) {
		if(str!=null&&str.length()!=0) {
			try {
				dos.writeUTF(str);
				dos.flush();
			}catch (IOException e) {
				flag=false;
				CloseUtils.closeAll(dis,dos);
				Server.list.remove(this);
			}
		}
	}
	private void sendOther() {
		String str=this.receive();
		List<MyChannel> list=Server.list;
		for(MyChannel other:list) {
			if(other==this) {
				continue;
			}
			other.send(str);
		}
	}
	@Override
	public void run() {
		while(flag) {
			this.sendOther();
		}
	}
}
