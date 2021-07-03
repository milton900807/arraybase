package com.arraybase.util;

import com.arraybase.db.util.SourceType;

public class GBRGX {









	public static final String SOLR = "^solr\\=([Hh][Tt][Tt][Pp]).*";
	public static final String CREATE_STATS_TABLE = "create stats";
//	public static final String HTTP_ADDRESS = "^(?!mailto:)(?:(?:http|https|ftp)://)(?:\\\\S+(?::\\\\S*)?@)?(?:(?:(?:[1-9]\\\\d?|1\\\\d\\\\d|2[01]\\\\d|22[0-3])(?:\\\\.(?:1?\\\\d{1,2}|2[0-4]\\\\d|25[0-5])){2}(?:\\\\.(?:[0-9]\\\\d?|1\\\\d\\\\d|2[0-4]\\\\d|25[0-4]))|(?:(?:[a-z\\\\u00a1-\\\\uffff0-9]+-?)*[a-z\\\\u00a1-\\\\uffff0-9]+)(?:\\\\.(?:[a-z\\\\u00a1-\\\\uffff0-9]+-?)*[a-z\\\\u00a1-\\\\uffff0-9]+)*(?:\\\\.(?:[a-z\\\\u00a1-\\\\uffff]{2,})))|localhost)(?::\\\\d{2,5})?(?:(/|\\\\?|#)[^\\\\s]*)?$";
	public static final String HTTP_ADDRESS = "\\s*((http|https|ftp):\\/\\/)?[\\w.-]+(?:\\.[\\w\\.-]+)*[\\w\\-\\._~:/?#@!\\$&'\\(\\)\\{\\}\\*\\+,;=.]+\\s*";

//	ftp://ftp.ensembl.org/pub/release-92/fasta/canis_familiaris/dna /canis
//	([A-Za-z0-9_]*['=']((http[s]?):\/)?\/([^\s]*))([\s]([A-Za-z0-9_]*['=']((http[s]?):\/)?\/([^\s]*)))* -- this will pull a series of http calls into fields
//public static final String IMPORT_JSON_FIELDS = "^create(\\s)+table(\\s)+" + GBRGX.FIELD + "(\\s)(for\\((\\s)*([0-9])+(\\s)*[,](\\s)*([0-9])+)(\\s)*\\)(\\s)+([A-Za-z0-9_]*['=']((http[s]?):\\/)?\\/([^\\s]*))([\\s]([A-Za-z0-9_]*['=']((http[s]?):\\/)?\\/([^\\s]*)))*";
	public static final String IMPORT_JSON_FIELDS = "^create(\\s)+table(\\s)+[A-Za-z0-9_]*(\\s)*(for\\((\\s)*([0-9])+(\\s)*[,](\\s)*([0-9])+)(\\s)*\\)(\\s)+([A-Za-z0-9_]*['=']" +
		"((http[s]?):\\/)?\\/([^\\s]*))([\\s]([A-Za-z0-9_]*['=']((http[s]?):\\/\\/)?([^\\s]*)))*";

//		"(\\s)+([A-Za-z0-9_]*['=']((http[s]?):\\/)?\\/([^\\s]*))([\\s]([A-Za-z0-9_]*['=']((http[s]?):\\/)?\\/([^\\s]*)))*";
    // this is used to replace the field value with a new string value.. eg. a find and replace when method chained with search.
	public static String FIELD_FUNCTION_REPLACE = "\\[" + GBRGX.FIELD + "\\]\\.replace\\s*\\(.*\\)\\s*";



	
	
	public static final String SET_CONFIG = "setconfig\\s";// this would be
															// interesting.. but
															// not implemented
															// y7et. I'm
															// thinkingt of
															// permitting the
															// creation of new
															// schema.. with new
															// sdervers etc.
	public static final String target = "([A-Za-z0-9_]*(\\{\\{i\\}\\})?)*";
	public static final String PATH = "(\\/)?" + target + "(\\/" + target + ")*(\\/)?";
	public static final String field_target = target + "(\\/" + target + ")*."
			+ target;

