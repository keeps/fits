package edu.harvard.hul.ois.fits.tools.emlCharacterizationTool;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.tools.utils.CommandLine;

public class EmlCharacterizationTool extends ToolBase{
	public final static String xslt = Fits.FITS_HOME+"xml/emlCharacterizationTool/emlCharacterizationToolToFits.xslt";
	private List<String> command = new ArrayList<String>(Arrays.asList("java","-jar",Fits.FITS_TOOLS+"emlCharacterizationTool/eml-characterization-tool.jar"));
	private final static String TOOL_NAME = "EML Characterization Tool";
	private boolean enabled = true;
	private boolean saveZipFile=true;
	
	
	public EmlCharacterizationTool() throws FitsToolException {
		info = new ToolInfo();
		info.setName(TOOL_NAME);
		String versionOutput = null;
		List<String> infoCommand = new ArrayList<String>();
		infoCommand.addAll(command);
		infoCommand.add("-v");
		versionOutput = CommandLine.exec(infoCommand,null);	
		info.setVersion(versionOutput.trim());
	}
	@Override
	
	public ToolOutput extractInfo(File file) throws FitsToolException {
		long startTime = System.currentTimeMillis();
		List<String> execCommand = new ArrayList<String>();
		execCommand.addAll(command);
		execCommand.add("-f");
		execCommand.add(file.getPath());
		String execOut = CommandLine.exec(execCommand,null);
		try{
			SAXBuilder sb=new SAXBuilder();
			Document rawOut=sb.build(new InputSource(new ByteArrayInputStream(execOut.getBytes("utf-8"))));
			Document fitsXml = transform(xslt, rawOut);
			output = new ToolOutput(this, fitsXml,rawOut);
			duration = System.currentTimeMillis()-startTime;
			runStatus = RunStatus.SUCCESSFUL;
			return output;
		}catch(Exception e){
			e.printStackTrace();
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
}
