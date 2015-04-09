package kornell.core.scorm12.rte.cmi;

import java.util.Map;

import kornell.core.scorm12.rte.DMElement;
import static kornell.core.scorm12.rte.DataType.*;
import kornell.core.scorm12.rte.SCOAccess;

/*
 * The maximum score that the student could have achieved.
 * The cmi.core.score.max must be a normalized value between 0 and 100. 
 */
public class Max extends DMElement{
	public static final Max dme = new Max();
	
	private Max(){
		super("max",false,EITHER(CMIDecimal,CMIBlank),SCOAccess.RW);
	}
	
	@Override
	public Map<String, String> initializeMap(Map<String, String> entries) {		
		return defaultTo(entries, "");
	}
	

}