	public static String REPLACE_SOLR_CORE = "replace core " + PATH + " " + PATH;
	public static final String FIELD = "[A-Za-z0-9_]*";
	public static String FUNCTION = "^[A-Za-z0-9_]*\\(.*\\)";
	public static String FUNCTION_CHAIN = "\\.[A-Za-z0-9_]*\\(.*\\)";


	public static final String CMD = "^[a-zA-Z0-9_]*\\s*=\\s*[a-zA-Z0-9_]*\\.[a-zA-Z0-9_]*";
//	public static final String search = "^((?:\\/[a-zA-Z0-9_-]+(?:_[a-zA-Z0-9_-]+)*(?:\\-[a-zA-Z0-9_-]+)*)+)\\s*\\["
//			+ "(\\s*[a-zA-Z0-9_-]+\\s*,*\\s*)+\\]\\.search\\(.*\\)\\[(\\s*[0-9]*,*\\s*)+\\]$";
	public static final String gmap = "^var\\(\\s*.*\\s*\\)";
	public static final String field = "^((?:\\/[a-zA-Z0-9_-]+(?:_[a-zA-Z0-9_-]+)*(?:\\-[a-zA-Z0-9_-]+)*)+)\\s*\\["
			+ "(\\s*[a-zA-Z0-9_-]+\\s*,*\\s*)+\\]$";
	public static final String mktable = "^mktable\\s*((?:\\/[a-zA-Z0-9_-]+(?:_[a-zA-Z0-9_-]+)*(?:\\-[a-zA-Z0-9_-]+)*)+)\\s*\\["
			+ "(\\s*[a-zA-Z0-9_-]+\\s*,*\\s*)+\\]$";
	public static final String compositeCmd = ".*\\|.*";
	public static final String CREATE_TABLE_FROM = "^create\\s+table\\s+.[A-Za-z0-9_]*\\s+from\\s+(https?:\\/\\/)" +
			".+";// +
//			"([/\\w \\.-]*)*\\/?$/*;";
	public static final String CREATE_TABLE_FROM_AWS_DYNAMODB = "^create\\s+table\\s+.[A-Za-z0-9_]*\\s+from\\s+(aws)\\.[A-Za-z0-9_]*\\s+\\(.*\\)\\s*";
	public static final String CREATE_TABLE = "^create\\s+table\\s+" + PATH + "\\s+(--schema=(([A-Za-z0-9_]*):(int|string|float|string_ci|text|double))(,(([A-Za-z0-9_]*):(int|string|float|string_ci|text|double)))*)?";
	public static final String CREATE_TRIGGER b= "^create\\s+trigger\\s+.[A-Za-z0-9_]*";
	public static final String SET_FIELD_TYPE = "^[A-Za-z0-9_]*\\.set[fF]ield[tT]ype\\s*"
			+ "\\(\\s*[A-Za-z0-9_]*\\s*\\=(int|string|text|double|float|boolean|sint|sfloat|sdouble)\\s*\\)\\s*";
	public static final String PRINT_TO_FILE = "\\s*export\\s*(.)*";
	public static final String SET_SCHEMA = "^set\\s+schema\\s+[A-Za-z0-9_]*\\s*\\(\\s*(int|string|text|double|float|boolean)\\s+[A-Za-z0-9_]*"
			+ "(,\\s*(int|string|text|double|float|boolean)\\s+[A-Za-z0-9_]*)*\\s*\\)";
//	public static final String SET_LINK = "^set\\s+link\\s+[A-Za-z0-9_]*\\s*";
	public static final String SET_LINK2 = "^[A-Za-z0-9_]*\\.set[lL]ink\\s*\\(.*\\)\\s*";
	public static final String SETLAC = "^[A-Za-z0-9_]*\\.set([lL]ink|[Tt]ype|[Tt]able|[Dd]escription|[Uu]ser)\\s*\\(.*\\)\\s*";
	public static final String INSERTLAC = "^[A-Za-z0-9_]*\\.insert\\s*\\(.*\\)\\s*";
	public static final String SELECTLAC = "^[A-Za-z0-9_]*\\.select\\s*\\(.*\\).*";
	public static final String APPEND_FILE_TO_INDEX = "^[A-Za-z0-9_]*\\.append\\s*\\(.*\\).*";
	// need to implement this.
//	public static final String LOAD_ABQ_TO_INDEX = "^load\\s*\\[A-Za-z0-9_]*(.*\\)\\s*";
	public static final String EXIT = "^exit\\s*";
	public static final String DIFF_SCHEMA = "^[A-Za-z0-9_]*.diffSchema\\s*\\(.*\\)\\s*";
	public static final String DIFF = "^[A-Za-z0-9_]*.diff\\s*\\(.*\\)\\s*";
	public static final String OUTPUT = "^[A-Za-z0-9_]*.print\\s*\\(.*\\)(\\[.*\\])*";
	public static final String SET_FIELD_LINK = "^[A-Za-z0-9_]*.set[fF]ield[lL]ink\\\\s*\\\\(.*\\\\)\\\\s*";
	public static final String PROPERTIES = "(settings|properties|what)";
	public static final String SET_TYPE = "^set\\s+type\\s+[A-Za-z0-9_]*\\s*\\(("
			+ SourceType.DB.name()
			+ "|"
			+ SourceType.TABLE.name()
			+ "|"
			+ SourceType.RAW_FILE.name()
			+ "|"
			+ SourceType.NODE.name()
			+ ")\\)\\s*";
	public static final String EVOLVE = "evolve\\s+(.*)\\.evq.json\\s+" + PATH;
	public static final String SET_TYPE1 = "^set\\s+type\\s+[A-Za-z0-9_]*\\s*\\((db|table|columns|values)\\)\\s*";
	public static final String SET_TYPE2 = "^[A-Za-z0-9_]*.settype\\s*\\((db|table|columns|values)\\)\\s*";
	public static String setvariable = "[A-Za-z]+\\s*=\\s*[A-Za-z]+\\s*\\(\\s*[A-Za-z0-9\\-\\._[\\s*,\\s*]]+\\s*\\)";
	public static String set_property = "^set\\s+build\\s+[A-Za-z0-9_]*(\\.(abq|ABQ))\\s+[A-Za-z0-9_]*\\s*";
	public static String REBUILD = "^rebuild\\s+build\\s+[A-Za-z0-9_]*\\s*";
	public static String BUILD = "^build\\s+[A-Za-z0-9_]*\\s*";
	public static String RUN_UPDATE_OBJECT = target
			+ ".(update|runupdate|exec[Uu]pdate)";
	public static final String MAX_OR_MIN = "((\\s*(max|min)\\s*)|(^[A-Za-z0-9_]*.(min|max)\\s*\\(.*\\)\\s*))";
	public static final String GLOBAL_MAX = "^max\\s*\\(.*\\)\\s*";
	public static final String MEAN = "((^[A-Za-z0-9_]*.(mean|average)\\s*\\(.*\\)\\s*)|(^mean\\s*))";
	public static final String MEDIAN = "(median|(^[A-Za-z0-9_]*.(median)\\s*\\(.*\\)\\s*))";
	public static final String MODE = "^[A-Za-z0-9_]*.(mode)\\s*\\(.*\\)\\s*";
	public static final String INTERQUARTILE = "^[A-Za-z0-9_]*.(interquartile|iqrange|iqr)\\s*\\(.*\\)\\s*";
	public static final String ADD_FIELD = "^[A-Za-z0-9_]*.(add[Ff]ield)\\s*\\(.*\\)\\s*";
	public static final String CROP = "^[A-Za-z0-9_]*.(crop)\\s*\\(.*\\s*,.*\\s*,.*\\)\\s*";

