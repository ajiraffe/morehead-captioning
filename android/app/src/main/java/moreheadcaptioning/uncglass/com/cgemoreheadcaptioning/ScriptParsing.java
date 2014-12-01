package moreheadcaptioning.uncglass.com.cgemoreheadcaptioning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by adam on 11/14/14.
 */
public class ScriptParsing {
    private static String filepath = "/sdcard/DCIM/Camera/";

    public static void parseInput() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(filepath + "Input.txt"));
            String output = toFormat(in);
            File file = new File(filepath + "Output.txt");	// Location to save created file
            file.createNewFile();

            BufferedWriter out = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            out.write(output);									// Write output to file "Output.txt"
            out.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    /*
     * Parses the unaltered script
     */
    private static String toFormat(BufferedReader in) {
        String textfile = "";
        while (true) {
            try {
                String text = in.readLine();
                if (text == null) break;
                if (isTime(text)){
                    //Can get NAME from here by taking
                    //text.substring(11)
                    textfile += "\n" + text.substring(0, 11) + "\n";
                }
                else{
                    textfile += text;
                }
            } catch(Exception e) {
                e.printStackTrace();
                break;
            }
        }
        return textfile;
    }

    private static boolean isTime(String in){
        try{
            Integer.parseInt(in.substring(0,1));
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}
