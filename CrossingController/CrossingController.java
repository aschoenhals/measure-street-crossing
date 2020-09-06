
import com.fazecast.jSerialComm.SerialPort;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CrossingController {

    public static ArrayList<String> ipAddresses;
    public static String logViewerIPAddress = "localhost";
    public static KeyEventClass kec;
    public static File file;

    public static void main(String[] args) {
        ipAddresses = new ArrayList<String>();
        startLoggingInNewFile();
        // ADJUST IP ADDRESSES OF CLIENTS
        ipAddresses.add("192.168.1.88"); // Client 1
        ipAddresses.add("192.168.1.35"); // Client 2
        kec = new KeyEventClass(ipAddresses);
        log("start session");
        SerialPort comPort = SerialPort.getCommPorts()[0]; // Select Arduino COM Port
        comPort.openPort();
        while (true) {
            serialListening(comPort);
        }

    }

    public static void serialListening(SerialPort comPort) {

        // System.out.println(comPort.getDescriptivePortName() + " Port opened");
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        InputStream in = comPort.getInputStream();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            String log;
            while ((line = br.readLine()) != null) {
                if (line.equals("Button1 pressed")) {
                    log = "left foot; detected";
                    log(log);
                } else if (line.equals("Button2 pressed")) {
                    log = "right foot; detected";
                    log(log);
                } else if (line.equals("Button1 released")) {
                    log = "left foot; lost";
                    log(log);
                } else if (line.equals("Button2 released")) {
                    log = "right foot; lost";
                    log(log);
                } else if (line.equals("Start")) {
                    nextSlide();
                    log = "slide number " + kec.getSlideCounter() + ";" + "video started";
                    log(log);
                } else if (line.equals("Duration")) {
                    long duration = Long.parseLong(br.readLine());
                    log = "slide number " + kec.getSlideCounter() + ";" + "duration;" + duration;
                    log(log);
                    log = "slide number " + kec.getSlideCounter() + ";" + "video stopped";
                    log(log);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void startLoggingInNewFile() {
        try {
            String fileName = "Logs/Logging_" + new SimpleDateFormat("yyyy-MM-ddHHmmss").format(new Date()) + ".csv";
            file = new File(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Logger method opens file and appends timestamped event
    public static void log(String text) {
        try {
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

    public static void logHTTP(String text) {

        try {
            String body = "parameter=" + text + "\n";

            URL url = new URL("http://" + logViewerIPAddress + ":8080/write");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(body.length()));

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());

            writer.write(body);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            for (String line; (line = reader.readLine()) != null;) {
                System.out.println(line);
            }

            writer.close();
            reader.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // method to pagedown
    public static void nextSlide() throws MalformedURLException {
        kec.setSlideCounter(kec.getSlideCounter() + 1);
        log("Slide loaded: " + kec.getSlideCounter());
        for (String ipAddress : ipAddresses) {
            new Thread(() -> pageDown(ipAddress)).start();
        }
    }

    public static void pageDown(String ipAddress) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + ipAddress + ":8080/pagedown"))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void pageUp(String ipAddress) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + ipAddress + ":8080/pageup"))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
