package main.music;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Dennis on 5-12-2016.
 */
public class WMPFolderParser {
    SAXParser parser;
    public WMPFolderParser() {
        try {
            parser = SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            throw new Error(e);
        }
    }

    public List<Element> parser(File file){
        MyDefaultHandler handler = new MyDefaultHandler();
        try {
            parser.parse(file, handler);
        } catch (SAXException | IOException e) {
            throw new Error(e);
        }
        return handler.elements;
    }

    private class MyDefaultHandler extends DefaultHandler {

        int level = 0;
        Stack<String> stack = new Stack<>();
        List<Element> elements = new ArrayList<>(1024);

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if("Dir".equals(qName)){
                if(level == 1) {
                    stack.push(attributes.getValue("Folder") + "\\");
                }
                if(level > 1){
                    stack.push(attributes.getValue("Name") + "\\");

                    String joined = stack.stream().collect(Collectors.joining());
                    String mod = attributes.getValue("Mod");
                    Element e = new Element(joined, mod);
                    elements.add(e);

                    if(!attributes.getValue("Dirty").equals("0")){
                        System.err.println("Dirty found "+e);
                    }
                    if(!attributes.getValue("Exclude").equals("0")){
                        System.err.println("Exclude found "+e);
                    }
                    if(!attributes.getValue("New").equals("0")){
                        System.err.println("New found "+e);
                    }
                }
            }
            level++;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if(level > 1)
                stack.pop();
            level--;
        }
    }

    static class Element{
        String joined, mod;

        public Element(String joined, String mod) {
            this.joined = joined;
            this.mod = mod;
        }

        @Override
        public String toString() {
            return "Element{" +
                    "joined='" + joined + '\'' +
                    ", mod='" + mod + '\'' +
                    '}';
        }
    }
}
