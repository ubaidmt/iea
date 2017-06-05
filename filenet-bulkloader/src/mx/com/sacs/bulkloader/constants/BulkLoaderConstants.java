package mx.com.sacs.bulkloader.constants;

public class BulkLoaderConstants {
	
	// Bulk Loader Type
	public final static int LOAD_CSV 			= 1;
	public final static int LOAD_FS 			= 2;
	
	// Load Result Status
	public final static int STATUS_OK 			= 0;
	public final static int STATUS_ERR 			= 1;
	
	// Property Data Types
	public final static String TYPE_STRING 		= "String";
	public final static String TYPE_INTEGER 	= "Integer";
	public final static String TYPE_FLOAT 		= "Float";
	public final static String TYPE_DATETIME 	= "DateTime";
	public final static String TYPE_BOOLEAN 	= "Boolean";
	
	// Segments
	public final static String SEGMENT_FIRST 	= "First";
	public final static String SEGMENT_LAST 	= "Last";
	public final static String SEGMENT_BETWEEN 	= "Between";
	public final static String SEGMENT_NONE		= "None";
	
	// Default Values Macros
	public final static String MACRO_FILENAME	= "{filename}";
	public final static String MACRO_FILEPATH	= "{filepath}";	
	
	// Defaut Position CSV
	public final static int PAGES_POSITION = 1;
}
