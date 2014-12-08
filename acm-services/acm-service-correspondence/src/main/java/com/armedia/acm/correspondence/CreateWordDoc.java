package com.armedia.acm.correspondence;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.*;
import java.io.*;

/**
 * Created by marjan.stefanoski on 03.12.2014.
 */
public class CreateWordDoc {

    public void testFile(String par1, String par2, String par3) {

        XWPFDocument document = new XWPFDocument();

        XWPFParagraph paragraphOne = document.createParagraph();
        paragraphOne.setAlignment(ParagraphAlignment.CENTER);
        paragraphOne.setBorderBottom(Borders.SINGLE);
        paragraphOne.setBorderTop(Borders.SINGLE);
        paragraphOne.setBorderRight(Borders.SINGLE);
        paragraphOne.setBorderLeft(Borders.SINGLE);
        paragraphOne.setBorderBetween(Borders.SINGLE);

        XWPFRun paragraphOneRunOne = paragraphOne.createRun();
        paragraphOneRunOne.setBold(true);
        paragraphOneRunOne.setItalic(true);
        paragraphOneRunOne.setText("Hello world! This is paragraph one!");
        paragraphOneRunOne.addBreak();

        XWPFRun paragraphOneRunTwo = paragraphOne.createRun();
        paragraphOneRunTwo.setText("Run two!");
        paragraphOneRunTwo.setTextPosition(100);
        XWPFRun paragraphOneRunThree = paragraphOne.createRun();
        paragraphOneRunThree.setStrike(true);
        paragraphOneRunThree.setFontSize(20);
        paragraphOneRunThree.setSubscript(VerticalAlign.SUBSCRIPT);
        paragraphOneRunThree.setText(" More text in paragraph one...");

        XWPFParagraph paragraphTwo = document.createParagraph();
        paragraphTwo.setAlignment(ParagraphAlignment.DISTRIBUTE);
        paragraphTwo.setIndentationRight(200);
        XWPFRun paragraphTwoRunOne = paragraphTwo.createRun();
        paragraphTwoRunOne.setText("And this is paragraph two.");

        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream("Test.docx");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            document.write(outStream);
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void test(String p1,String p2,String p3) {
        String filePath = "C:\\Users\\marjan.stefanoski\\Downloads\\ClearanceDenied.docx";
        POIFSFileSystem fs = null;
        try {
            fs = new POIFSFileSystem(new FileInputStream(filePath));
            HWPFDocument doc = new HWPFDocument(fs);
            doc = replaceText(doc, "$VAR", "MyValue1");
            saveWord(filePath, doc);
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private HWPFDocument replaceText(HWPFDocument doc, String findText, String replaceText){
        Range r1 = doc.getRange();

        for (int i = 0; i < r1.numSections(); ++i ) {
            Section s = r1.getSection(i);
            for (int x = 0; x < s.numParagraphs(); x++) {
                Paragraph p = s.getParagraph(x);
                for (int z = 0; z < p.numCharacterRuns(); z++) {
                    CharacterRun run = p.getCharacterRun(z);
                    String text = run.text();
                    if(text.contains(findText)) {
                        run.replaceText(findText, replaceText);
                    }
                }
            }
        }
        return doc;
    }

    private void saveWord(String filePath, HWPFDocument doc) throws FileNotFoundException, IOException{
        FileOutputStream out = null;
        try{
            out = new FileOutputStream(filePath);
            doc.write(out);
        }
        finally{
            out.close();
        }
    }
}