	// ___________________________________________________________________________________________________________________________
	// ___________________________________________________________________________________________________________________________
	// _____________________________     Genomic Searches              _______________________________________________________________
	// ___________________________________________________________________________________________________________________________

	public static final String REVERSE_COMPLEMENT  = "[Rr][Cc]\\s+[AaCcGgTt]+";
	public static final String HIT_GENES = "(" + PATH
			+ ")(\\.genes)" + "\\s*\\(.*,\\s*" + PATH + "\\s*\\)\\s*"
			+ "(\\[" + FIELD + "\\])*\\s*";

	public static final String FLANK_SEQUENCE = "(" + PATH
			+ ")(\\.flank)" + "\\s*\\(.*\\)\\s*"
			+ "(\\[" + FIELD + "\\])*\\s*";

//	seq --mode=h  --sequence=/human --annotations=/annotations/GRCh38/a GCTATTAGGAGTCTTT
	public static final String SEQUENCE_SEARCH = "" +
			"seq" +
			"\\s+(--mode=[A-Za-z0-9_]*)" +
			"\\s+(--sequence=" + PATH + ")" +
			"\\s+(--annotation=" + PATH + ")" +
			"\\s+[ACTG]*";


	public static final String RECURSIVE_SEARCH = "(" + PATH
			+ ")(\\.genes)" + "\\s*\\(.*\\)\\s*"
			+ "(\\[" + FIELD + "\\])*\\s*";

