package edu.harvard.hul.ois.fits.tools.fido;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.Document;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.tools.utils.CommandLine;

public class Fido extends ToolBase{
	public final static String xslt = Fits.FITS_HOME+"xml/fido/fidoToFits.xslt";
	

	
	private List<String> command = new ArrayList<String>(Arrays.asList("python", Fits.FITS_TOOLS+"fido/fido.py","-q"));
	private final static String TOOL_NAME = "fido";
	private boolean enabled = true;
	
	public Fido() throws FitsToolException {
		info = new ToolInfo();
		info.setName(TOOL_NAME);
		String versionOutput = null;
		List<String> infoCommand = new ArrayList<String>(Arrays.asList("python", Fits.FITS_TOOLS+"fido/fido.py"));
		infoCommand.add("-v");
		versionOutput = CommandLine.exec(infoCommand,null);	
		info.setVersion(versionOutput.trim().split(" ")[1]);
	}
	@Override
	public ToolOutput extractInfo(File file) throws FitsToolException {
		long startTime = System.currentTimeMillis();
		List<String> execCommand = new ArrayList<String>();
		execCommand.addAll(command);
		execCommand.add(file.getPath());
		String execOut = CommandLine.exec(execCommand,null);
		
		
		Document rawOut = createXml(execOut);
	    
		
		Document fitsXml = transform(xslt, rawOut);

	    
		output = new ToolOutput(this, fitsXml,rawOut);

		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
		
		return output;

	}
	
	
	private Document createXml(String execOut) throws FitsToolException {  
		
		
		String[] lines = execOut.split("\n");
		
    	StringWriter out = new StringWriter();
    	
    	out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("\n");
        out.write("<fido>");
        out.write("\n");
        out.write("<identifications>");
        out.write("\n");
        boolean ok = false;
        for(String s : lines){
          
          String[] tokens = s.split(",");
          if(tokens[0].equalsIgnoreCase("OK")){
            out.write("<identification>");
            out.write("\n");
            ok=true;
            out.write("\n");
            if(tokens[1]!=null && !tokens[1].trim().equals("")){
                out.write("<time>"+StringEscapeUtils.escapeXml(tokens[1])+" ms</time>");
                out.write("\n");
            }
            if(tokens[2]!=null && !tokens[2].trim().equals("")){
                out.write("<puid>"+StringEscapeUtils.escapeXml(tokens[2])+"</puid>");
                out.write("\n");
            }
            if(tokens[3]!=null && !tokens[3].trim().equals("")){
                out.write("<formatName>"+StringEscapeUtils.escapeXml(tokens[3].substring(1, tokens[3].length()-1))+"</formatName>");
                out.write("\n");
            }
            if(tokens[4]!=null && !tokens[4].trim().equals("")){
                out.write("<signatureName>"+StringEscapeUtils.escapeXml(tokens[4].substring(1, tokens[4].length()-1))+"</signatureName>");
                out.write("\n");
            }
            if(tokens[5]!=null && !tokens[5].trim().equals("")){
                out.write("<fileSize>"+StringEscapeUtils.escapeXml(tokens[5])+"</fileSize>");
                out.write("\n");
            }
            if(tokens[6]!=null && !tokens[6].trim().equals("")){
                out.write("<fileName>"+StringEscapeUtils.escapeXml(tokens[6].substring(1, tokens[6].length()-1))+"</fileName>");
                out.write("\n");
            }
            if(tokens[7]!=null && !tokens[7].trim().equals("")){
                out.write("<mimeType>"+StringEscapeUtils.escapeXml(tokens[7].substring(1, tokens[7].length()-1))+"</mimeType>");
                out.write("\n");
            }
            if(tokens[8]!=null && !tokens[8].trim().equals("")){
                out.write("<matchType>"+StringEscapeUtils.escapeXml(tokens[8].substring(1, tokens[8].length()-1))+"</matchType>");
                out.write("\n");
            }
            out.write("</identification>");
            out.write("\n");
          }
        }
        out.write("</identifications>");
        out.write("\n");

        if(ok){
        	out.write("<correctlyIdentified>true</correctlyIdentified>");
        }else{
        	out.write("<correctlyIdentified>false</correctlyIdentified>");
        }
       
        out.write("</fido>");
        out.write("\n");
        out.flush();
        try {
			out.close();
		} catch (IOException e) {
			throw new FitsToolException("Error closing OdfValidator XML output stream",e);
		}
        
        
        Document doc = null;
		try {
			doc = saxBuilder.build(new StringReader(out.toString()));
		} catch (Exception e) {
			throw new FitsToolException("Error parsing Fido XML Output",e);
		} 
        return doc;
    }
	
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;		
	}

}
