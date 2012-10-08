package ru.phsystems.irisx.utils.betaface;

import org.apache.commons.codec.binary.Base64;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * IRIS-X Project
 * Author: Nikolay A. Viguro
 * WWW: smart.ph-systems.ru
 * E-Mail: nv@ph-systems.ru
 * Date: 28.09.12
 * Time: 14:03
 *
 * BetaFace implements.
 */

public class BetaFace {

    public static final int MAXWIDTH = 480;
    public static final int MAXHEIGHT = 640;
    protected String apiKey = "d45fd466-51e2-4701-8da8-04351c872236";
    protected String apiSecret = "171e8465-f548-401d-b63b-caf0dc28df5f";

    protected String serviceURL = "http://www.betafaceapi.com/service.svc";
    protected boolean downScale = true;

    // Constructor
    /////////////////////////////////////////////////

    public BetaFace() {}


    public HttpURLConnection connect(String urlString) throws IOException {

        PrintWriter out = null;
        HttpURLConnection connection = null;

        URL url = new URL(serviceURL+urlString);
        URLConnection uc = url.openConnection();
        connection = (HttpURLConnection) uc;
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-type", "application/xml");

        return connection;
    }



    // Set person (multiple UIDs)
    /////////////////////////////////////////////////

    public String setPerson (String name, ArrayList<String> uidList) throws IOException, JDOMException {

        HttpURLConnection connection = connect("/Faces_SetPerson");

        PrintWriter out = new PrintWriter(connection.getOutputStream());

        Element rootElement = new Element("FacesSetPersonRequest");

        Namespace ns1 = Namespace.getNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
        Namespace ns2 = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        rootElement.addNamespaceDeclaration(ns1);
        rootElement.addNamespaceDeclaration(ns2);

        Element faceUids = new Element("face_uids");

        for(int i=0; i < uidList.size(); i++) {
            String uid = uidList.get(i);

            Namespace ns = Namespace.getNamespace("http://schemas.microsoft.com/2003/10/Serialization/Arrays");
            Element guid = new Element("guid", ns).addContent(uid);
            faceUids.addContent(guid);
        }

        rootElement.addContent(new Element("api_key").addContent(apiKey));
        rootElement.addContent(new Element("api_secret").addContent(apiSecret));
        rootElement.addContent(faceUids);
        rootElement.addContent(new Element("person_id").addContent(name));

        Document doc = new Document(rootElement);

        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        out.println(outputter.outputString(doc));
        out.flush();
        out.close();

        BufferedInputStream input = null;
        try {
            input = new BufferedInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.out.println("MESSAGE: " + connection.getResponseMessage());
        }

        byte[] contents = new byte[64536];

        int bytesRead = 0;
        String strFileContents = "";

        while( (bytesRead = input.read(contents)) != -1){
            strFileContents = new String(contents, 0, bytesRead);
        }

        return strFileContents;
    }

    // Set person
    /////////////////////////////////////////////////

    public boolean setPerson (String name, String uid) throws IOException, JDOMException {

        HttpURLConnection connection = connect("/Faces_SetPerson");

        PrintWriter out = new PrintWriter(connection.getOutputStream());

        Element rootElement = new Element("FacesSetPersonRequest");

        Element faceUids = new Element("face_uids");

            Namespace ns = Namespace.getNamespace("http://schemas.microsoft.com/2003/10/Serialization/Arrays");
            Element guid = new Element("guid", ns).addContent(uid);
            faceUids.addContent(guid);

        rootElement.addContent(new Element("api_key").addContent(apiKey));
        rootElement.addContent(new Element("api_secret").addContent(apiSecret));
        rootElement.addContent(faceUids);
        rootElement.addContent(new Element("person_id").addContent(name));

        Document doc = new Document(rootElement);

        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        out.println(outputter.outputString(doc));
        out.flush();
        out.close();

        BufferedInputStream input = null;
        try {
            input = new BufferedInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.out.println("MESSAGE: " + connection.getResponseMessage());
        }

        byte[] contents = new byte[64536];

        int bytesRead = 0;
        String strFileContents = "";

        while( (bytesRead = input.read(contents)) != -1){
            strFileContents = new String(contents, 0, bytesRead);
        }

        SAXBuilder builder = new SAXBuilder();
        Reader in = new StringReader(strFileContents);

        Document resp = builder.build(in);
        Element root = resp.getRootElement();
        String status = root.getChild("int_response").getText();

        if(Integer.valueOf(status) != 1)
        {
            return true;
        }
        return false;
    }

    // Recognize person
    /////////////////////////////////////////////////

