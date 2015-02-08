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

public class SemaforSent {
	
	String sentStr;
	String[] wordArray;
	
	ArrayList<Frame> frameList;
	
	public SemaforSent(String inputStr){
		sentStr = getText(inputStr);
		//System.out.println(sentStr);
		wordArray = sentStr.trim().split("[\\s]+");
		ArrayList<String> frameStrList = getFrameStrList(inputStr);
		int[] strIndexArray = getStrIndexArray(sentStr);
		/*for(int i=0;i<strIndexArray.length;i++){
			System.out.print(strIndexArray[i]);
			System.out.print(" ");
		}
		System.out.println();*/
		frameList = new ArrayList<Frame>();
		for(int i=0;i<frameStrList.size();i++){
			String nowFrameStr = frameStrList.get(i);
			Frame nowFrame = new Frame(nowFrameStr);
			nowFrame.setWordIndex(strIndexArray);
			frameList.add(nowFrame);
		}
	}
	
	public ArrayList<Frame> getFrameList(){
		return frameList;
	}
	
	private int[] getStrIndexArray(String inputStr){
		//-1: space
		int[] result = new int[inputStr.length()];
		int index = -1;
		boolean insideWord = false;
		for(int i=0;i<inputStr.length();i++){
			if(inputStr.substring(i, i+1).matches("[\\s]")){
				result[i] = -1;
				insideWord = false;
			}else{
				if(insideWord==false){
					index++;
				}
				result[i] = index;
				insideWord = true;
			}
		}
		return result;
	}
	
	private ArrayList<String> getFrameStrList(String inputStr){
		ArrayList<String> result = new ArrayList<String>();
		String head = "<annotationSet ID=";
		String tail = "</annotationSet>";
		while(inputStr.indexOf(head)>=0){
			int begin = inputStr.indexOf(head);
			int end = inputStr.indexOf(tail)+tail.length();
			result.add(inputStr.substring(begin, end));
			if(end<inputStr.length()){
				inputStr = inputStr.substring(end);
			}else{
				break;
			}
		}
		return result;
	}
	
	private String getText(String inputStr){
		String head = "<text>";
		String tail = "</text>";
		int begin = inputStr.indexOf(head)+head.length();
		int end = inputStr.indexOf(tail);
		return inputStr.substring(begin, end);
	}
	
	public String getSentText(){
		return sentStr;
	}
	
	public String[] getWordArray(){
		return wordArray;
	}
	

}

class Frame {
	
	ArrayList<Layer> layerList;
	String frameName;
	
	public Frame(String nowFrameStr){
		layerList = new ArrayList<Layer>();
		frameName = getFrameName(nowFrameStr);
		ArrayList<String> layerStrList = getLayerStrList(nowFrameStr);
		for(int i=0;i<layerStrList.size();i++){
			String nowLayerStr = layerStrList.get(i);
			Layer nowLayer = new Layer(nowLayerStr);
			layerList.add(nowLayer);
		}
	}
	
	private ArrayList<String> getLayerStrList(String nowFrameStr){
		ArrayList<String> result = new ArrayList<String>();
		String head = "<layer ID=\"";
		String tail = "</layer>";
		while(nowFrameStr.indexOf(head)>=0){
			int begin = nowFrameStr.indexOf(head);
			int end = nowFrameStr.indexOf(tail)+tail.length();
			result.add(nowFrameStr.substring(begin, end));
			if(end<nowFrameStr.length()){
				nowFrameStr = nowFrameStr.substring(end);
			}else{
				break;
			}
		}
		return result;
	}
	
	private String getFrameName(String nowFrameStr){
		String head = "frameName=\"";
		String tail = "\">";
		int beginIndex = nowFrameStr.indexOf(head)+head.length();
		nowFrameStr = nowFrameStr.substring(beginIndex);
		int endIndex = nowFrameStr.indexOf(tail);
		return nowFrameStr.substring(0, endIndex).trim();
	}
	
	public void setName(String nowStr){
		frameName = nowStr;
	}
	
	public String getName(){
		return frameName;
	}
	
	public void addLayer(Layer nowLayer){
		layerList.add(nowLayer);
	}
	
	public ArrayList<Layer> getLayerList(){
		return layerList;
	}
	