	public static final String JSON_CONFIG = "\\s*(\\.)?" + PATH + "(\\.json)";
	public static final String LOAD_GTF = "\\s*loadgtf\\s+(\\.)?" + PATH + "(\\.gtf)" + "\\s+" + PATH;
	public static final String LOAD_GTF_FROM_WEB = "\\s*loadgtf\\s+(\\.)?" + HTTP_ADDRESS +  "\\s+" + PATH;
//	public static final String LOAD_GTF_URL = "\\s*loadgtf\\s+(\\.)?" + PATH + "(\\.gtf)" + "\\s+" + PATH;


	// ___________________________________________________________________________________________________________________________
	// ___________________________________________________________________________________________________________________________
	// ___________________________________________________________________________________________________________________________


	public static final String SEARCH_FORMAT_OUTPUT = "(" + PATH
			+ ")(\\.search)" + "\\s*\\(.*\\)\\s*"
			+ "((\\.set\\s*\\(.*\\)\\s*)|" + "(\\[" + FIELD + "\\])+\\s*"
			+ ")\\s*(\\{([0-9_]*)\\-([0-9_]*)\\})*";


	public static final String SEARCH_DISTINCT_OUTPUT = "(" + PATH
			+ ")*\\s*[A-Za-z0-9_]*(\\.search|\\.s|\\.sr)" + "\\s*\\(.*\\)\\s*"
			+ "((\\.set\\s*\\(.*\\)\\s*)|" + "(\\[" + FIELD + "\\])+\\s*"
			+ ")\\s*(\\{([0-9_]*)\\-([0-9_]*)\\})*";
	// ___________________________________________________________________________________________________________________________
	// ___________________________________________________________________________________________________________________________
	// ___________________________________________________________________________________________________________________________

	public static final String SEARCH_FLOW = SEARCH_FORMAT_OUTPUT + ">\\s*"
			+ target + "";

	public static final String PAIRED_TTEST = "\\s*([Pp][Aa][Ii][Rr][Ee][Dd](_)?[Tt][Tt][Ee][Ss][Tt]|pt(test)?|[Pp]aired[Tt]|pairedt)\\s*"
			+ "\\(\\s*"
			+ SEARCH_FORMAT_OUTPUT
			+ "\\s*,\\s*"
			+ SEARCH_FORMAT_OUTPUT + "\\s*\\)\\s*";

