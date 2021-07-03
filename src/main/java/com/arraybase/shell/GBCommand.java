package com.arraybase.shell;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;

import clinic.gene.shell.genomics.GeneHitSearch;
import clinic.gene.shell.genomics.LoadGTF;
import clinic.gene.shell.genomics.RecursiveCoreSearchGetFlankingSeqeunce;
import clinic.gene.shell.genomics.SequenceSearch;
import com.arraybase.*;
import com.arraybase.aws.dynamodb.CreateTableIndexFromAWSDynamoDB;
import com.arraybase.shell.cmds.genome.CoordinateSearch;
import com.arraybase.modules.UsageException;
import com.arraybase.shell.cmd.PRINTproperties;
import com.arraybase.shell.cmd.RebuildNodeCommand;
import com.arraybase.shell.cmds.*;
//import com.arraybase.shell.cmds.IntoCommand;
import com.arraybase.shell.cmds.genome.LocusShow;
import com.arraybase.shell.cmds.math.LinearRegressionPrint;
import com.arraybase.shell.cmds.math.PairedTTest;
import com.arraybase.shell.cmds.plot.ScatterPlot;
import com.arraybase.shell.genomics.ReverseComplement;
import com.arraybase.shell.genomics.loadGFF;
import com.arraybase.shell.genomics.loadGenome;
import com.arraybase.shell.loaders.LoadTextFile;
import com.arraybase.shell.tab_completion.LsHint;
import com.arraybase.shell.iterminal.c.ConsoleReader;
import com.arraybase.util.GBRGX;

public class GBCommand {

	private LinkedHashMap<String, GBPlugin> cmdset = new LinkedHashMap<String, GBPlugin>();
	private LinkedHashMap<String, CommandOption> copsions = new LinkedHashMap<String, CommandOption>();

	// order here is important...
	// the first that is added has priority
	public GBCommand ()
	{
		init();
	}
	public void updateCommands ( LinkedHashMap<String, GBPlugin> cmds ){
		cmdset = cmds;
	}
	
	public void reset ()
	{
		init();
	}
	
