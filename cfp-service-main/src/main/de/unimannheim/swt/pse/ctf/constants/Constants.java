package de.unimannheim.swt.pse.ctf.constants;

import java.io.File;
import java.nio.file.Paths;

public class Constants {
	public final static String mapTemplateFolder = Paths.get("").toAbsolutePath().toString().split("cfp14")[0]+"cfp14"+File.separator+"."+File.separator+"cfp-service-main"+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"maptemplates"+File.separator;
	
}
