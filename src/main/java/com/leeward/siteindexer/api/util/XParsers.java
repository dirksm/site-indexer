package com.leeward.siteindexer.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import org.xml.sax.SAXException;

public class XParsers {

	/**
     * This method parses the .docx files.
     *
     * @param docx
     * @throws FileNotFoundException
     * @throws IOException
     * @throws XmlException
     * @throws InvalidFormatException
     * @throws OpenXML4JException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public String docFileContentParser(OPCPackage docx) throws FileNotFoundException,
            IOException,
            XmlException,
            InvalidFormatException,
            OpenXML4JException,
            ParserConfigurationException,
            SAXException {
        XWPFWordExtractor xw = new XWPFWordExtractor(docx);
        return xw.getText();
    }

    /**
     * This method parses the pptx files
     *
     * @param pptx
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidFormatException
     * @throws XmlException
     * @throws OpenXML4JException
     */
    public String ppFileContentParser(OPCPackage pptx) throws FileNotFoundException,
            IOException,
            InvalidFormatException,
            XmlException,
            OpenXML4JException {
        XSLFPowerPointExtractor xw = new XSLFPowerPointExtractor(pptx);
        return xw.getText();
    }

    /**
     * This method parsed xlsx files
     *
     * @param xlsx
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidFormatException
     * @throws XmlException
     * @throws OpenXML4JException
     */
    public String excelContentParser(OPCPackage xlsx) throws FileNotFoundException,
            IOException,
            InvalidFormatException,
            XmlException,
            OpenXML4JException {
        XSSFExcelExtractor xe = new XSSFExcelExtractor(xlsx);
        return xe.getText();
    }    
}
