import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class KeyEventClass extends JFrame implements KeyListener {
    
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    ArrayList<String> ipAddresses;
    public int slideCounter = 1;

    public int getSlideCounter() {
        return slideCounter;
    }
    
    public void setSlideCounter(int counter) {
        this.slideCounter = counter;
    }

    public KeyEventClass(ArrayList<String> ipAddresses) {
        this.setLayout(new BorderLayout());
        JTextField field = new JTextField();
        field.addKeyListener(this);
        this.add(field, BorderLayout.CENTER);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setTitle("Bitte im Vordergrund lassen");
        this.setSize(320, 70);

        this.ipAddresses = ipAddresses;
    }

    public void keyTyped(KeyEvent e) {
       //
    }

    boolean pageDownPressed = false;
    boolean pageUpPressed = false;


    public void pageDown(String ipAddress) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + ipAddress + ":8080/pagedown"))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
           
        } catch (Exception e1) {
           e1.printStackTrace();
        }
    }
    

    public void pageUp(String ipAddress) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + ipAddress + ":8080/pageup"))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            
        } catch (Exception e1) {
           e1.printStackTrace();
        }
    }


    /*public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN && !pageDownPressed) {
            for(String ipAddress : ipAddresses) {
                try {
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://" + ipAddress + ":8080/pagedown"))
                            .build();
                    client.send(request, HttpResponse.BodyHandlers.ofString());
                    pageDownPressed = true;
                } catch (Exception e1) {
                   e1.printStackTrace();
                }
            }
        } else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP && !pageUpPressed) {
            for(String ipAddress : ipAddresses) {
                try {
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://" + ipAddress + ":8080/pageup"))
                            .build();
                    client.send(request, HttpResponse.BodyHandlers.ofString());
                    pageUpPressed = true;
                } catch (Exception e1) {
                   e1.printStackTrace();
                }
            }
        }
    }*/

    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN && !pageDownPressed) {
            for(String ipAddress : ipAddresses) {
                new Thread(() -> pageDown(ipAddress)).start();
            }
            pageDownPressed = true;
        } else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP && !pageUpPressed) {
            for(String ipAddress : ipAddresses) {
                new Thread(() -> pageUp(ipAddress)).start();  
            }
            pageUpPressed = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            pageDownPressed = false;
            this.slideCounter++;
            log("Slide loaded: " + getSlideCounter());
        } else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            this.slideCounter--;
            if(this.slideCounter < 1) {
                this.slideCounter = 1;
            }
            log("Slide loaded: " + getSlideCounter());
            pageUpPressed = false;
        }
    }

    // Logger method opens file and appends timestamped event
    public void log(String text) {
        try {
            File file = new File("logs.csv");
            FileWriter fr = new FileWriter(file, true);
            BufferedWriter br = new BufferedWriter(fr);
            PrintWriter pr = new PrintWriter(br);
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
            String toPrint = timeStamp + "; " + text + ";";
            System.out.println(toPrint);
            pr.println(toPrint);
            pr.close();
            br.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}