/**
 * This file is part of SemaforViewer.

    SemaforViewer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SemaforViewer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SemaforViewer.  If not, see <http://www.gnu.org/licenses/>.
 */

package drawer;

import java.io.*;
import java.util.*;

public class HTMLDrawer {

	static final String encoding = "UTF8";
	static final String jsPath = "js/open.js";
	static final String[] colorArray = {"#BCF46E", "#FFD673", "#6F81D6", "#EB6AA3"};
	static int colorIndex = 0;
	
	public static void main(String[] args) throws IOException {
		
		String inputFileStr = "";
		String outputFolderStr = "";
		
		if(args.length==2){
			inputFileStr = args[0];
			System.out.println("Input File: "+inputFileStr);
			outputFolderStr = args[1];
			System.out.println("Output Folder: "+outputFolderStr);
		}else{
			System.out.println("usage: java -jar SemaforViewer.jar <inputFile> <outputFolder>");
			System.exit(1);
		}
		
		File inputFile = new File(inputFileStr);
		File orOutputFolder = new File(outputFolderStr);
		if(!orOutputFolder.exists()){
			orOutputFolder.mkdir();
		}
		File outputFolder = new File(orOutputFolder, "htm");
		if(!outputFolder.exists()){
			outputFolder.mkdir();
		}
		File jsFolder = new File(outputFolder, "js");
		if(!jsFolder.exists()){
			jsFolder.mkdir();
		}
		File jsFile = new File(jsFolder, "open.js");
		printJS(jsFile);
		
		System.out.println("Get XML String...");
		ArrayList<String> sentStrList = getSentStrList(inputFile);
		System.out.println("#Sentence: "+Integer.toString(sentStrList.size()));
		
		System.out.println("Print HTML pages...");
		for(int i=0;i<sentStrList.size();i++){
			String nowSentStr = sentStrList.get(i);
			SemaforSent nowSent = new SemaforSent(nowSentStr);
			File nowOutputFile = new File(outputFolder, Integer.toString(i)+".htm");
			drawHTML(nowOutputFile, nowSent);
		}
		
		System.out.println("Print the index page...");
		File indexFile = new File(orOutputFolder, "index.htm");
		printIndex(indexFile, sentStrList);

	}
	
