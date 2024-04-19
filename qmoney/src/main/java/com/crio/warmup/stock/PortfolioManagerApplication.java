
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {


  private static RestTemplate restTemplate = new RestTemplate();


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Task:
  // - Read the json file provided in the argument[0], The file is available in the classpath.
  // - Go through all of the trades in the given file,
  // - Prepare the list of all symbols a portfolio has.
  // - if "trades.json" has trades like
  // [{ "symbol": "MSFT"}, { "symbol": "AAPL"}, { "symbol": "GOOGL"}]
  // Then you should return ["MSFT", "AAPL", "GOOGL"]
  // Hints:
  // 1. Go through two functions provided - #resolveFileFromResources() and #getObjectMapper
  // Check if they are of any help to you.
  // 2. Return the list of all symbols in the same order as provided in json.

  // Note:
  // 1. There can be few unused imports, you will need to fix them to make the build pass.
  // 2. You can use "./gradlew build" to check if your code builds successfully.


  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {


    File file = resolveFileFromResources(args[0]);
    ObjectMapper objectmapper = getObjectMapper();
    PortfolioTrade[] trades = objectmapper.readValue(file, PortfolioTrade[].class);


    List<String> symbols = new ArrayList<>();
    for (PortfolioTrade t : trades) {
      symbols.add(t.getSymbol());
    }
    return symbols;
  }



  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  // Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  // for the stocks provided in the Json.
  // Use the function you just wrote #calculateAnnualizedReturns.
  // Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.



  // TODO: CRIO_TASK_MODULE_REST_API
  // Find out the closing price of each stock on the end_date and return the list
  // of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>



  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader().getResource(filename).toURI())
        .toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Follow the instructions provided in the task documentation and fill up the correct values for
  // the variables provided. First value is provided for your reference.
  // A. Put a breakpoint on the first line inside mainReadFile() which says
  // return Collections.emptyList();
  // B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  // following the instructions to run the test.
  // Once you are able to run the test, perform following tasks and record the output as a
  // String in the function below.
  // Use this link to see how to evaluate expressions -
  // https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  // 1. evaluate the value of "args[0]" and set the value
  // to the variable named valueOfArgument0 (This is implemented for your reference.)
  // 2. In the same window, evaluate the value of expression below and set it
  // to resultOfResolveFilePathArgs0
  // expression ==> resolveFileFromResources(args[0])
  // 3. In the same window, evaluate the value of expression below and set it
  // to toStringOfObjectMapper.
  // You might see some garbage numbers in the output. Dont worry, its expected.
  // expression ==> getObjectMapper().toString()
  // 4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  // second place from top to variable functionNameFromTestFileInStackTrace
  // 5. In the same window, you will see the line number of the function in the stack trace window.
  // assign the same to lineNumberFromTestFileInStackTrace
  // Once you are done with above, just run the corresponding test and
  // make sure its working as expected. use below command to do the same.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "trades.json";
    String toStringOfObjectMapper = "ObjectMapper";
    String functionNameFromTestFileInStackTrace = "mainReadFile";
    String lineNumberFromTestFileInStackTrace = "";


    return Arrays.asList(
        new String[] {valueOfArgument0, resultOfResolveFilePathArgs0, toStringOfObjectMapper,
            functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace});
  }


  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    File reader = resolveFileFromResources(args[0]);
    ObjectMapper om = getObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    PortfolioTrade[] portfolios = om.readValue(reader, PortfolioTrade[].class);

    List<String> symbolsSortedByCloseValues = Arrays.stream(portfolios).map(symbol -> {
      String result = restTemplate.getForObject("https://api.tiingo.com/tiingo/daily/"
          + symbol.getSymbol() + "/prices?startDate=" + symbol.getPurchaseDate() + "&endDate="
          + args[1] + "&token=" + "56ced5d79d2bfeedeb35aa03a11d6d68a33074f7", String.class);
      List<TiingoCandle> collection = new ArrayList<>();
      try {
        collection = om.readValue(result, new TypeReference<ArrayList<TiingoCandle>>() {});
      } catch (JsonProcessingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      Double closeValue =
          collection.isEmpty() ? 0.0 : collection.get(collection.size() - 1).getClose();
      if (collection.isEmpty()) {
        throw new RuntimeException();
      }
      return new AbstractMap.SimpleEntry<>(closeValue, symbol.getSymbol());
    }).sorted(Map.Entry.comparingByKey()) // Sort by close value
        .map(Map.Entry::getValue) // Extract symbol
        .collect(Collectors.toList());

    return symbolsSortedByCloseValues;



  }

  // TODO:
  // After refactor, make sure that the tests pass by using these two commands
  // ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  // ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename)
      throws IOException, URISyntaxException {
    File tradeFromJson = resolveFileFromResources(filename);
    ObjectMapper objMapper = getObjectMapper();

    PortfolioTrade[] pfTrade = objMapper.readValue(tradeFromJson, PortfolioTrade[].class);
    List<PortfolioTrade> tradeList = new ArrayList<>();

    for (PortfolioTrade trade : pfTrade) {
      tradeList.add(trade);
    }

    // return Collections.emptyList();
    return tradeList;

  }


  // TODO:
  // Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    LocalDate startDate = trade.getPurchaseDate();
    if (startDate.isAfter(endDate)) {
      throw new RuntimeException();
    }

    String uriTemplate = "https://api.tiingo.com/tiingo/daily/" + trade.getSymbol() + "/prices?"
        + "startDate=" + trade.getPurchaseDate().toString() + "&endDate=" + endDate.toString()
        + "&token=" + token;
    return uriTemplate;
  }

  // TODO:
  // Ensure all tests are passing using below command
  // ./gradlew test --tests ModuleThreeRefactorTest
  public static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    if(Objects.nonNull(candles))
    {
      if(candles.size() > 0)
        return candles.get(0).getOpen();
    }
    return 0.0;

  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    if(candles != null)
    {
      if(candles.size() > 0)
        return candles.get(candles.size()-1).getClose();
    }
    return 0.0;

  }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    String url = prepareUrl(trade, endDate, token);

    RestTemplate restTemplate = new RestTemplate();

    TiingoCandle[] tiingoCandlesArray = restTemplate.getForObject(url, TiingoCandle[].class);

    if(tiingoCandlesArray != null)
      return Arrays.asList(tiingoCandlesArray);

    return Collections.emptyList();

  }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
        LocalDate endDate = LocalDate.parse(args[1]);
        String token = PortfolioManagerApplication.getToken();
        List<PortfolioTrade> pfTrades = readTradesFromJson(args[0]);
        List<AnnualizedReturn> annualizedReturns = new ArrayList<>();

        annualizedReturns = getAnnualizedReturnList(pfTrades, endDate, token);
        
        return annualizedReturns;

  }
  public static final Comparator<AnnualizedReturn> sortAnnual = new Comparator<AnnualizedReturn>() {

    @Override
    public int compare(AnnualizedReturn s1, AnnualizedReturn s2) {

      return s2.getAnnualizedReturn().compareTo(s1.getAnnualizedReturn());

    }

  };
  public static List<AnnualizedReturn> getAnnualizedReturnList(List<PortfolioTrade> pfTrades, LocalDate endDate, String token)
  {
    List<AnnualizedReturn> annualizedReturns = new ArrayList<>();
    for(PortfolioTrade portFolioTrade: pfTrades)
    {
        List<Candle> tiingoCandles = fetchCandles(portFolioTrade, endDate, token);

        Double openingPrice = getOpeningPriceOnStartDate(tiingoCandles);
        Double closingPrice = getClosingPriceOnEndDate(tiingoCandles);

        AnnualizedReturn currAnnualizedReturn = calculateAnnualizedReturns(endDate, portFolioTrade, openingPrice, closingPrice);

        annualizedReturns.add(currAnnualizedReturn);

    }
    
    Collections.sort(annualizedReturns,sortAnnual);

    return annualizedReturns;
  }

  public static Double calculateTotalReturn(Double buyPrice, Double sellPrice)
  {
      return  (sellPrice - buyPrice)/buyPrice;
  }

  public static Double getTotalNumberOfYears(LocalDate startDate, LocalDate endDate)
  {
      Double topicalyear = 365.24;
      return ChronoUnit.DAYS.between(startDate, endDate)/topicalyear;
  }

  public static Double getAnnualizedReturns(Double totalReturns, Double total_num_years)
  {
      Double annualized_returns = Math.pow((Double)(1+totalReturns), (Double)(1/total_num_years)) - 1;
      return annualized_returns;
  }

  

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  // Return the populated list of AnnualizedReturn for all stocks.
  // Annualized returns should be calculated in two steps:
  // 1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  // 1.1 Store the same as totalReturns
  // 2. Calculate extrapolated annualized returns by scaling the same in years span.
  // The formula is:
  // annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  // 2.1 Store the same as annualized_returns
  // Test the same using below specified command. The build should be successful.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, PortfolioTrade trade,
      Double buyPrice, Double sellPrice) {
      
       
      Double totalReturn = (sellPrice - buyPrice) / buyPrice;
      long daysBetween = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
      double years = (double) daysBetween / 365;
      Double annualret = Math.pow(1 + totalReturn, 1 / years) - 1;
      return new AnnualizedReturn(trade.getSymbol(), annualret, totalReturn);
  
  }

  public static String getToken() {
    return "5725a601162d3aae842ae372c49be1c71a97208f";
  }





















  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainReadFile(args));


    printJsonObject(mainReadQuotes(args));



    printJsonObject(mainCalculateSingleReturn(args));

  }
  




  }




  