	/**
	 *  These are the default commands
	 */
	private void init(){
		// cmdset.put (GBRGX.MAX_OR_MIN, new ExpandMacros () );
		// cmdset.put(GBRGX.SUSPEND, new SuspendCommand());
//		cmdset.put(GBRGX.REPLACE, new ReplaceCommand());

		cmdset.put(GBRGX.LS, new ListPath());
		cmdset.put (GBRGX.ROOTS, new ShowRoots());
		cmdset.put(GBRGX.LS_SEARCHABLE_NODES, new LSSearchableNodes());
		cmdset.put(GBRGX.CMD, new ArrayCommand());
		cmdset.put(GBRGX.INPUT_AND_PROMPT, new Iprompt());
		cmdset.put(GBRGX.FUNCTION, new FunctionCommand());
//		cmdset.put(GBRGX.search, new SearchCommand());
		cmdset.put(GBRGX.field, new PrintFieldCommand());
		cmdset.put(GBRGX.PRINT_VARS, new PrintVariables());
		cmdset.put(GBRGX.mktable, new MakeTableCommand());
		cmdset.put(GBRGX.CLEAR_TABLE, new ResetTableCommand());
		cmdset.put(GBRGX.compositeCmd, new CompositeCommand());
		cmdset.put(GBRGX.setvariable, new SetVariableCommand());
		cmdset.put(GBRGX.set_property, new SetNodePropertyCommand());
		cmdset.put(GBRGX.REBUILD, new RebuildNodeCommand());
		cmdset.put(GBRGX.BUILD, new RebuildNodeCommand());
		cmdset.put (GBRGX.CREATE_TABLE_FROM, new CreateTableFromURL ());
		cmdset.put(GBRGX.CREATE_TABLE, new CreateTableNodeCommand());
		cmdset.put (GBRGX.CREATE_TABLE_FROM_AWS_DYNAMODB, new CreateTableIndexFromAWSDynamoDB());
		cmdset.put(GBRGX.SET_SCHEMA, new SetSchemaCommand());
		cmdset.put(GBRGX.SET_TYPE1, new SetTypeCommand());
		cmdset.put(GBRGX.SET_TYPE2, new SetTypeCommand());
//		cmdset.put(GBRGX.SET_LINK, new SetLinkCommand());
		cmdset.put(GBRGX.SET_LINK2, new SetLinkCommand2());
		cmdset.put(GBRGX.SETLAC, new search());
		cmdset.put(GBRGX.INSERTLAC, new InsertLacCommand());
		cmdset.put(GBRGX.SELECTLAC, new SelectLacCommand());
		cmdset.put(GBRGX.APPEND_FILE_TO_INDEX, new LOADLacCommand());
		cmdset.put(GBRGX.EXIT, new Exit());
		cmdset.put(GBRGX.DIFF_SCHEMA, new Diff());
		cmdset.put(GBRGX.OUTPUT, new PrintTable());
		cmdset.put(GBRGX.MAX_OR_MIN, new MINMAXValue());
		cmdset.put(GBRGX.MEAN, new MEANValue());
		cmdset.put(GBRGX.MEDIAN, new MedianValue());
		cmdset.put(GBRGX.MODE, new ModeValue());
		cmdset.put(GBRGX.INTERQUARTILE, new InterQuartile());
		cmdset.put(GBRGX.ADD_FIELD, new AddField());
		cmdset.put(GBRGX.SEARCH_FLOW, new searchflow());
		cmdset.put(GBRGX.SEARCH_FORMAT_OUTPUT, new search2());
//		cmdset.put(GBRGX.SEARCH_DISTINCT_OUTPUT, new search_distinct());
		cmdset.put(GBRGX.SET_FIELD_TYPE, new SetFieldType());
		cmdset.put(GBRGX.HEAD, new Head());
		cmdset.put(GBRGX.COUNT, new Count());
		cmdset.put(GBRGX.PROPERTIES, new PRINTproperties());
		cmdset.put(GBRGX.DESCRIPTION, new NodeSettings());
		cmdset.put(GBRGX.SET_PROP, new NodeSetProperty());
		cmdset.put(GBRGX.SHOW_PROPS, new ShowProps());
		cmdset.put(GBRGX.RM_PROPS, new RMProp());
		cmdset.put(GBRGX.MV, new mv());
		cmdset.put(GBRGX.CP, new cp());
		cmdset.put(GBRGX.CREATE_STATS_TABLE, new CreateStatsTable());
		cmdset.put(GBRGX.RM, new KillNode());
		cmdset.put(GBRGX.RM_FROM_TABLE, new RMWhere());
		cmdset.put(GBRGX.DROP, new DropTable());
		cmdset.put(GBRGX.INSERT_CSV, new loadCSV());
		cmdset.put(GBRGX.SETVALUE, new SetValue());
		cmdset.put(GBRGX.PUTVALUE, new CacheTest());
		cmdset.put(GBRGX.CREATE_CACHE, new CREATECacheTest());
		cmdset.put(GBRGX.REFRESH, new RefreshNode());
		cmdset.put(GBRGX.UPDATE, new InstantFunkUpdateNode());
		cmdset.put(GBRGX.SET_ABQ_VALUE, new SetABQValue());
		cmdset.put(GBRGX.KILL, new KillNode());
		cmdset.put(GBRGX.JOIN, new JoinCommand());
		cmdset.put(GBRGX.LOAD_CRON, new LoadCron());
		cmdset.put(GBRGX.SET_CRON, new SetCron () );
		cmdset.put(GBRGX.SEARCH_COUNT, new SearchCount());
		cmdset.put(GBRGX.GLOBAL_MAX, new Max());
		cmdset.put(GBRGX.GLOBAL_MIN, new Min());
		cmdset.put(GBRGX.GLOBAL_MEAN, new Mean());
		cmdset.put(GBRGX.ADD_NODE_FACET, new SetFacet());
		cmdset.put(GBRGX.DELETE_NODE_FACET, new Deletefacet());
		cmdset.put(GBRGX.SET_UPDATE_OBJECT, new SetUpdateObject());
		cmdset.put(GBRGX.EXPORT_ABQ, new ExportABQObject());
		cmdset.put(GBRGX.DELETE_CORE, new DeleteCore());
		cmdset.put(GBRGX.PK_ID, new PKID());
		cmdset.put(GBRGX.DIFF_FIELD, new DiffField());
		cmdset.put(GBRGX.FIELD_EDIT, new FieldEdit());
		cmdset.put(GBRGX.SOLR, new SetSolr());
		cmdset.put ( GBRGX.IMPORT_WEB_FILE, new ImportWebFile () );
		cmdset.put ( GBRGX.IMPORT_JAR_ARRAYBASE_INDEXER, new ImportJarArrayBaseIndexer () );
		cmdset.put ( GBRGX.IMPORT_JSON_FIELDS, new JSONFieldsImporter() );
		// cmdset.put(GBRGX.RUN_UPDATE_OBJECT, new RunUpdateObject ()); --- see
		// the instant funk above...
		// cmdset.put(GBRGX.UPDATE, new SetUpdateObject());
		// cmdset.put(GBRGX., new ExportABQObject());
		cmdset.put(GBRGX.CREATE_CORE, new SetCreateCore());
//		cmdset.put(GBRGX.DELETE_CORE, new DeleteCore());
		cmdset.put(GBRGX.PK_ID, new PKID());
		cmdset.put(GBRGX.SET_VARIABLE_TO_SEARCH, new AsignVarSearch());
		// cmdset.put(GBRGX.RUN_UPDATE_OBJECT, new RunUpdateObject ()); --- see
		// the instant funk above...
		// cmdset.put(GBRGX.UPDATE, new SetUpdateObject());
		cmdset.put(GBRGX.STDV, new StandardDeviation());
		cmdset.put(GBRGX.PRINT_TO_FILE, new PrintToFile());
		cmdset.put(GBRGX.IMPORT_FILE_INTO_ARRAY_LIST_VAR,
				new FileToArrayListVar());
		cmdset.put(GBRGX.MAP, new MapIt());
		cmdset.put(GBRGX.ARCHIVE_CORE, new ArchiveCore());
		// cmdset.put(GBRGX.ADD_NODES, new FileToArrayListVar());
		// SimpleLinearRegression
		cmdset.put(GBRGX.PRINT_REGRESSION, new LinearRegressionPrint());
		// {{ SCATTER PLOT STUFF }}
		cmdset.put(GBRGX.SCATTERPLOT, new ScatterPlot());
		cmdset.put(GBRGX.EXEC_VAR, new ExecuteVariable());
		cmdset.put(GBRGX.COPY_ROWS, new CopyRows());
		cmdset.put(GBRGX.METHOD_CHAIN, new MethodChain());
		cmdset.put(GBRGX.PAIRED_TTEST, new PairedTTest());
		cmdset.put(GBRGX.FIELD_LOADER, new FieldLoader());
		cmdset.put(GBRGX.DESCJ, new PrintJavaFieldConstant());
		cmdset.put ( GBRGX.SET_FIELD_LINK, new SetFieldProp ());
		cmdset.put(GBRGX.CROP, new CropData());
		cmdset.put (GBRGX.PRINT_PREVIOUS_COMMAND, new PreviousCommand());
		cmdset.put ( GBRGX.COORDINATES, new CoordinateSearch() );
		cmdset.put ( GBRGX.REVERSE_COMPLEMENT, new ReverseComplement() );
		cmdset.put ( GBRGX.LOCUS, new LocusShow() );
		cmdset.put ( GBRGX.REPLACE_SOLR_CORE, new ReplaceSolrCore() );
//		cmdset.put ( GBRGX.RECURSIVE_SEARCH, new RecursiveCoreSearch() );
		//   /path/folder.flank(*ACTG*)
		cmdset.put ( GBRGX.FLANK_SEQUENCE, new RecursiveCoreSearchGetFlankingSeqeunce() );
		cmdset.put ( GBRGX.HIT_GENES, new GeneHitSearch() );
		cmdset.put ( GBRGX.JSON_CONFIG, new JSONConfig());
		cmdset.put ( GBRGX.LOAD_GTF, new LoadGTF());
		cmdset.put ( GBRGX.SEQUENCE_SEARCH, new SequenceSearch() );
		cmdset.put ( GBRGX.LOAD_TAB_DELIMITED_FILE, new LoadTextFile() );
		cmdset.put ( GBRGX.ADD_AND_LOAD_FILE_FROM_ABX_FILE, new loadABXFile() );
		cmdset.put ( GBRGX.LOAD_GENOME, new loadGenome() );
		cmdset.put ( GBRGX.LOAD_GFF, new loadGFF() );
		cmdset.put ( GBRGX.LOAD_GTF_FROM_WEB, new LoadGTF() );
		cmdset.put ( GBRGX.EVOLVE, new EvolveIndex() );
		cmdset.put ( GBRGX.UPDATE_INDEX, new UpdateIndex() );







		// tab completion options
		copsions.put("\\s*[A-Za-z0-9_]*(.search|search)" + "\\s*\\(",
				new SearchTargetHint());
		// tab completion options
		copsions.put("^[A-Za-z0-9_]*",
				new LsHint());

		copsions.put(GBRGX.COMMAND_LINE_COMMAND,
				new LsHint("desc"));
		copsions.put(GBRGX.COMMAND_LINE_COMMAND,
				new LsHint("cd"));

		copsions.put("\\s*[A-Za-z0-9_]*\\s+", new ListLocalHint());
		copsions.put(GBRGX.SEARCH_FORMAT_OUTPUT + "|", new PipeHint());

	}

