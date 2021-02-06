package ren.io;
import java.io.PrintStream;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * Created on 24/07/2005
 *
 * @author Rene Wooller
 */

public class DPrint {

    public DPrint() {
        super();
        // TODO Auto-generated constructor stub
    }

    static void print(Document d) {
        NodeList nl = d.getChildNodes();
        PrintStream ps = System.out;
        for(int i=0; i< nl.getLength(); i++) {
            print(nl.item(i), ps);
        }
    }
   
    static void print(Node node, PrintStream out) {
        int type = node.getNodeType();
        switch (type) {
          case Node.ELEMENT_NODE:
            out.print("<" + node.getNodeName());
            NamedNodeMap attrs = node.getAttributes();
            int len = attrs.getLength();
            for (int i=0; i<len; i++) {
                Attr attr = (Attr)attrs.item(i);
                out.print(" " + attr.getNodeName() + "=\"" +
                          escapeXML(attr.getNodeValue()) + "\"");
            }
            out.print('>');
            NodeList children = node.getChildNodes();
            len = children.getLength();
            for (int i=0; i<len; i++)
              print(children.item(i), out);
            out.print("</" + node.getNodeName() + ">");
            break;
          case Node.ENTITY_REFERENCE_NODE:
            out.print("&" + node.getNodeName() + ";");
            break;
          case Node.CDATA_SECTION_NODE:
            out.print("<![CDATA[" + node.getNodeValue() + "]]>");
            break;
          case Node.TEXT_NODE:
            out.print(escapeXML(node.getNodeValue()));
            break;
          case Node.PROCESSING_INSTRUCTION_NODE:
            out.print("<?" + node.getNodeName());
            String data = node.getNodeValue();
            if (data!=null && data.length()>0)
               out.print(" " + data);
            out.println("?>");
            break;
        }
      }

      static String escapeXML(String s) {
        StringBuffer str = new StringBuffer();
        int len = (s != null) ? s.length() : 0;
        for (int i=0; i<len; i++) {
           char ch = s.charAt(i);
           switch (ch) {
           case '<': str.append("&lt;"); break;
           case '>': str.append("&gt;"); break;
           case '&': str.append("&amp;"); break;
           case '"': str.append("&quot;"); break;
           case '\'': str.append("&apos;"); break;
           default: str.append(ch);
         }
        }
        return str.toString();
      }
    
    
    
}
