package cfe.web;

import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlFileInput;
import org.htmlunit.html.HtmlLabel;
import org.htmlunit.html.HtmlPage;

public class WebUtil {
    
    /**
     * Gets the dom element on the specified page that has the specified label.
     * 
     * @param page
     * @param label
     * @return
     */
    public static DomElement getDomElementByLabel(HtmlPage page, String label) {
        DomElement domElement = null;
        DomNodeList<DomElement> labelList = page.getElementsByTagName("label");

        for (int i = 0; i < labelList.getLength(); i++) {
            DomElement domLabelElement = labelList.get(i);
            if (domLabelElement instanceof HtmlLabel) {
                HtmlLabel labelElement = (HtmlLabel) domLabelElement;

                if (labelElement.getTextContent().equals(label)) {
                    String id = labelElement.getAttribute("for");
                    if (id != null) {
                        domElement = page.getElementById(id);
                    }
                }
            }
        }
        return domElement;
    }
    
    public static HtmlFileInput getFileInputByLabel(HtmlPage page, String label) {
        HtmlFileInput fileInput = null;
        
        DomElement element = WebUtil.getDomElementByLabel(page, label);
        if (element != null && element instanceof HtmlFileInput) {
            fileInput = (HtmlFileInput) element;
        }
        return fileInput;
    }
}