	public void exec(String line, String key_v) throws UsageException {
		Set<String> keys = cmdset.keySet();
		boolean found = false;
		if (line != null)
			line = line.trim();
		for (String key : keys) {
			if (line.matches(key)) {
				line = expand(line);
				execMod(cmdset.get(key), line, key_v);
				found = true;
				return;
			}
		}
		if (!found) {
			GB.gogb(parse(line));
			//GB.print ( " Command not found . " + line );
		}
	}

	private String expand(String line) {
		GBVariables vars = GB.getVariables();
		Set<String> vs = vars.getSets();
		for (String key : vs) {
			String k = "\\$" + key;
			if (line.contains(k)) {
				GBV v = vars.getVariable(key);
				line.replaceAll(k, v.toString());
				return v.toString();
			}
		}
		return line;
	}

	private static String[] parse(String line) {
		String[] args = line.split("\\s+");
		return args;
	}

	public boolean matches(String line) {
		line = line.trim();
		Set<String> keys = cmdset.keySet();
		for (String key : keys) {
			if (line.matches(key))
				return true;
		}
		return false;
	}

	/**
	 * This is going to execute the dynamically loaded command.
	 * 
	 * @param line
	 * @throws UsageException
	 * @throws ClassNotFoundException
	 */
	private static void execMod(GBPlugin plugin, String line, String key)
			throws UsageException {
		plugin.exec(line, key);
	}

