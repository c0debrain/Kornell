package kornell.core.to;

import java.util.List;

public interface CourseClassesTO {
	public static final String TYPE = TOFactory.PREFIX + "courseClasses+json";
	
	List<CourseClassTO> getCourseClasses(); 
	void setCourseClasses(List<CourseClassTO> courseClasses);

}
