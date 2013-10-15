package ru.phsystems.irisx.utils.betaface;

import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * IRIS-X Project
 * Author: Nikolay A. Viguro
 * WWW: smart.ph-systems.ru
 * E-Mail: nv@ph-systems.ru
 * Date: 28.09.12
 * Time: 14:03
 *
 * BetaFace.com API implements.
 */

public class BetaFace {

    public static final int MAXWIDTH = 480;
    public static final int MAXHEIGHT = 640;
    public static boolean debug = false;

    protected final String apiKey = "d45fd466-51e2-4701-8da8-04351c872236";
    protected final String apiSecret = "171e8465-f548-401d-b63b-caf0dc28df5f";
    protected final String serviceURL = "http://www.betafaceapi.com/service.svc";

    private static Logger log = Logger.getLogger(BetaFace.class.getName());

    // Constructor
    /////////////////////////////////////////////////

    public BetaFace() {}

     // Common method for send info
    public String process(String urlString, Document doc) throws IOException {

        HttpURLConnection connection = null;

        if(debug)
        log.info("Send request - "+urlString);

        URL url = new URL(serviceURL+urlString);
        URLConnection uc = url.openConnection();
        connection = (HttpURLConnection) uc;
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-type", "application/xml");

        PrintWriter out = new PrintWriter(connection.getOutputStream());

        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        out.println(outputter.outputString(doc));
        out.flush();
        out.close();

        BufferedInputStream input = null;
        try {
            input = new BufferedInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            if(debug)
            log.info("Error: " + connection.getResponseMessage());
        }

        byte[] contents = new byte[64536];

        int bytesRead = 0;
        String content = "";

        while( (bytesRead = input.read(contents)) != -1){
            content += new String(contents, 0, bytesRead);
        }

        return content;
    }


    // Test
    //////////////////////////////////

    public static void main(String[] args) throws IOException, JDOMException, InterruptedException {

        System.out.println("Start BetaFace API test!");

        // set debug to true
        BetaFace.debug = true;

        // image settings
        // we can load image from file, byte array or http url
        Image img1 = new Image("1.jpg");
        Image img2 = new Image("2.jpg");
        Image img3 = new Image(new URL("http://my-hit.ru/images/film/wall/5380/11816_1024.jpg"));

        // get arraylist with detected faces
        ArrayList<Face> faces0 = img1.getFaces();
        ArrayList<Face> faces1 = img2.getFaces();
        ArrayList<Face> faces2 = img3.getFaces();

        // some debug :)
        System.out.println("UID1: "+faces0.get(0).getUID()+"\nUID2: "+faces1.get(0).getUID()+"\nUID3: "+faces2.get(0).getUID()+"\nUID4: "+faces2.get(1).getUID());

        //add person and set friendly name
        Person NikolayViguro = new Person();
        NikolayViguro.setName("Nikolay Viguro");

        // add UIDs, where we present
        NikolayViguro.addUID(faces0.get(0).getUID());
        NikolayViguro.addUID(faces1.get(0).getUID());

        // and save person on betaface
        boolean flag = NikolayViguro.rememberPerson();

        System.out.println("Person saved = " + flag);

        //prepare UID list, witch we will compare our person
        ArrayList<String> list = new ArrayList<String>();
        list.add(faces0.get(0).getUID());
        list.add(faces1.get(0).getUID());
        list.add(faces2.get(0).getUID());
        list.add(faces2.get(1).getUID());

        // compare
        System.out.println("Is Nikolay Viguro present on images? " + NikolayViguro.compareWithUIDs(list));
    }

}

