package pl.bsp.arduino;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import pl.bsp.entities.Resource;
import pl.bsp.model.User;
import pl.bsp.services.ArduinoService;
import pl.bsp.services.ArduinoServiceImpl;
import pl.bsp.services.UserServiceImpl;


public class ObserveMotion {
	ArduinoService ardServ = new ArduinoServiceImpl();
	private String arduinoIp;
	private String userName;

	List<Resource> resources = new ArrayList<>();


	public ObserveMotion(String arduinoIp, User owner) {
		this.arduinoIp = arduinoIp;
		this.userName = owner.getUsername();

		for(pl.bsp.model.Resource res : owner.getResources()) {
			Resource newRes = new Resource();
			newRes.setSerial_id(res.getSerialId());
			newRes.setResourceType(res.getResourceType());
			newRes.setLocalization(res.getLocalization());
			newRes.setDescription(res.getDescription());
			newRes.setName(res.getName());
			newRes.setUsername(res.getUser().getUsername());
			resources.add(newRes);
		}
		(new Thread(new QueueCheckingThread())).start();
	}

	public void addResource(Resource resource) {
		resources.add(resource);
		System.out.println("Dodalem " + resource.getName());
	}

	public String getUserName() {
		return userName;
	}

	public class QueueCheckingThread implements Runnable {

		public void run() {
			String received = null;
			byte[] receiveData = new byte[1024];
			String valueToSend = "T";
			byte[] sendData = valueToSend.getBytes();
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			DatagramSocket serverSocket = null;
			try {
				serverSocket = new DatagramSocket();
			} catch (SocketException e) {
				e.printStackTrace();
			}

			try {
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
						InetAddress.getByName(arduinoIp), 8888);
				serverSocket.send(sendPacket);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			boolean errorOccured = false;
			while (!errorOccured) {
				try {
					serverSocket.receive(receivePacket);
					received = new String(receivePacket.getData());
					System.out.println(received);
					System.out.println((int) received.charAt(2));
					if (received.charAt(2) == 0) {
						System.out.println("WIFIE");
						for (Resource r : resources) {
							System.out.println(r.getName());
							if (r.getResourceType().equals("Active object")) {
								ardServ.turnOffTheLight(arduinoIp, r.getSerial_id());
							}
						}
					}
					if (received.charAt(2) == 1) {
						System.out.println("WIFIE");
						for (Resource r : resources) {
							System.out.println(r.getName());
							if (r.getResourceType().equals("Active object")) {
								ardServ.turnOnTheLight(arduinoIp, r.getSerial_id());
							}
						}
					}
				} catch (SocketTimeoutException e) {
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

}
