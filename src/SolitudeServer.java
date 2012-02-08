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
	
	private class Node{
		float x,y,thick;
	}
	private class Shape{
		Vector<Node> nodes = new Vector<Node>();
	}

	static final int NUM_PLAYERS = 8;
	static final int CONTROLP5_WIDTH = 100;
	final int CANVAS_WIDTH = screen.width - CONTROLP5_WIDTH;
	final int CANVAS_HEIGHT = 480;
//	static String HOST_IN = "127.0.0.1";
	static int LISTEN_PORT = 12000;
	static String HOST = "127.0.0.1";
	static int PORT = 1234;
	
	
	ControlP5 gui;
	Textfield tfIpIn, tfPortIn;
	Textfield tfIpOut, tfPortOut;
	
	OscP5 osc;
	NetAddress remoteLocationIn, remoteLocationOut;
	
	Vector<Shape> shapes = new Vector<Shape>(NUM_PLAYERS);
	int r = 228;
	int g = 228;
	int b = 228;
	int a = 128;
	int p1color = color(r,0,0);
	int p2color = color(r,g,0);
	int p3color = color(r,0,b);
	int p4color = color(0,g,0);
	int p5color = color(r,g,b);
	int p6color = color(0,0,b);
	int p7color = color(r,a,0);
	int p8color = color(0,a,b);
	int playerColors[] = {p1color, p2color, p3color, p4color, p5color, p6color, p7color, p8color};
	