	public static final String HEAD =  PATH  + FIELD + "\\.head";
	public static final String COUNT = PATH + "\\.(count)\\s*(\\(.*\\))*\\s*(\\[.*\\])*";
	public static final String SET_PROP = "^[A-Za-z0-9_]*\\.(set[Pp]rop|set[Pp]roperty)\\s*\\(.*\\)\\s*";
	public static final String SHOW_PROPS = PATH + target + "\\.(props|showprops|showproperties)\\s*";
	public static final String RM_PROPS = "^[A-Za-z0-9_]*\\.([dr]p|delprops|delprop|rmprops|rmprop|rmproperties)\\s*\\(\\s*.*\\)\\s*";
	public static final String DESCRIPTION =  PATH + target + "\\.(desc|description|setdesc|setDescription|setdescription)\\s*\\(.*\\)\\s*";
	public static final String REF_NODES = "^[A-Za-z0-9_]*\\.(ref|references|referencenodes|children|list)\\s*";
	public static final String MV = "^mv\\s*(.*)\\s*(.*)";
	public static final String CP = "^cp\\s*(.*)\\s*(.*)";
	public static final String RM = "^(rm|del|delete|remove)\\s*(.*)\\s*(.*)";
	public static final String INSERT_CSV = "^[A-Za-z0-9_]*.(insert)\\s*\\(.*(.csv)\\)\\s*";
	public static final String SETVALUE = "^[A-Za-z0-9_]*.(set)\\s*\\(.*\\)\\s*";
	public static final String RM_FROM_TABLE = "^[A-Za-z0-9_]*.(rm|del|delete|DEL|DELETE|remove)"
			+ "\\s*\\(.*\\)\\s*" + ".*" + "(\\[.*\\])*.*";
	public static final String PUTVALUE = "^[A-Za-z0-9_]*.(cache)\\s*\\(.*\\)\\s*";
	public static final String CREATE_CACHE = "^create\\s+cache\\s+[A-Za-z0-9_]*";
	public static final String REFRESH =  PATH + target + "\\.(refresh|reload)";
	public static final String KILL = "^kill\\s+[A-Za-z0-9_]*";
	// create table t1 <- t2[ncbi_id][symbol] + t3[expressiondata][blah] ,
	// t2[ensemble_id]=t3[ensemble]
	public static final String JOIN = "^create\\s+table\\s+([A-Za-z0-9_])*\\s*<-\\s*";
	public static final String GMEDIAN = "\\s*mean\\s*(\\s*"
			+ SEARCH_FORMAT_OUTPUT + "\\s*)\\s*";
	public static final String GMAX = "\\s*max\\s*(\\s*" + SEARCH_FORMAT_OUTPUT
			+ "\\s*)\\s*";
	public static final String GMIN = "\\s*max\\s*(\\s*" + SEARCH_FORMAT_OUTPUT
			+ "\\s*)\\s*";
	public static final String CRON_RELOAD = "^[A-Za-z0-9_]*.set[Rr]eload\\s*\\(\\s*.*\\s*\\)\\s*";
	public static final String LOAD_CRON = "loadcron\\s+.*";
	public static final String SET_CRON = "cron\\s+.*";
	public static final String PRINT_VARS = "(vars\\s*|printvars\\s*|variables\\s*)";
	public static final String GLOBAL_MIN = "^min\\s*\\(.*\\)\\s*";
	public static final String GLOBAL_MEAN = "^(mean|average)\\s*\\(.*\\)\\s*";
	public static final String UPDATE = "^[A-Za-z0-9_]*.(update)(\\s*(\\s*.*\\s*)\\s*)*";
	public static final String SET_ABQ_VALUE = "^[A-Za-z0-9_]*.(setabqprop|setabq|abqprop)\\s*\\(.*,.*\\)\\s*";
	public static final String SEARCH_COUNT = "^[A-Za-z0-9_]*.(facet|searchcount)\\s*\\(.*,.*\\)\\s*";
	public static final String ADD_NODE_FACET = "^[A-Za-z0-9_]*.(set[fF]acet)\\s*\\((.*)=(.*)(,.*)*\\)\\s*";
	public static final String DELETE_NODE_FACET = "^[A-Za-z0-9_]*.(delete[Ff]acet|remove[Ff]acet)\\s*\\(.*\\)\\s*";
	public static final String LAC = "^[A-Za-z0-9_]*.[A-Za-z0-9_]*\\s*\\(\\s*.*\\s*\\)\\s*";
	public static final String SET_UPDATE_OBJECT = "^[A-Za-z0-9_]*.set[uU]pdate[oO]bject\\s*\\(\\s*.*\\s*(\\,\\s*.*\\s*)*\\)\\s*";
	public static final String CREATE_CORE = "create\\s+core\\s+[A-Za-z0-9_]*\\s*\\(\\s*(int|[Ss]tring|text|double|float|boolean)\\s+[A-Za-z0-9_]*"
			+ "(,\\s*(int|string|text|double|float|boolean)\\s+[A-Za-z0-9_]*)*\\s*\\)";
	public static final String DELETE_CORE = "[del|rm|delete|remove|kill]\\s+core\\s+[A-Za-z0-9_]*\\s*";
	public static final String INTO_COMMAND = ".*>.*";
	public static final String PK_ID = "^[A-Za-z0-9_]*.set[Pp][Kk][Ii][Dd]\\s*\\(\\s*[A-Za-z0-9_]*\\s*\\)\\s*";
	public static final String EXPORT_ABQ = "^[A-Za-z0-9_]*.export[Aa][Bb][Qq]\\s*(\\(.*\\))*\\s*";
	public static final String DIFF_FIELD = target
			+ ".diff\\s*\\(\\s*.*\\s*\\)\\s*";
	public static final String CD = "cd";
	public static final String LCD = "lcd";
	public static final String ROOTS = "roots";
	public static final String MKDIR = "mkdir";
	public static final String PWD = "pwd";
	public static final String DESCJ = "descj\\s*" + target;
	public static final String DROP = "^\\s*drop\\s+" + target;