	public static void printIndex(File indexFile, ArrayList<String> sentStrList) throws IOException {
		BufferedWriter fOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(indexFile, false), encoding));
		fOut.write("<html><head><title>Semafor HTML Viewer Index Page</title></head>");
		fOut.write("<BODY TOPMARGIN=20 LEFTMARGIN=20 MARGINHEIGHT=20 MARGINWIDTH=20>");
		fOut.write("<ol>");
		for(int i=0;i<sentStrList.size();i++){
			String nowSentStr = sentStrList.get(i);
			SemaforSent nowSent = new SemaforSent(nowSentStr);
			fOut.write("<li>");
			fOut.write("<a href=\"");//<a href="http://www.goldcoastwebdesigns.com/change-hyperlink-color.shtml">
			fOut.write("htm/"+Integer.toString(i)+".htm");
			fOut.write("\">");
			fOut.write(nowSent.getSentText());
			fOut.write("</li>");
			fOut.write("<br>");
		}
		fOut.write("</ol>");
		fOut.write("</body></html>");
		fOut.close();
	}
	
	public static void printJS(File jsFile) throws IOException {
		BufferedWriter fOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsFile, false), encoding));
		fOut.write("function showhidediv(id){");
		fOut.newLine();
		fOut.write("try{");
		fOut.newLine();
		fOut.write("var sbtitle=document.getElementById(id);");
		fOut.newLine();
		fOut.write("if(sbtitle){");
		fOut.newLine();
		fOut.write("if(sbtitle.style.display=='block'){");
		fOut.newLine();
		fOut.write("sbtitle.style.display='none';");
		fOut.newLine();
		fOut.write("}else{");
		fOut.newLine();
		fOut.write("sbtitle.style.display='block';");
		fOut.newLine();
		fOut.write("}");
		fOut.newLine();
		fOut.write("}");
		fOut.newLine();
		fOut.write("}catch(e){}");
		fOut.newLine();
		fOut.write("}");
		fOut.newLine();
		fOut.close();	
	}
	
	public static void drawHTML(File nowOutputFile, SemaforSent nowSent) throws IOException {
		//annotationSet-layer-label
		BufferedWriter fOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nowOutputFile, false), encoding));
		fOut.write("<html><head><title>Semafor HTML Viewer</title></head>");
		fOut.write("<BODY TOPMARGIN=20 LEFTMARGIN=20 MARGINHEIGHT=20 MARGINWIDTH=20>");
		//<BODY TOPMARGIN=0 LEFTMARGIN=0 MARGINHEIGHT=0 MARGINWIDTH=0>
		
		fOut.write("<script language=\"JavaScript\" src=\""+jsPath+"\" type=\"text/javascript\"></script>");
		
		String[] wordArray = nowSent.getWordArray();
		
		fOut.write("<ul>");
		//fOut = drawText(fOut, nowArray);
		fOut.write("<li><h3>Sentence</h3></li>");
		fOut.write(nowSent.getSentText());
		fOut.write("<li><h3>Frames</h3><h4>Click the frame names to see details</h4></li>");
		fOut.write("<ul>");
		ArrayList<Frame> frameList = nowSent.getFrameList();
		for(int i=0;i<frameList.size();i++){
			Frame nowFrame = frameList.get(i);
			fOut = printList(fOut, nowOutputFile, i, nowFrame.getName());
			fOut = drawFrame(fOut, nowFrame, wordArray, i);
			//fOut.write("<br>");
		}
		fOut.write("</ul>");
		
		fOut.write("</ul>");
		
		fOut.write("</body></html>");
		fOut.close();
	}
	
	public static BufferedWriter printList(BufferedWriter fOut, File nowOutputFile, int index, String frameName) throws IOException {
		fOut.write("<div id=\"sbtitle\"><div onclick='showhidediv(\""+Integer.toString(index)+"\");'><li>");
		fOut.write(frameName);
		fOut.write("</li></div>");
		return fOut;
	}
	
	public static BufferedWriter drawFrame(BufferedWriter fOut, Frame nowFrame, String[] wordArray, int index) throws IOException {
		fOut.write("<div id=\""+Integer.toString(index)+"\" style=\"display:none;\"><br>");
		fOut.write("<table border=1 cellpadding=5>");
		fOut = drawText(fOut, wordArray);
		//String nowFrameName = nowFrame.getName();
		ArrayList<Layer> layerList = nowFrame.getLayerList();
		for(int i=0;i<layerList.size();i++){
			fOut.write("<tr>");
			/*if(i==0){
				fOut.write("<th rowspan="+Integer.toString(layerList.size())+" align=center>"+nowFrameName+"</th>");
			}*/
			Layer nowLayer = layerList.get(i);
			String nowLayName = nowLayer.getName();
			fOut.write("<td align=center>"+nowLayName+"</th>");
			ArrayList<Label> labelList = nowLayer.getLabelList();
			String[] result = new String[wordArray.length];
			for(int j=0;j<result.length;j++){
				result[j] = "-";
			}
			for(int j=0;j<labelList.size();j++){
				Label nowLabel = labelList.get(j);
				String nowLabelName = nowLabel.getName();
				int begin = nowLabel.getBeginWordIndex();
				int end = nowLabel.getEndWordIndex();
				for(int k=begin;k<=end;k++){
					result[k] = nowLabelName;
				}
			}
			for(int j=0;j<result.length;j++){
				String nowLabel = result[j];
				int length = 0;
				for(int k=j;k<result.length;k++){
					if(result[k].equals(nowLabel)){
						length++;
					}else{
						break;
					}
				}
				if(nowLabel.equals("-")){
					nowLabel = "&nbsp;";
					fOut.write("<th align=center colspan="+Integer.toString(length)+">"+nowLabel+"</td>");
				}else{
					fOut.write("<th align=center bgcolor=\""+colorArray[colorIndex%colorArray.length]+"\" colspan="+Integer.toString(length)+">"+nowLabel+"</td>");
					colorIndex++;
				}
				j = j+length-1;
			}
			fOut.write("</tr>");
		}
		fOut.write("</table>");
		fOut.write("<br></div>");
		return fOut;
	}
	
	public static BufferedWriter drawText(BufferedWriter fOut, String[] nowArray) throws IOException {
		//fOut.write("<table border=1>");
		fOut.write("<tr>");
		/*fOut.write("<td align=center>");
		fOut.write("Frame");
		fOut.write("</td>");*/
		fOut.write("<td align=center>");
		fOut.write("Layer");
		fOut.write("</td>");
		for(int i=0;i<nowArray.length;i++){
			fOut.write("<th align=center>");
			fOut.write(nowArray[i]);
			fOut.write("</th>");
		}
		fOut.write("</tr>");
		//fOut.write("</table>");
		return fOut;
	}
	
	public static ArrayList<String> getSentStrList(File inputFile) throws IOException {
		ArrayList<String> result = new ArrayList<String>();
		String head = "<sentence ID=";
		String tail = "</sentence>";
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), encoding));
		String lineBuffer = "";
		String sentStr="";
		boolean inSnet = false;
		while(true){
			lineBuffer = in.readLine();
			if(lineBuffer!=null){
				if(lineBuffer.length()>0){
					if(inSnet){
						sentStr+=lineBuffer;
						if(lineBuffer.indexOf(tail)>=0){
							result.add(sentStr);
							inSnet = false;
							sentStr="";
						}
					}else{
						if(lineBuffer.indexOf(head)>=0){
							inSnet = true;
							sentStr+=lineBuffer;
						}
					}
				}
			}else{
				break;
			}
		}
		return result;
	}

}
