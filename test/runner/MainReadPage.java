/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 *
 * @author itrc169
 */
public class MainReadPage {
    
    public static void main(String[] args) {
    String urltext = "http://rengganiez.multiply.com/journal/?&page_start=0";
    BufferedReader in = null;
    try {
      URL url = new URL(urltext);
      in = new BufferedReader(new InputStreamReader(url.openStream()));

      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        System.out.println(inputLine);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
    
}
