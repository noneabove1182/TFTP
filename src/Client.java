/**
 * SYSC 3303 - ASSIGNMENT 1
 * 
 * @author Mohammed Ahmed-Muhsin
 * @version 2.0 
 * The client will be creating and sending a DatagramPacket with either a read request or a write request
 * This version of the program will have a generic request with a string. 
 * The request will be sent to the intermediate and the client will wait until it receives a response from the intermediate
 * This version will enable a read request following the read request format outlined in the Assignment
 */

import java.io.*;
import java.net.*;


public class Client {
	
	// socket deceleration for the required socket
	DatagramSocket sendReceiveSocket;
	
	// packet deceleration for the packets being sent and received
	DatagramPacket sendPacket, receivePacket;
	
	// declare filename and mode to be used
	String filenameString, modeString;
	byte filenameBytes[], modeBytes[];
	
	// create a 0, 1, and 2 byte
	final byte zero = (byte)0;
	final byte one = (byte)1;
	final byte two = (byte)2;
	
	// the messages that will be sent are stored in these variables 
	static byte readMsg[];
	static byte writeMsg[];
	
	// default constructor for Intermediate class 
	public Client(){
		
		// initialize the String variables that will be passed onto the program
		filenameString = "moh.txt";
		modeString = "netascii";
		
		// change string values to bytes
		filenameBytes = filenameString.getBytes();
		modeBytes = modeString.getBytes();
		
		// initialize the messages
		readMsg = readRqst();
		writeMsg = writeRqst();
		
		System.out.println("edited4");
		
	}
	
	/**
	 * This procedure will create the read request
	 * @return a byte array with a read message
	 */
	private byte[] readRqst(){
		
		return createMsg(one, filenameBytes, modeBytes);
	}
	
	/**
	 * This procedure will create the write request
	 * @return a byte array with a write message
	 */
	private byte[] writeRqst(){
		
		return 	createMsg(two, filenameBytes, modeBytes);
	}
	
	/**
	 * This procedure will create the invalid request
	 * @return a byte array with an invalid message
	 */
	// send and receive procedure for the packet
	// this procedure will also be able to accept a request depending on its arguments
	/**
	 * @param rqstMsg a byte array which will have the message attached to the packet being sent
	 */
	private void sendReceive(byte[] rqstMsg)
	{
		// initialize the DatagramSocket sendReceive to bind to any available port
		try {
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException se){
			System.err.println("Socket exception error: " + se.getMessage());
		}
		
		System.out.println("Client has started...");
				
		// send the packet to well-known port 68
		try {
			sendPacket = new DatagramPacket(rqstMsg, rqstMsg.length, InetAddress.getLocalHost(), 68);
		} catch (UnknownHostException uhe) {
			System.err.println("Unknown host exception error: " + uhe.getMessage());
		}
		
		System.out.println("Client sending packet to intermediate...");
	    // send the packet to the intermediate via the send socket 
	    try {
	       sendReceiveSocket.send(sendPacket);
	       } catch (IOException ioe) {
	    	System.err.println("Unknown IO exception error: " + ioe.getMessage());
	    }
	    
		// prints out the information on the sent packet
		System.out.println("Client packet sent to intermediate...");
		printInformation(sendPacket);

		
		// prepare to receive a packet
		byte rply[] = new byte[100];
		receivePacket = new DatagramPacket(rply, rply.length);
		
		// block until you receive a packet
		// TODO ADD A TIME OUT ON IT 
		try {
			System.out.println("Client receiving packet from intermediate...");
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException ioe) {
			System.err.println("IO Exception error: " + ioe.getMessage());
		}
		
		// prints out the information on the received packet
		printInformation(receivePacket);
		System.out.println("Client packet received.. closing open sockets");
		
		// close the socket
		sendReceiveSocket.close();
		
	}

	/**
	 * the following method will be called when trying to print out information about a specific packet
	 * @param p the information displayed desired for this packet
	 */
	private void printInformation(DatagramPacket p) {
		
		// print out the information on the packet
		System.out.println("Host: " + p.getAddress());
		System.out.println("Host port: " + p.getPort());
		System.out.println("Containing the following \nString: " + new String(p.getData()));
		System.out.println("Length of packet: " + p.getLength());
		System.out.println("Bytes: ");
		for (int i = 0; i < p.getLength(); i++) {
			System.out.print(Integer.toHexString(p.getData()[i]));
		}
		System.out.println("\n\n");
		
	}
	
	/**
	 * @param rqstByte will be 0 or 1 depending if it is a read or write request, otherwise invalid
	 * @param file the file name
	 * @param mode the mode 
	 * @return a byte array for the message that we are sending
	 */
	private byte[] createMsg(byte rqstByte, byte file[], byte mode[]) {
		// the message will be of size 4 (0, rqstByte, 0, 0) with the file length and mode length
		
		//Create a byte array output stream
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		//write bytes to outputstream
		outputStream.write( (byte)0 );
		outputStream.write( rqstByte );
		outputStream.write( file, 0, file.length );
		outputStream.write( (byte)0 );
		outputStream.write( mode, 0, mode.length );
		outputStream.write( (byte)0 );
		
		byte[] message = outputStream.toByteArray();
		
		return message;
		
	}
	
	private void run(){
		for (int i = 0; i < 4; i++) {
			sendReceive(readMsg);
			sendReceive(writeMsg);
		}
	}
	
	// our main program
	public static void main(String[] args) {
		Client client = new Client();
		
		client.run();
		// repeat the read and write requests 4 times, alternating
		
		
	}
}
