package moreheadcaptioning.uncglass.com.cgemoreheadcaptioning;

import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.widget.CardBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by adam on 11/14/14.
 */
public class TextRenderer implements Runnable{
    private static String filepath = "/sdcard/DCIM/Camera/";
    //use Environment.getExternalStorageDirectory().getPath()?

    CardBuilder card;
    LinkedQueue<Display> mQueue;
    Timer t;
    public TextRenderer(CardBuilder c, Queue<Display> q){
        card = c;
        mQueue = (LinkedQueue<Display>) q;
        t = new Timer();
    }

    public void populateQueue(){
        ///First get output.txt stuff
        try {
            BufferedReader in = new BufferedReader(new FileReader(filepath + "Output.txt"));
            readInput(mQueue, in);
            Log.w("HELLO 2 FROM TEXTREADER", "FINISHED OUTPUT");
        } catch(Exception e) {
            Log.w("HELLO 2 FROM TEXTREADER", "GOT CAUGHT IN POPULATE QUEUE");
            ScriptParsing.parseInput();							// Transforms unaltered script into a formated output file
            try {
                BufferedReader in = new BufferedReader(new FileReader(filepath + "Output.txt"));
                readInput(mQueue, in);
            } catch(FileNotFoundException err) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

//    public void startRun(){
//        t.schedule(this, 0);
//    }

    private double prevTime = 0;
    public void run(){
        while (!mQueue.isEmpty()) {
            Log.w("CAPTIONING", "running the loop...");
            try {
                Display tmp = mQueue.dequeue();
                int temp = tmp.text.length();
                if ( temp > 50 ) {
                    for (int i = 0; i < temp; i++) {
                        card.setText(tmp.text.substring(0, i));
                        SystemClock.sleep((long) ((tmp.time - prevTime) * 1000 / temp));
                    }
                } else {
                    card.setText(tmp.text);
                    SystemClock.sleep((long) ((tmp.time - prevTime) * 1000));
                }

                Log.w("CAPTIONING", tmp.text );
                prevTime = tmp.time;							// Assign current time to prevTime

            } catch (IndexOutOfBoundsException e) {				// Error thrown if queue is empty while attempting to dequeue
                Log.w("CAPTIONING", "exiting...");
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    /*
     * Create BufferedReader Object from file "Output.txt"
     * If "Output.txt" does not exist, it is created by calling parseInput()
     */
    public void readInput(Queue<Display> q) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(filepath + "Output.txt"));
            readInput(q, in);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
     * Displays the text property of the next node at the correct time
     */
    public void displayCaptioning(Queue<Display> q, CardBuilder card) {
        double prevTime = 0;
        while (!q.isEmpty()) {
            try {
                Display tmp = q.dequeue();
                Thread.sleep((long) ((tmp.time - prevTime) * 1000));
                card.setText(tmp.text);
                prevTime = tmp.time;							// Assign current time to prevTime
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            } catch (IndexOutOfBoundsException e) {				// Error thrown if queue is empty while attempting to dequeue
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    /*
     * Parses the file "Output.txt" into nodes with a 'text' and 'time' property
     */
    private void readInput(Queue<Display> q, BufferedReader in) {
        for (int i=0; i<4; i++){
            try {
                String num = in.readLine();						// Read line representing time
                //if (num == null) break;
                double time = calculateTime(num);				// Returns time in seconds

                String text = in.readLine();					// Read line representing text
                Display node = new Display(text, time);			// Create new object of Display with 'text' and 'time' properties
                q.enqueue(node);								// Enqueue the new node
            } catch(Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    /*
     * Calculates the time to display text in seconds
     */
    public double calculateTime(String num) {
        double time;

        String[] split = num.split(";");
        if (split.length == 1) {								// If seconds and tenths of a second were separated by a colon
            split = num.split(":");								// Split string on colon
			/*
			 * Calculate hours, minutes, seconds, and tenths of a second and add them together
			 */
            time = 3600 * Double.parseDouble(split[0]);			// Calculate number of hours
            time += 60 * Double.parseDouble(split[1]);			// Calculate number of minutes
            time += Double.parseDouble(split[2]);				// Calculate number of seconds
            time += Double.parseDouble(split[3]) / 100;			// Calculate tenths of a second
        }
        else {													// If seconds and tenths of a second were separated by a semicolon
			/*
			 * Calculate hours, minutes, seconds, and tenths of a second and add them together
			 */
            time = Double.parseDouble(split[1]) / 100;			// Calculate tenths of a second
            split = split[0].split(":");						// Split remaining string on colon
            time += 3600 * Double.parseDouble(split[0]);		// Calculate number of hours
            time += 60 * Double.parseDouble(split[1]);			// Calculate number of minutes
            time += Double.parseDouble(split[2]);				// Calculate number of seconds
        }
        return time;
    }
}