	public static String SET_VARIABLE_TO_SEARCH = target + "\\s*=\\s*" + target
			+ "." + target + "\\s*\\(\\s*.*\\s*\\)" + "\\s*(\\[.*\\]\\s*)*";

	public static String MAC_EXP = "\\$";
	public static String ARCHIVE_CORE = "^archive\\s*" + target + "\\s*";
	public static String PRINT_REGRESSION = "^regression";
	public static String SCATTERPLOT = "\\s*[A-Za-z0-9_]*(.swingplot)"
			+ "\\s*\\(.*\\)\\s*" + ".*" + "(\\[.*\\])*\\s*";
	public static String PNGPLOT = "\\s*[A-Za-z0-9_]*(.plot)"
			+ "\\s*\\(.*\\)\\s*" + ".*" + "(\\[.*\\])*\\s*";
	public static String EXEC_VAR = "^exec\\s+" + target;
	public static final String STDV = "((^[A-Za-z0-9_]*.(stdv)"
			+ "\\s*\\(.*\\)\\s*)|(^stdv\\s*))";
	public static final String IMPORT_FILE_INTO_ARRAY_LIST_VAR = target
			+ "\\S*<-\\S*(read|readfile|readFile|load)\\S*\\(.*\\)\\S*";

	public static final String IMPORT_WEB_FILE = "import\\S*fasta\\S*\\(.*\\)\\S*";
	public static final String IMPORT_JAR_ARRAYBASE_INDEXER = "import\\s*[A-za-z0-9_]*(.jar)\\s*\\(.*\\)(.class)\\s*";


