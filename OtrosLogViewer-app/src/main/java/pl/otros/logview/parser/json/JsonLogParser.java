package pl.otros.logview.parser.json;

import pl.otros.logview.LogData;
import pl.otros.logview.importer.InitializationException;
import pl.otros.logview.parser.LogParser;
import pl.otros.logview.parser.ParserDescription;
import pl.otros.logview.parser.ParsingContext;
import pl.otros.logview.pluginable.AbstractPluginableElement;

import java.text.ParseException;
import java.util.Optional;
import java.util.Properties;

public class JsonLogParser extends AbstractPluginableElement implements LogParser {

  private final ParserDescription parserDescription;
  private final JsonExtractor jsonExtractor;


  public JsonLogParser() {
    super("Json", "Json log parser");
    parserDescription = new ParserDescription();
    parserDescription.setDisplayName(name);
    parserDescription.setDescription(description);
    jsonExtractor = new JsonExtractor();
  }

  @Override
  public void init(Properties properties) throws InitializationException {
    jsonExtractor.init(properties);
    parserDescription.setDisplayName(properties.getProperty("name","Unnamed json parser"));
    parserDescription.setDescription(properties.getProperty("description","<Without description>"));
  }

  @Override
  public void initParsingContext(ParsingContext parsingContext) {
    parsingContext.setDateFormat(jsonExtractor.createDateFormatter());

  }

  @Override
  public LogData parse(String line, ParsingContext parsingContext) throws ParseException {

    final StringBuilder unmatchedLog = parsingContext.getUnmatchedLog();
    if (unmatchedLog.length()==0 && !line.startsWith("{")){
      return null;
    }

    unmatchedLog.append(line).append("\n");
    final Optional<LogData> logData = jsonExtractor.parseJsonLog(unmatchedLog.toString(), parsingContext.getDateFormat());
    if (logData.isPresent()) {
      unmatchedLog.setLength(0);
      return logData.get();
    }
    return null;
  }


  @Override
  public ParserDescription getParserDescription() {
    return parserDescription;
  }

  @Override
  public int getVersion() {
    return 1;
  }

  @Override
  public int getApiVersion() {
    return LOG_PARSER_VERSION_1;
  }



  @Override
  public String getName() {
    return parserDescription.getDisplayName();
  }

  @Override
  public String getDescription() {
    return parserDescription.getDescription();
  }
}