	public void setWordIndex(int[] strIndexArray){
		for(int i=0;i<layerList.size();i++){
			ArrayList<Label> labelList = layerList.get(i).getLabelList();
			for(int j=0;j<labelList.size();j++){
				Label nowLabel = labelList.get(j);
				int beginIndex = nowLabel.getBegin();
				int endIndex = nowLabel.getEnd();
				if(strIndexArray[beginIndex]!=-1){
					nowLabel.setBeginWordIndex(strIndexArray[beginIndex]);
				}else{
					System.out.println("setWordIndex error: beginWordIndex = "+Integer.toString(beginIndex));
					System.exit(1);
				}
				if(strIndexArray[endIndex]!=-1){
					nowLabel.setEndWordIndex(strIndexArray[endIndex]);
				}else{
					System.out.println("setWordIndex error: endWordIndex = "+Integer.toString(endIndex));
					System.exit(1);
				}
			}			
		}
	}
	
}

class Layer {
	
	ArrayList<Label> labelList;
	String name;
	
	public Layer(String nowLayerStr){
		labelList = new ArrayList<Label>();
		name = getLayerName(nowLayerStr);
		ArrayList<String> labelStrList = getLabelStrList(nowLayerStr);
		for(int i=0;i<labelStrList.size();i++){
			String nowLabelStr = labelStrList.get(i);
			Label nowLabel = new Label(nowLabelStr);
			labelList.add(nowLabel);
		}
	}
	
	private ArrayList<String> getLabelStrList(String nowLayerStr){
		ArrayList<String> result = new ArrayList<String>();
		String head = "<label ID=\"";
		String tail = "\"/>";
		while(nowLayerStr.indexOf(head)>=0){
			int begin = nowLayerStr.indexOf(head);
			int end = nowLayerStr.indexOf(tail)+tail.length();
			result.add(nowLayerStr.substring(begin, end));
			if(end<nowLayerStr.length()){
				nowLayerStr = nowLayerStr.substring(end);
			}else{
				break;
			}
		}
		return result;
	}
	
	private String getLayerName(String nowLayerStr){
		String head = "name=\"";
		String tail = "\">";
		int beginIndex = nowLayerStr.indexOf(head)+head.length();
		nowLayerStr = nowLayerStr.substring(beginIndex);
		int endIndex = nowLayerStr.indexOf(tail);
		return nowLayerStr.substring(0, endIndex).trim();
	}
	
	public void setName(String nowStr){
		name = nowStr;
	}
	
	public String getName(){
		return name;
	}
	
	public void addLayer(Label nowLabel){
		labelList.add(nowLabel);
	}
	
	public ArrayList<Label> getLabelList(){
		return labelList;
	}
	
	
}

class Label{
	
	private int begin;
	private int end;
	private String name;
	
	private int beginWordIndex;
	private int endWordIndex;
	
	public Label(String nowLabelStr){
		//<label ID="1000101" end="92" name="Target" start="88"/>
		begin = Integer.parseInt(getValue(nowLabelStr, "start"));
		end = Integer.parseInt(getValue(nowLabelStr, "end"));
		name = getValue(nowLabelStr, "name");
	}
	
	private String getValue(String nowLabelStr, String field){
		//<label ID="1000101" end="92" name="Target" start="88"/>
		String head = field+"=\"";
		String tail = "\"";
		if(nowLabelStr.indexOf(head)>=0){
			int begin = nowLabelStr.indexOf(head)+head.length();
			nowLabelStr = nowLabelStr.substring(begin);
			int end = nowLabelStr.indexOf(tail);
			return nowLabelStr.substring(0, end);
		}else{
			return null;
		}
	}
	
	public void setBeginWordIndex(int nowInt){
		beginWordIndex = nowInt;
	}
	
	public int getBeginWordIndex(){
		return beginWordIndex;
	}
	
	public void setEndWordIndex(int nowInt){
		endWordIndex = nowInt;
	}
	
	public int getEndWordIndex(){
		return endWordIndex;
	}
	
	public int getBegin(){
		return begin;
	}
	
	public int getEnd(){
		return end;
	}
	
	public String getName(){
		return name;
	}
	
}