	// THIS IS USED FOR SEARCHING AND EDITING A FIELD
	public static final String SEARCH_WITH_PARAM_AND_FORMAT_OUTPUT = "\\s*[A-Za-z0-9_]*(.search|search)"
			+ "\\s*\\(.*\\)\\s*" + ".*" + "(\\[.*\\])*\\s*";
	// THIS IS USED FOR SEARCHING AND EDITING A FIELD
	public static final String SET_SEARCH_VALUE = SEARCH_FORMAT_OUTPUT
			+ "\\s*<-\\s*" + SEARCH_WITH_PARAM_AND_FORMAT_OUTPUT;
	public static final String PRINT_PREVIOUS_COMMAND= "prev";


	public static final String LLS = "^(lls|locallist|localls)";
	public static final String APPEND_TABLE = target + "\\s*\\+\\s*" + target;
	public static final String FIELD_EDIT = target + "\\." + target + "\\."
			+ target + "\\s*\\(\\s*.*\\s*\\)";
	public static final String MAP = "map(\\s*\\(.*\\))?\\s*>?\\s*"
			+ field_target;
	public static final String SET_FIELD_VALUES = target + "\\." + target
			+ ".set[Ff]ield[Vv]alue(s)?\\s*\\(\\.*\\)";

	public static final String COUNT_RANGE = "\\s*\\{\\s*[0-9]+\\s*\\-\\s*[0-9]+\\s*\\}";
	public static final String INPUT_AND_PROMPT = target + ".iprompt";
	public static final String COPY_ROWS = target + "\\.copyRows"
			+ "\\s*\\(.*\\)\\s*";
	public static final String SEARCH_AND_REPLACE = target + "\\." + target
			+ ".replace\\s*\\(\\.*\\)";

	// note the colon in included as the general char but the '[' is only paired
	// directly with the colon
	// this is for the scenario: something:[0 TO 1200] etc
	public static final String OPERATOR = "[\\+\\-\\\\\\?\\*]";
	public static final String SEARCH_STRING = "[A-Za-z0-9_\\(\\)\\+\\-\\*\\s\\]\\:\\.\\[\\]:]*(\\:\\[\")*";// cant seem to get the quotes to work.
	public static final String INPUT_FIELD = "(" + SEARCH_STRING + ")";
	public static final String INPUT_FIELDS = INPUT_FIELD + "(\\s*,\\s*"
			+ INPUT_FIELD + "\\s*)*";
	public static final String TABLE_FIELD = "(\\s*\\[\\s*" + target
			+ "\\s*\\]\\s*)+";
	public static final String NUMBER_RANGE = "([0-9_]*)\\-([0-9_]*)";
	public static final String TABLE_FIELDS = TABLE_FIELD + "(\\s*(,|"
			+ OPERATOR + ")\\s*" + TABLE_FIELD + "\\s*)*(\\{" + NUMBER_RANGE
			+ "\\})*";
	public static final String PARAMS = "(" + TABLE_FIELDS + "|" + INPUT_FIELDS
			+ ")\\s*(\\s*,\\s*(" + TABLE_FIELDS + "|" + INPUT_FIELDS + ")*|"
			+ TABLE_FIELDS + "|" + INPUT_FIELD + ")";
	public static final String METHOD = target + "\\(" + PARAMS + "\\)";
	public static final String TARGET_METHOD = target + "\\." + target + "\\("
			+ PARAMS + "\\)";
	public static final String METHOD_CHAIN = TARGET_METHOD + "(\\." + METHOD
			+ ")+";
	/*
	 * This loader feature is for settin specific loaders into fields. The goal
	 * here is to be able to load data into a table once particular row-based
	 * criterias has been satisfied.
	 * target.fied.trigger(ftp://something..../[id])
	 */
	public static final String FIELD_LOADER = target + "." + FIELD +"." + "trigger\\s\\(.*\\)\\s";//TARGET;
	// list recursively all the searchable nodes
	public static final String LS_SEARCHABLE_NODES = "(listsearch|lsearch|ListSearch|list_search)\\s*" + PATH;
	public static final String COMMAND_LINE_COMMAND = "^([A-Za-z0-9_]*)+\\s+([A-Za-z0-9_]*)*";
	//public static final String REFRESH = "^[/A-Za-z0-9_]*.(refresh|reload)";
	// clear the data from the table.
	public static final String CLEAR_TABLE = target + ".(clear|reset)";
	public static final  String LS = "ls\\s*("+PATH+")?";
	public static final String COORDINATES_deprecated = "(" + PATH
			+ ")(\\.coords)" + "\\s*\\(.*\\)\\s*"
			+ "(((\\.set\\s*\\(.*\\)\\s*)|" + "(\\[" + FIELD + "\\])+\\s*"
			+ ")\\s*(\\{([0-9_]*)\\-([0-9_]*)\\})*)?";
	public static final String LOCUS = PATH + "\\." + FIELD + "\\[" + NUMBER_RANGE + "\\]\\s*";
//	/human/chr1.sequence[8-99].coords(*ACGT*)
	public static final String COORDINATES = PATH + "\\." + FIELD + "(\\[" + NUMBER_RANGE + "\\])?\\s*" +
			"(\\.coords)" + "\\s*\\(.*\\)\\s*";
	private static final String FILE_PATH = "(C\\:)?"+PATH + "(\\.)?" + target;
	public static final String LOAD_TAB_DELIMITED_FILE = "load\\s+(--delimeter=(tab|space))\\s+" +
			"(--schema=([0-9]*\\:"+target+")([,]([0-9]*\\:"+target+"))*\\s*)?" +
			FILE_PATH + "\\s+" + PATH;