	public  GBPlugin getPlugin(String c1) {
		c1 = c1.trim();
		Set<String> keys = cmdset.keySet();
		for (String key : keys) {
			if (c1.matches(key)) {
				return cmdset.get(key);
			}
		}
		return null;
	}

	public  void printCommands() {
		String line = "";
		Set<String> keys = cmdset.keySet();
		if (line != null)
			line = line.trim();
		for (String key : keys) {
			GB.print(key.toString());
		}
	}

	public  void printHint(String b, final ConsoleReader reader) {
		Set<String> cs = commandSetHelp();
		for (String s : cs) {

			String[] re = GBNodes.getNodeNames(GB.pwd());
			for ( String f : re )
			{
				if ( b.toLowerCase().startsWith(f))
				{
					reader.setCursorBuffer(f);
					try {
						reader.redrawLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
//					return;
				}
			}
			if (b.matches(s)) {
				CommandOption co = copsions.get(s);
				co.setCurrentBuffer(b);
				String t = co.toString();
				if ( t != null )
				{
//					GB.print ( "  ");
//					GB.print ( t );
//					GB.print ( "  ");
				}
				String buf = co.getNewBufferCommand ();
				if ( buf != null && buf.length()>0 ){
					reader.setCursorBuffer (buf);
				}
			}
		}
	}

	private  Set<String> commandSetHelp() {
		return copsions.keySet();
	}

}
