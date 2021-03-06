package edu.harvard.hul.ois.fits.tools.aiCharacterizationTool;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
public class AiCharacterizationTool extends ToolBase{
	public final static String xslt = Fits.FITS_HOME+"xml/aiCharacterizationTool/aiCharacterizationToolToFits.xslt";
	private final static String TOOL_NAME = "AI Characterization Tool";
	private boolean enabled = true;
	//private static Logger logger = Logger.getLogger(AiCharacterizationTool.class);
	private pt.keep.validator.ai.AiCharacterizationTool keepValidator;
	
	public AiCharacterizationTool() throws FitsToolException {
		info = new ToolInfo();
		info.setName(TOOL_NAME);
		keepValidator = new pt.keep.validator.ai.AiCharacterizationTool();
		info.setVersion(keepValidator.getVersion());
	}
	public ToolOutput extractInfo(File file) throws FitsToolException {
		long startTime = System.currentTimeMillis();
		try{
		    String execOut = keepValidator.run(file);
			SAXBuilder sb=new SAXBuilder();
			Document rawOut=sb.build(new InputSource(new ByteArrayInputStream(execOut.getBytes("utf-8"))));
			Document fitsXml = transform(xslt, rawOut);
			output = new ToolOutput(this, fitsXml,rawOut);
			duration = System.currentTimeMillis()-startTime;
			runStatus = RunStatus.SUCCESSFUL;
			return output;
		}catch(Exception e){
			return null;
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;
	}

	public Boolean canIdentify() {
		return false;
	}
	
	public static void main(String args[]){
	  try{
	    
	    pt.keep.validator.ai.AiCharacterizationTool keepValidator = new pt.keep.validator.ai.AiCharacterizationTool();
	    System.out.println(keepValidator.run(new File("/home/sleroux/Development/fits-testing/corpora/largeCorpora/2.ai")));
	    System.out.println(keepValidator.getVersion());
	  }catch(Exception e){
	    e.printStackTrace();
	  }
	}
}