	public static final String ADD_AND_LOAD_FILE_FROM_ABX_FILE = "\\s*addx\\s+(\\.)?" + PATH + "(\\.abx)" + "\\s+" + PATH;
//	--load-genome --overlap=25 ftp://ftp.ensembl.org/pub/release-92/fasta/canis_familiaris/dna /canis
	public static final String LOAD_GENOME = "--load-genome\\s(--overlap=[0-9]*)\\s+--i=([0-9]*[\\.]*[0-9]*)(,([A-Za-z0-9_]*))*\\s+"  + HTTP_ADDRESS + "\\s+" + PATH;
	public static final String LOAD_GFF = "--load-gff\\s+" + HTTP_ADDRESS + "\\s+" + PATH;
	public static final String UPDATE_INDEX = "--update-index\\s+" + PATH + "(\\.json)" + "\\s+" + PATH;
	//http://s3-proxy:10000/vfvf-ngs-reference/genomes/canis-familiaris-88/resources/mrna.gff3 /test/gffcanfam"

	public static void main(String[] _ag){
		String s = "/test/abq/oligo4[sequence].search(CAG*)[83, 388]";// [SEQUENCE].search(hello)[0,
		int index = s.indexOf('[');
		int left_br = s.indexOf('[');
		int right_br = s.indexOf(']');
		String path = s.substring(0, index);
		String fields = s.substring(left_br + 1, right_br);
		String[] f = fields.split(",");
		int left_parm = s.indexOf("(");
		int right_parm = s.indexOf(")");

		String searchString = s.substring(left_parm + 1, right_parm);
		int lst_open = s.lastIndexOf("[");
		int lst_close = s.lastIndexOf(']');
		String range = s.substring(lst_open + 1, lst_close);
		int start = 0;
		int end = 1000;
		if (range.contains(",")) {
			String[] sp = range.split(",");
			String sstrt = sp[0].trim();
			start = Integer.parseInt(sstrt);
			String increment = sp[1].trim();
			end = Integer.parseInt(increment);
		} else {
			String strt = range.trim();
			start = Integer.parseInt(strt);
		}
		String[] t = path.split("/");
		String table = t[t.length - 1];
		System.out.println(" path:\t " + path);
		System.out.println(" table:\t " + table);
		for (int i = 0; i < f.length; i++) {
			System.out.println(" field:\t" + f[i]);
		}
		System.out.println(" start : " + start + " increment : " + end);
		System.out.println(" search string:\t" + searchString);

	}

}
