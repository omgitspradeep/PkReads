package com.example.pkreads.utils;

import android.sax.Element;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

public class ResourceManager {
    public static File selectedFile=null;
    public static String TAG="EPUB";
    public static ArrayList<File> allFiles = new ArrayList<File>();

    public static ArrayList<File> getAllFiles() {
        return allFiles;
    }

    public static void setAllFiles(ArrayList<File> allFiles) {
        ResourceManager.allFiles = allFiles;
    }


    public static boolean isItEPUBFile(String filename){
        boolean flag= false;
        if(filename.endsWith(".epub")){
            flag =true;
        }
        return flag;
    }

    public static File getFile() {
        return selectedFile;
    }

    public static void setSelectedFile(File selectedFile) {
        ResourceManager.selectedFile = selectedFile;
    }
//EPUB SECTION


    //EPUB file is a zip file, the first thing we need is a function that allows us to extract the files from a zip file
    public InputStream fetchFromZip(String fileName) {

        ZipFile mZip = null;
        try {
            mZip = new ZipFile(fileName);
        } catch (IOException e) {
            Log.e(ResourceManager.TAG, "Error opening file", e);
        }

        InputStream in = null;
        ZipEntry containerEntry = mZip.getEntry(fileName);
        if (containerEntry != null) {
            try {
                in = mZip.getInputStream(containerEntry);
            } catch (IOException e) {
                Log.e(ResourceManager.TAG, "Error reading zip file " + fileName, e);
            }
        }
        return in;
    }

    //We will also need to parse the XML files. For that we use SAX parser. le, IBM's DeveloperWorks has an excellent article on the options. We're going to use a SAX parser.
    // The basic idea of the SAX approach is you write a ContentHandler and plug it into the SAX pipeline.

    void parseXmlResource(String fileName, ContentHandler handler) {
        InputStream in = fetchFromZip(fileName);
        if (in != null) {
            try {
                try {
                    // obtain XML Reader
                    SAXParserFactory parseFactory = SAXParserFactory.newInstance();
                    XMLReader reader = parseFactory.newSAXParser().getXMLReader();

                    // connect reader to content handler
                    reader.setContentHandler(handler);

                    // process XML
                    InputSource source = new InputSource(in);
                    source.setEncoding("UTF-8");
                    reader.parse(source);
                } finally {
                    in.close();
                }
            } catch (ParserConfigurationException e) {
                Log.e(ResourceManager.TAG, "Error setting up to parse XML file ", e);
            } catch (IOException e) {
                Log.e(ResourceManager.TAG, "Error reading XML file ", e);
            } catch (SAXException e) {
                Log.e(ResourceManager.TAG, "Error parsing XML file ", e);
            }
        }
    }


    /*
        The first step in parsing an EPUB file is to read the container.xml file for the location of the .opf file.
        From the EPUB specification, the container file is always called "container.xml" and must be in the folder "META-INF".


    */

    private static final String XML_NAMESPACE_CONTAINER = "urn:oasis:names:tc:opendocument:xmlns:container";

    private String mOpfFileName;


    /*
     For location of the .opf file, we look through the <rootfile> elements until we find one with a media-type of application/oebps-package+xml
     The element with that media-type has attribute "full-path"  which holds all details to understand EPUB file.
     Lastly, getContentHandler() is called to package it all up into a ContentHandler that can be to passed to a XMLReader.

     We can obtain the name of the .opf file with the following code:
        parseXmlResource("META-INF/container.xml", constructContainerFileParser());

     */

    private ContentHandler constructContainerFileParser() {
        // describe the relationship of the elements
        RootElement root = new RootElement(XML_NAMESPACE_CONTAINER,"container");
        Element rootfilesElement = root.getChild(XML_NAMESPACE_CONTAINER,"rootfiles");
        Element rootfileElement = rootfilesElement.getChild(XML_NAMESPACE_CONTAINER, "rootfile");

        // how to parse a rootFileElement
        rootfileElement.setStartElementListener(new StartElementListener(){
            public void start(Attributes attributes) {
                String mediaType = attributes.getValue("media-type");
                if ((mediaType != null) && mediaType.equals("application/oebps-package+xml")) {
                    mOpfFileName = attributes.getValue("full-path");
                }
            }
        });
        return root.getContentHandler();
    }



    //****************************************************************
    /*

    */

    private static final String XML_NAMESPACE_PACKAGE = "http://www.idpf.org/2007/opf";

    private HashMap<String, String> mManifestIndex;
    private HashMap<String, String> mManifestMediaTypes;
    private ArrayList<String> mSpine;
    private String mTocName;

    /*private ContentHandler constructOpfFileParser() {
        // describe the relationship of the elements
        RootElement root = new RootElement(XML_NAMESPACE_PACKAGE, "package");
        Element manifest = root.getChild(XML_NAMESPACE_PACKAGE, "manifest");
        Element manifestItem = manifest.getChild(XML_NAMESPACE_PACKAGE, "item");
        Element spine = root.getChild(XML_NAMESPACE_PACKAGE, "spine");
        Element itemref = spine.getChild(XML_NAMESPACE_PACKAGE, "itemref");

        // build up list of files in book from manifest
        manifestItem.setStartElementListener(new StartElementListener(){
            public void start(Attributes attributes) {
                String href = attributes.getValue("href");
                // href may be a relative path, so need to resolve
                href = FilenameUtils.concat(FilenameUtils.getPath(mOpfFileName), href);
                mManifestIndex.put(attributes.getValue("id"), href);
                mManifestMediaTypes.put(href, attributes.getValue("media-type"));
            }
        });

        // get name of Table of Contents file from the Spine
        spine.setStartElementListener(new StartElementListener(){
            public void start(Attributes attributes) {
                String toc = attributes.getValue("toc");
                mTocName = mManifestIndex.get(toc).getHref();
            }
        });

        // Build "spine", the files in the zip in reading order
        itemref.setStartElementListener(new StartElementListener(){
            public void start(Attributes attributes) {
                mSpine.add(attributes.getValue("idref"));
            }
        });
        return root.getContentHandler();
    }*/


}