//	boolean bTest = false;
	
	static int scannerX = 0;
	boolean bPlay = false;
	

	public void setup() {
		size(CANVAS_WIDTH+CONTROLP5_WIDTH,CANVAS_HEIGHT);
		smooth();
		
		osc = new OscP5(this,LISTEN_PORT);
//		remoteLocationIn = new NetAddress(HOST_IN,PORT_IN);
		remoteLocationOut = new NetAddress(HOST,PORT);

		for (int i = 0; i < NUM_PLAYERS; i++) {
			shapes.add(new Shape());
		}
		
		
		setGUI();
	}

	public void draw() {
		background(255);

		// guides
		stroke(255-32);
		line(0, height/2, CANVAS_WIDTH, CANVAS_HEIGHT/2);
		line(CANVAS_WIDTH/2, 0, CANVAS_WIDTH/2, CANVAS_HEIGHT);
		line(CANVAS_WIDTH, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		
//		if(bTest){
//			background(128);
//			bTest = false;
//		}
		
		drawShapes();
		
		// Scanner head - CAP‚AL		
		if(bPlay){
			scannerX++;
		}

		stroke(color(255,0,0));
		line(scannerX, 0, scannerX, height);
	}
	
	public void drawShapes(){
		for (int i = 0; i < shapes.size(); i++) {
			fill(playerColors[i]);
			beginShape();
			Shape shape = shapes.elementAt(i);
			for (int j = 0; j < shape.nodes.size(); j++) {
				Node node = shape.nodes.elementAt(j);
				float x = node.x * CANVAS_WIDTH;
				float y = node.y * CANVAS_HEIGHT - (node.thick/2) * CANVAS_HEIGHT;
				vertex(x, y);
			}
			for (int j = shape.nodes.size()-1; j >= 0; j--) {
				Node node = shape.nodes.elementAt(j);
				float x = node.x * CANVAS_WIDTH;
				float y = node.y * CANVAS_HEIGHT + (node.thick/2) * CANVAS_HEIGHT;
				vertex(x, y);
			}
			endShape();
		}
	}

	
	/***
	 * FOR OSC TESTING.  REMOVE WHEN DONE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	public void keyPressed(){
		switch (key) {
		case ' ':
			println(shapes.size());
			println(shapes.elementAt(0).nodes.size());
			break;
		case 's':
			sendTestOsc();
		default:
			break;
		}
	}
	public void sendTestOsc(){
		OscMessage myMessage = new OscMessage("/test");
		myMessage.add(1); // player
		myMessage.add(random(0,1)); // x1
		myMessage.add(random((float)0,(float)0.5)); // y1
		myMessage.add(random((float)0,(float)0.5)); // thickness 1
		myMessage.add(random(0,1)); // x1
		myMessage.add(random((float)0,(float)0.5)); // y1
		myMessage.add(random((float)0,(float)0.5)); // thickness 1
		
		osc.send(myMessage, remoteLocationOut);
		
		println("sendig osc through port: "+PORT);
	}
	/***
	 * END TESTING
	 */
	
	public void oscEvent(OscMessage theOscMessage){
		print("------------------------------\nSOLITUDE SERVER\n\n### received an osc message.");
		print(" addrpattern: "+theOscMessage.addrPattern());
		println(" typetag: "+theOscMessage.typetag());

//		println("typetag length: "+theOscMessage.typetag().length());
		int num_args = theOscMessage.typetag().length();
		int playerID = theOscMessage.get(0).intValue();
//		println("player ID: "+playerID);
		Shape shape = new Shape();
		shape.nodes.clear();
		for (int i = 1; i < num_args; i += 3) {
			Node node = new Node();
			node.x = theOscMessage.get(i).floatValue();
			node.y = theOscMessage.get(i+1).floatValue();
			node.thick = theOscMessage.get(i+2).floatValue();
			shape.nodes.add(node);
		}
		shapes.setElementAt(shape, playerID-1);
		  
		println("---------------------------------");
//		  bTest = true;
	}
	
	public void controlEvent(ControlEvent theEvent){
		String name = theEvent.controller().name();
		println(name);
		
		boolean in = false;
		boolean out = false;
		
//		if(name == "ip in"){ 
//			HOST_IN = tfIpIn.getText();
//			in = true;
//		}
		if(name == "port in") {
			LISTEN_PORT = Integer.parseInt(tfPortIn.getText());
			in = true;
		}
		if(name == "ip out") {
			HOST = tfIpOut.getText();
			out = true;
		}
		if(name == "port out") {
			PORT = Integer.parseInt(tfPortOut.getText());
			out = true;
		}

		if(in) osc = new OscP5(this, LISTEN_PORT);
		if(out)remoteLocationOut = new NetAddress(HOST, PORT);
		
		if(name == "play") bPlay = !bPlay;
	}

	public void setGUI(){
		gui = new ControlP5(this);
		int buttonW = CONTROLP5_WIDTH-5;
		int buttonH = 20;
		int offset = 2;

		// In IP and PORT
//		tfIpIn = controlP5.addTextfield("ip in", width - buttonW, 10, buttonW-10, buttonH);
//		tfIpIn.setText(HOST_IN);
//		tfIpIn.setAutoClear(false);
//		tfIpIn.setColorLabel(color(0));
//		tfIpIn.setColorBackground(color(228));
//		tfIpIn.setColorValueLabel(color(128));
//		tfIpIn.captionLabel().style().marginTop = -32;
		
		tfPortIn = gui.addTextfield("port in", width - buttonW, buttonH+offset+20, buttonW-10, buttonH);
		tfPortIn.setText(Integer.toString(LISTEN_PORT));
		tfPortIn.setAutoClear(false);
		tfPortIn.setColorLabel(color(0));
		tfPortIn.setColorBackground(color(228));
		tfPortIn.setColorValueLabel(color(128));
		tfPortIn.captionLabel().style().marginTop = -32;
		tfPortIn.captionLabel().set("Listening to port #");

		// Out IP and PORT
		tfIpOut = gui.addTextfield("ip out", width - buttonW, (buttonH+offset)*4+10, buttonW-10, buttonH);
		tfIpOut.setText(HOST);
		tfIpOut.setAutoClear(false);
		tfIpOut.setColorLabel(color(0));
		tfIpOut.setColorBackground(color(228));
		tfIpOut.setColorValueLabel(color(128));
		tfIpOut.captionLabel().style().marginTop = -32;
		
		tfPortOut = gui.addTextfield("port out", width - buttonW, (buttonH+offset)*5+20, buttonW-10, buttonH);
		tfPortOut.setText(Integer.toString(PORT));
		tfPortOut.setAutoClear(false);
		tfPortOut.setColorLabel(color(0));
		tfPortOut.setColorBackground(color(228));
		tfPortOut.setColorValueLabel(color(128));
		tfPortOut.captionLabel().style().marginTop = -32;
		
		// Play button
		Toggle t = gui.addToggle("play", width - buttonW, (buttonH+offset)*7+10, buttonW, buttonH);
		t.setColorActive(color(0,128,0));
		t.setColorBackground(color(128,0,0));
		t.captionLabel().set("PLAY / STOP");
		t.captionLabel().style().marginTop = -17;
		t.captionLabel().style().marginLeft = 10;
	}
	
	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#FFFFFF", "SolitudeServer" });
	}
}