    public String getRecognizeUid (ArrayList<String> uidList, String person) throws IOException, JDOMException {

        HttpURLConnection connection = connect("/Faces_Recognize");

        PrintWriter out = new PrintWriter(connection.getOutputStream());

        Element rootElement = new Element("FacesRecognizeRequest");

        Element faceUids = new Element("faces_uids");

        for(int i=0; i < uidList.size(); i++) {
            String uid = uidList.get(i);

            Namespace ns = Namespace.getNamespace("http://schemas.microsoft.com/2003/10/Serialization/Arrays");
            Element guid = new Element("guid", ns).addContent(uid);
            faceUids.addContent(guid);
        }

        rootElement.addContent(new Element("api_key").addContent(apiKey));
        rootElement.addContent(new Element("api_secret").addContent(apiSecret));
        rootElement.addContent(faceUids);
        rootElement.addContent(new Element("group_results").addContent("false"));

        Element targets = new Element("targets");

        Namespace nsTargets = Namespace.getNamespace("http://schemas.microsoft.com/2003/10/Serialization/Arrays");
        Element string = new Element("string", nsTargets).addContent(person);
        targets.addContent(string);

        rootElement.addContent(targets);

        Document doc = new Document(rootElement);

        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        out.println(outputter.outputString(doc));
        out.flush();
        out.close();

        BufferedInputStream input = null;
        try {
            input = new BufferedInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.out.println("MESSAGE: " + connection.getResponseMessage());
        }

        byte[] contents = new byte[64536];

        int bytesRead = 0;
        String strFileContents = "";

        while( (bytesRead = input.read(contents)) != -1){
            strFileContents = new String(contents, 0, bytesRead);
        }

        SAXBuilder builder = new SAXBuilder();
        Reader in = new StringReader(strFileContents);

        Document resp = builder.build(in);
        Element root = resp.getRootElement();
        String uidRecog = root.getChild("recognize_uid").getText();

        return uidRecog;
    }


    // Send file to Betaface
    /////////////////////////////////////////////////

    public String sendFile (Document doc) throws IOException, JDOMException {

        HttpURLConnection connection = connect("/UploadNewImage_File");

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
                System.out.println("MESSAGE: " + connection.getResponseMessage());
        }

        byte[] contents = new byte[64536];

        int bytesRead = 0;
        String strFileContents = "";

            while( (bytesRead = input.read(contents)) != -1){
                strFileContents = new String(contents, 0, bytesRead);
            }

        SAXBuilder builder = new SAXBuilder();
        Reader in = new StringReader(strFileContents);

        Document resp = builder.build(in);
        Element root = resp.getRootElement();
        String uid = root.getChild("img_uid").getText();

         ///////////////////////////////////////////

        int isOk = 0;
        String strFileContents2 = null;

        while(isOk == 0)
        {
            HttpURLConnection connectionGetImage = connect("/GetImageInfo");

            PrintWriter outGetImage = new PrintWriter(connectionGetImage.getOutputStream());

            Element rootElement2 = new Element("ImageInfoRequestUid");

            Namespace ns1 = Namespace.getNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
            Namespace ns2 = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

            rootElement2.addNamespaceDeclaration(ns1);
            rootElement2.addNamespaceDeclaration(ns2);

            rootElement2.addContent(new Element("api_key").addContent(apiKey));
            rootElement2.addContent(new Element("api_secret").addContent(apiSecret));
            rootElement2.addContent(new Element("img_uid").addContent(uid));

            Document doc2 = new Document(rootElement2);

            PrintWriter out2 = new PrintWriter(connectionGetImage.getOutputStream());

            XMLOutputter outputter2 = new XMLOutputter(Format.getPrettyFormat());
            out2.println(outputter2.outputString(doc2));
            out2.flush();
            out2.close();

            BufferedInputStream input2 = null;
            try {
                input2 = new BufferedInputStream(connectionGetImage.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.out.println("MESSAGE: " + connectionGetImage.getResponseMessage());
            }

            byte[] contents2 = new byte[64536];
            int bytesRead2;

            while( (bytesRead2 = input2.read(contents2)) != -1){
                strFileContents2 = new String(contents2, 0, bytesRead2);
            }

            SAXBuilder builder2 = new SAXBuilder();
            Reader in2 = new StringReader(strFileContents);

            Document resp2 = builder.build(in2);
            Element root2 = resp.getRootElement();
            String uid2 = root.getChild("int_response").getText();

            if(Integer.valueOf(uid2) != 1)
            {
                isOk = 1;
            }
            else
            {
                System.out.println("NOT OK NOW!");
            }
        }

        SAXBuilder builder3 = new SAXBuilder();
        Reader in3 = new StringReader(strFileContents2);

        Document resp3 = builder3.build(in3);
        Element root3 = resp3.getRootElement();

        return root3.getChild("faces").getChild("FaceInfo").getChild("uid").getText();
    }

    // Wait for recognize
    /////////////////////////////////////////////////

