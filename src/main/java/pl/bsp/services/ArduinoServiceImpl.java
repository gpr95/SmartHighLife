package pl.bsp.services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class ArduinoServiceImpl implements ArduinoService {

	@Override
	public String findArduinoInNetwork() {

		String localhostPrefix = null;
		int lastOcetet = 0;
		Enumeration<?> interfaces = null;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) interfaces.nextElement();
				Enumeration<InetAddress> iNetAddress = netInterface.getInetAddresses();
				while (iNetAddress.hasMoreElements()) {
					InetAddress i = iNetAddress.nextElement();

					if (i.getHostAddress().contains(".")) {
						String ipString = i.getHostAddress();
						String tmp = ipString.substring(ipString.lastIndexOf('.') + 1, ipString.length());
						try {
							int lastOctetTmp = Integer.parseInt(tmp);
							if (lastOctetTmp > 1) {
								localhostPrefix = ipString.substring(0, ipString.lastIndexOf('.') + 1);
								lastOcetet = lastOctetTmp;
							}
						} catch (NumberFormatException e) {

						}

					}

				}
			}
		} catch (SocketException e) {
		}

		DatagramSocket serverSocket = null;

		byte[] receiveData = new byte[1024];
		byte[] sendData;
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {
			serverSocket = new DatagramSocket();
			serverSocket.setSoTimeout(50);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		String valueToSend = "XXX";
		sendData = valueToSend.getBytes();

		String received = null;
		InetAddress arduinoIp = null;
		for (int i = lastOcetet + 1; i < 254; i++) {
			try {
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
						InetAddress.getByName(localhostPrefix + i), 8888);
				Thread.sleep(30);
				serverSocket.send(sendPacket);

				serverSocket.receive(receivePacket);

				received = new String(receivePacket.getData());
				System.out.println(received);
				System.out.println(receivePacket.getAddress());
				System.out.println(receivePacket.getSocketAddress());
				arduinoIp = receivePacket.getAddress();
				break;
			} catch (SocketTimeoutException e) {
			} catch (UnknownHostException e) {
			} catch (IOException e) {
			} catch (InterruptedException e) {
			}
		}

		if (received == null)
			for (int i = lastOcetet - 1; i > 1; i--) {
				try {
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
							InetAddress.getByName(localhostPrefix + i), 8888);
					Thread.sleep(30);
					serverSocket.send(sendPacket);

					serverSocket.receive(receivePacket);
					received = new String(receivePacket.getData());

					System.out.println(received);
					System.out.println(receivePacket.getAddress());
					System.out.println(receivePacket.getSocketAddress());
					arduinoIp = receivePacket.getAddress();
				} catch (SocketTimeoutException e) {
				} catch (UnknownHostException e) {
				} catch (IOException e) {
				} catch (InterruptedException e) {
				}
			}

		serverSocket.close();
		
		if (arduinoIp != null)
			return arduinoIp.getHostAddress();
		
		return null;
	}
	
	@Override
	public void addNewResource(String arduinoIp, int resourceId) {
		if(resourceId < 65)
			return;
		char id = (char) resourceId;
		writeMsgToArduino("A",id, arduinoIp);
	}

	@Override
	public void turnOnTheLight(String arduinoIp, int resourceId) {
		char id = (char) resourceId;
		writeMsgToArduino("N",id, arduinoIp);
	}

	@Override
	public void turnOffTheLight(String arduinoIp, int resourceId) {
		char id = (char) resourceId;
		writeMsgToArduino("F" ,id, arduinoIp);
	}

	@Override
	public String getResourceValue(String arduinoIp, int resourceId) {
		char id = (char) resourceId;
		String returnedValue = writeMsgToArduino("G" , id, arduinoIp);
		System.out.println(returnedValue);
		if(returnedValue.startsWith("N"))
			return "ON";
		if(returnedValue.startsWith("F"))
			return "OFF";
		return returnedValue;
		
	}

	private String writeMsgToArduino(String header, char id, String arduinoIp) {
		String received = null;
		String valueToSend = header + id;
		byte[] sendData = valueToSend.getBytes();
		byte[] receiveData = new byte[1024];
		
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket();
			serverSocket.setSoTimeout(100);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		boolean retransmit = false;
		do {
			try {
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
						InetAddress.getByName(arduinoIp), 8888);
				serverSocket.send(sendPacket);
				serverSocket.receive(receivePacket);
				received = new String(receivePacket.getData());
				System.out.println(received);
				retransmit = false;
			} catch (SocketTimeoutException e) {
				retransmit = true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (retransmit);
		serverSocket.close();

		return received;
	}

	

}
