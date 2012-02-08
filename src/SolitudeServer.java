/*
 * Project: 		SolitudeServer
 * File: 			SolitudeServer.java
 * Created by: 		roger, Feb 8, 2012
 */

//package ;

import processing.core.*;
import controlP5.*;
import oscP5.*;
import netP5.*;
import java.util.Vector;

public class SolitudeServer extends PApplet {
	
	private class Shape{
		float x, y, thick;
	}
	/*
	private class Player{
		Vector<Shape> shapes;
	}*/

	static final int NUM_PLAYERS = 8;
	static final int CONTROLP5_WIDTH = 100;
	final int CANVAS_WIDTH = screen.width - CONTROLP5_WIDTH;
	final int CANVAS_HEIGHT = 480;
	static String HOST_IN = "127.0.0.1";
	static int PORT_IN = 12000;
	static String HOST_OUT = "127.0.0.127";
	static int PORT_OUT = 12021;
	
	ControlP5 controlP5;
	Textfield tfIpIn, tfPortIn;
	Textfield tfIpOut, tfPortOut;
	
	OscP5 osc;
	NetAddress remoteLocationIn, remoteLocationOut;
	
	Vector<Shape> shapes = new Vector<Shape>(NUM_PLAYERS);
	
	boolean bTest = false;
	

	public void setup() {
		size(CANVAS_WIDTH+CONTROLP5_WIDTH,CANVAS_HEIGHT);
		
		osc = new OscP5(this,PORT_IN);
		remoteLocationIn = new NetAddress(HOST_IN,PORT_IN);
		remoteLocationOut = new NetAddress(HOST_OUT,PORT_OUT);
		
		setGUI();
	}

	public void draw() {
		background(255);

		// guides
		stroke(255-32);
		line(0, height/2, CANVAS_WIDTH, CANVAS_HEIGHT/2);
		line(CANVAS_WIDTH/2, 0, CANVAS_WIDTH/2, CANVAS_HEIGHT);
		line(CANVAS_WIDTH, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		
		if(bTest){
			background(128);
			bTest = false;
		}
				
	}
	
	/***
	 * FOR OSC TESTING.  REMOVE WHEN DONE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	public void sendOsc(){
		OscMessage myMessage = new OscMessage("/test");
		myMessage.add(1); // player
		myMessage.add((float)0.5); // x1
		myMessage.add((float)0.3); // y1
		myMessage.add((float)0.2); // thickness 1
		
		osc.send(myMessage, remoteLocationIn);
	}
	/***
	 * END TESTING
	 */
	
	public void oscEvent(OscMessage theOscMessage){
		print("### received an osc message.");
		  print(" addrpattern: "+theOscMessage.addrPattern());
		  println(" typetag: "+theOscMessage.typetag());
		  
//		  theOscMessage.print();
		  println("player: "+theOscMessage.get(0).intValue());
		  println("x: "+theOscMessage.get(1).floatValue());
		  println("y: "+theOscMessage.get(2).floatValue());
		  println("th: "+theOscMessage.get(3).floatValue());
		  
		  bTest = true;
	}
	
	public void controlEvent(ControlEvent theEvent){
		String name = theEvent.controller().name();
		println(name);
		
		boolean in = false;
		boolean out = false;
		
		if(name == "ip in"){ 
			HOST_IN = tfIpIn.getText();
			in = true;
		}
		if(name == "port in") {
			PORT_IN = Integer.parseInt(tfPortIn.getText());
			in = true;
		}
		if(name == "ip out") {
			HOST_OUT = tfIpOut.getText();
			out = true;
		}
		if(name == "port out") {
			PORT_IN = Integer.parseInt(tfPortOut.getText());
			out = true;
		}

		if(in) remoteLocationIn = new NetAddress(HOST_IN, PORT_IN);
		if(out)remoteLocationOut = new NetAddress(HOST_OUT, PORT_OUT);

	}

	public void setGUI(){
		controlP5 = new ControlP5(this);
		int buttonW = CONTROLP5_WIDTH-5;
		int buttonH = 20;
		int offset = 2;

		// In IP and PORT
		tfIpIn = controlP5.addTextfield("ip in", width - buttonW, 10, buttonW-10, buttonH);
		tfIpIn.setText(HOST_IN);
		tfIpIn.setAutoClear(false);
		tfIpIn.setColorLabel(color(0));
		tfIpIn.setColorBackground(color(228));
		tfIpIn.setColorValueLabel(color(128));
		tfIpIn.captionLabel().style().marginTop = -32;
		
		tfPortIn = controlP5.addTextfield("port in", width - buttonW, buttonH+offset+20, buttonW-10, buttonH);
		tfPortIn.setText(Integer.toString(PORT_IN));
		tfPortIn.setAutoClear(false);
		tfPortIn.setColorLabel(color(0));
		tfPortIn.setColorBackground(color(228));
		tfPortIn.setColorValueLabel(color(128));
		tfPortIn.captionLabel().style().marginTop = -32;

		// Out IP and PORT
		tfIpOut = controlP5.addTextfield("ip out", width - buttonW, (buttonH+offset)*4+10, buttonW-10, buttonH);
		tfIpOut.setText(HOST_OUT);
		tfIpOut.setAutoClear(false);
		tfIpOut.setColorLabel(color(0));
		tfIpOut.setColorBackground(color(228));
		tfIpOut.setColorValueLabel(color(128));
		tfIpOut.captionLabel().style().marginTop = -32;
		
		tfPortOut = controlP5.addTextfield("port out", width - buttonW, (buttonH+offset)*5+20, buttonW-10, buttonH);
		tfPortOut.setText(Integer.toString(PORT_OUT));
		tfPortOut.setAutoClear(false);
		tfPortOut.setColorLabel(color(0));
		tfPortOut.setColorBackground(color(228));
		tfPortOut.setColorValueLabel(color(128));
		tfPortOut.captionLabel().style().marginTop = -32;
		
		// Play button
		controlP5.addButton("play",0, width - buttonW, (buttonH+offset)*7+10, buttonW, buttonH);
		
	}
	
	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#FFFFFF", "SolitudeServer" });
	}
}
