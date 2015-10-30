import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import neurosky.*; 
import java.net.*; 
import org.json.*; 
import SimpleOpenNI.*; 
import fullscreen.*; 

import neurosky.*; 
import org.json.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class NeuroPaintPlus extends PApplet {

float [] x = new float [50];
float [] y = new float [50];






SimpleOpenNI kinect;

 
FullScreen fs;
 
ThinkGearSocket neuroSocket;
int attention = 1;
int meditation = 1;
int blinkSt = 0;
PFont font;
int blink = 0;
int state = 0;

int closestValue;
int closestX;
int closestY;

float lastX;
float lastY;

//declare global variables for previous
//x and y co ordinates
int previousX;
int previousY;

int huE;
 
public void setup() 
{
  // fullscreen
  size(displayWidth, displayHeight);
  ThinkGearSocket neuroSocket = new ThinkGearSocket(this);
  try 
  {
    neuroSocket.start();
  } 
  catch (ConnectException e) {
    //e.printStackTrace();
  }
    kinect = new SimpleOpenNI(this);
  kinect.enableDepth();
  smooth();
  font = createFont("Arial", 64);
  textFont(font);
  colorMode(HSB,100,100,1625);
  noStroke();
    for (int i = 0; i<50; i++) {
    x[i] = 0;
    y[i] = 0;
    }
    background(0);
}
 
public void draw() 
{
  
    if (state == 0) {
    // initialisation screen - game will start when connection signal < 50
    fill(100,100,1625);
    textAlign(CENTER);
    text("Initialising your brain...", displayWidth/2, displayHeight/2);
  }
 
  else if (state == 1) {
  //background(0);
     closestValue = 8000;
  
  kinect.update();
  
   int[] depthValues = kinect.depthMap();
  
  // for each row in the depth image
  for(int y = 0; y < 480; y++){
    for(int x = 0; x < 640; x++){
      
      //reverse x
      int reversedX = 640-x-1;
    
    
      // pull ut the corresponding value from the depth array
      int i = reversedX + y * 640;
      int currentDepthValue = depthValues[i];
      
      // if that pixel is the closest one we've seen so far and
      // is within a range 620 is 2 feet 1525 is 5 feet
      if(currentDepthValue > 610 && currentDepthValue < 1625
      && currentDepthValue < closestValue){
        
      
          //save its value
          closestValue = currentDepthValue;
          // and save its position (both x and y coordinates)
          closestX = x;
          closestY = y;
        }
      }
  } 
   float interpolatedX = lerp(lastX, closestX, 0.3f);
   float interpolatedY = lerp(lastY, closestY, 0.3f);
    
  for (int i=0; i<49; i++) {
    x[i] = x [i+1];
    y[i] = y [i+1];
    
      x[49] = interpolatedX;
  y[49] = interpolatedY;
  lastX = interpolatedX;
  lastY = interpolatedY;
    
    fill (attention, meditation , 1625 - closestValue); //colour i is the circles devided by two makes it lighter
    ellipse (x[49]*2, y[49]*2, i*500/(closestValue), i*500/(closestValue));
    
  
  

  }
  
  //text("Attention: "+attention, 200, 150);
  
  //rect(400, 130, attention*3, 40);

  //text("Meditation: "+meditation, 200, 250);

  //rect(400, 230, meditation*3, 40);
 

 
  if (blink==3) 
  {
    
    {
      background(0);
      blink = 0;
    }
  }
}
}
 

public void poorSignalEvent(int sig) {  
  // waits for when connection signal to the headset is good
  if (sig < 50 && state == 0) {
    state = 1;
  }
  
  else if (sig > 50 && state == 1) {
    state = 0;
  }
    println("SignalEvent "+sig);
}
 
public void attentionEvent(int attentionLevel) 
{
  attention = attentionLevel;
}
 
public void meditationEvent(int meditationLevel) 
{
  meditation = meditationLevel;
}
 
public void blinkEvent(int blinkStrength) 
{
  blinkSt = blinkStrength;
  blink++;
}
 
public void stop() {
  neuroSocket.stop();
  super.stop();
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "NeuroPaintPlus" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