    public String recognize (String uid) throws IOException, JDOMException, InterruptedException {

        int isOk = 0;
        String strFileContents2 = null;

        while(isOk == 0)
        {
            HttpURLConnection connection = connect("/GetRecognizeResult");

            Element rootElement = new Element("RecognizeResultRequest");

            rootElement.addContent(new Element("api_key").addContent(apiKey));
            rootElement.addContent(new Element("api_secret").addContent(apiSecret));
            rootElement.addContent(new Element("recognize_uid").addContent(uid));

            Document doc = new Document(rootElement);

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
                System.out.println("MESSAGE: " + connection.getResponseMessage());
            }

            byte[] contents = new byte[64536];

            int bytesRead = 0;
            String strFileContents = "";

            while( (bytesRead = input.read(contents)) != -1){
                strFileContents = new String(contents, 0, bytesRead);
            }

            SAXBuilder builder = new SAXBuilder();
            Reader in = new StringReader(strFileContents);

            Document resp = builder.build(in);
            Element root = resp.getRootElement();
            String uids = root.getChild("int_response").getText();

            if(Integer.valueOf(uids) != 1)
            {
                isOk = 1;
                System.out.println("GOT ANSWER: "+strFileContents);
            }
            else
            {
                System.out.println("NOT OK NOW!");
                Thread.sleep(1000);
            }
        }

        SAXBuilder builder3 = new SAXBuilder();
        Reader in3 = new StringReader(strFileContents2);

        Document resp3 = builder3.build(in3);
        Element root3 = resp3.getRootElement();

        return "ok";
    }


    //  Convert byte array image to BufferedImage
    /////////////////////////////////////////////////

    public BufferedImage checkSize(byte[] image) throws IOException
    {
        InputStream in = new ByteArrayInputStream( image );
        BufferedImage buffImage = ImageIO.read(in);

        int origWidth = buffImage.getWidth();
        int origHeight = buffImage.getHeight();

        if(origHeight > MAXHEIGHT || origWidth > MAXWIDTH)
        {
            buffImage = resizeImage(buffImage, MAXHEIGHT, MAXWIDTH);
        }

        return buffImage;
    }

    //  Creating send image to betaface XML doc
    /////////////////////////////////////////////////

    public Document sendImageXML(byte[] image) throws IOException
    {
        Element rootElement = new Element("ImageRequestBinary");

        Namespace ns1 = Namespace.getNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
        Namespace ns2 = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        rootElement.addNamespaceDeclaration(ns1);
        rootElement.addNamespaceDeclaration(ns2);

        rootElement.addContent(new Element("api_key").addContent(apiKey));
        rootElement.addContent(new Element("api_secret").addContent(apiSecret));
        rootElement.addContent(new Element("detection_flags").addContent("0"));
        rootElement.addContent(new Element("imagefile_data").addContent(encode64(image)));
        rootElement.addContent(new Element("original_filename").addContent("temp.jpg"));

        return new Document(rootElement);
    }

    //  Encode byte array to Base64
    /////////////////////////////////////////////////

    public String encode64(byte[] image)
    {
        byte[] encoded = Base64.encodeBase64(image);
        return new String(encoded);
    }

    //  Decode Base64 String to byte array
    /////////////////////////////////////////////////

    public byte[] decode64(String image)
    {
        return new Base64().decode(image);
    }

    // Resize image
    /////////////////////////////////////////////////

    public BufferedImage resizeImage(BufferedImage original, int height, int width)
    {
        int type = original.getType() == 0? BufferedImage.TYPE_INT_ARGB : original.getType();

        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(original, 0, 0, height, width, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        return resizedImage;
    }

    public byte[] readFile(String filename) throws IOException {

        File file = new File(filename);
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        // Close the input stream and return bytes
        is.close();

        return bytes;
    }

    // Test
    //////////////////////////////////

    public static void main(String[] args) throws IOException, JDOMException, InterruptedException {

        BetaFace bf = new BetaFace();
        byte[] bytes = bf.readFile("1.jpg");
        byte[] bytes2 = bf.readFile("2.jpg");

        Document doc = bf.sendImageXML(bytes);
        Document doc2 = bf.sendImageXML(bytes2);

        String uid = bf.sendFile(doc);
        String uid2 = bf.sendFile(doc2);
        System.out.println("UID1: "+uid+"\nUID2: "+uid2);

        boolean setP = bf.setPerson("Nikolay Viguro", uid);
        System.out.println("PERSON SET is "+setP);

        ArrayList<String> list = new ArrayList<String>();
        list.add(uid);
        list.add(uid2);

        String recogUid = bf.getRecognizeUid(list, "Nikolay Viguro");
        System.out.println("RECOGNIZE UID: "+recogUid);

        String ok = bf.recognize(recogUid);

    }

}

