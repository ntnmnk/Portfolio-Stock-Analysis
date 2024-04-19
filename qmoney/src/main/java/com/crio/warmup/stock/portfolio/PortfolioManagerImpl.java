
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import com.crio.warmup.stock.PortfolioManagerApplication;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.crio.warmup.stock.PortfolioManagerApplication;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  public static final String TOKEN="5725a601162d3aae842ae372c49be1c71a97208f";

  RestTemplate restTemplate = new RestTemplate();
  private StockQuotesService stockQuotesService;



  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  protected PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
  }

  protected PortfolioManagerImpl(RestTemplate restTemplate, StockQuotesService stockQuotesService) {
    this.restTemplate = restTemplate;
    this.stockQuotesService = stockQuotesService;
  }



  // TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  // Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  // clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  // CHECKSTYLE:OFF



  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  // CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Extract the logic to call Tiingo third-party APIs to a separate function.
  // Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException, RuntimeException, StockQuoteServiceException
       {
    return this.stockQuotesService.getStockQuote(symbol, from, to);

     
      
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
        + "startDate=$STARTDATE&endDate=$ENDDATE&token=5725a601162d3aae842ae372c49be1c71a97208f";
        
    return uriTemplate;
  }


  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) throws StockQuoteServiceException, RuntimeException
       
     {
    // TODO Auto-generated method stub
    List<AnnualizedReturn> annualizedReturns = new ArrayList<>();

    String token = PortfolioManagerApplication.getToken();

    for (PortfolioTrade portfolioTrade : portfolioTrades) {
      AnnualizedReturn annualizedReturn = getAnnualizedReturn(portfolioTrade, endDate);
      annualizedReturns.add(annualizedReturn);
    }

    // annualizedReturns = PortfolioManagerApplication.getAnnualizedReturnList(pfTrades, endDate,
    // token);
    Collections.sort(annualizedReturns, getComparator());
    return annualizedReturns;

  }

  
  
  public AnnualizedReturn getAnnualizedReturn(PortfolioTrade portfolioTrade, LocalDate endDate)
      throws StockQuoteServiceException, RuntimeException

  {
    AnnualizedReturn annualizedReturn = new AnnualizedReturn("", 0.0, 0.0);

    String symbol = portfolioTrade.getSymbol();
    LocalDate startDate = portfolioTrade.getPurchaseDate();

    try {
      List<Candle> tiingoCandles = getStockQuote(symbol, startDate, endDate);
      Double openingPrice = PortfolioManagerApplication.getOpeningPriceOnStartDate(tiingoCandles);
      Double closingPrice = PortfolioManagerApplication.getClosingPriceOnEndDate(tiingoCandles);

      annualizedReturn = PortfolioManagerApplication.calculateAnnualizedReturns(endDate,
          portfolioTrade, openingPrice, closingPrice);

    } catch (JsonProcessingException e) {
      System.out.println(e.getMessage());
      annualizedReturn = new AnnualizedReturn(symbol, Double.NaN, Double.NaN);
    }
    // catch (StockQuoteServiceException e)
    // {
    // System.out.println(e.getMessage());
    // annualizedReturn = new AnnualizedReturn(symbol, Double.NaN, Double.NaN);
    // }


    return annualizedReturn;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturnParallel(
      List<PortfolioTrade> portfolioTrades, LocalDate endDate, int numThreads)  throws InterruptedException, StockQuoteServiceException ,InterruptedException {

        List<AnnualizedReturn> annualizedReturns = new ArrayList<AnnualizedReturn>();
        
     List<Future<AnnualizedReturn>> futureReturnsList = new ArrayList<Future<AnnualizedReturn>>();
     final ExecutorService pool = Executors.newFixedThreadPool(numThreads);

  for (int i = 0; i < portfolioTrades.size(); i++) {

    PortfolioTrade trade = portfolioTrades.get(i);

    Callable<AnnualizedReturn> callableTask = () -> {
      return getAnnualizedReturn(trade, endDate);
    };
    Future<AnnualizedReturn> futureReturns = pool.submit(callableTask);
    futureReturnsList.add(futureReturns);
  }

  for (int i = 0; i < portfolioTrades.size(); i++) {
    Future<AnnualizedReturn> futureReturns = futureReturnsList.get(i);
    try {
      AnnualizedReturn returns = futureReturns.get();
      annualizedReturns.add(returns);
    } catch (ExecutionException e) {
      throw new StockQuoteServiceException("Error when calling the API", e);

    }
  }
  
   return annualizedReturns.stream().sorted(getComparator()).collect(Collectors.toList());


    // TODO Auto-generated method stub
    
  }


  // @Override
  // public
  //  List<AnnualizedReturn> calculateAnnualizedReturnParallel(
  //     List<PortfolioTrade> portfolioTrades, LocalDate endDate, int numThreads)
  //     throws InterruptedException, StockQuoteServiceException {
  //   // TODO Auto-generated method stub
  //   ExecutorService executor = Executors.newFixedThreadPool(numThreads);
  //   List<AnnualizedReturn> anreturns = new ArrayList<AnnualizedReturn>();
  //   List<Future<AnnualizedReturn>> list = new ArrayList<Future<AnnualizedReturn>>();

  //   for (PortfolioTrade symbol : portfolioTrades) {
  //     Callable<AnnualizedReturn> callable = new PortfolioCallable(symbol,endDate,this.stockQuotesService);
  //     Future<AnnualizedReturn> future = executor.submit(callable);
  //     list.add(future);
  //   }

  //   for (Future<AnnualizedReturn> fut : list) {
  //     try {
  //       anreturns.add(fut.get());
  //     } catch (ExecutionException e) {
  //       throw new StockQuoteServiceException("Execution exception");
  //     }
  //   }
  //   Collections.sort(anreturns, getComparator());

  //   executor.shutdown();

  //   return anreturns;



  // }
  


  public AnnualizedReturn getAnnualizedAndTotalReturns(PortfolioTrade trade, LocalDate endDate)
      throws StockQuoteServiceException, RuntimeException {
    LocalDate startDate = trade.getPurchaseDate();
    String symbol = trade.getSymbol();


    Double buyPrice = 0.0, sellPrice = 0.0;


    try {
      LocalDate startLocalDate = trade.getPurchaseDate();


      List<Candle> stocksStartToEndFull = getStockQuote(symbol, startLocalDate, endDate);


      Collections.sort(stocksStartToEndFull, (candle1, candle2) -> {
        return candle1.getDate().compareTo(candle2.getDate());
      });

      Candle stockStartDate = stocksStartToEndFull.get(0);
      Candle stocksLatest = stocksStartToEndFull.get(stocksStartToEndFull.size() - 1);


      buyPrice = stockStartDate.getOpen();
      sellPrice = stocksLatest.getClose();
      endDate = stocksLatest.getDate();


    } catch (JsonProcessingException e) {
      throw new RuntimeException();
    }
    Double totalReturn = (sellPrice - buyPrice) / buyPrice;


    long daysBetweenPurchaseAndSelling = ChronoUnit.DAYS.between(startDate, endDate);
    Double totalYears = (double) (daysBetweenPurchaseAndSelling) / 365;


    Double annualizedReturn = Math.pow((1 + totalReturn), (1 / totalYears)) - 1;
    return new AnnualizedReturn(symbol, annualizedReturn, totalReturn);


  }



  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Modify the function #getStockQuote and start delegating to calls to
  // stockQuoteService provided via newly added constructor of the class.
  // You also have a liberty to completely get rid of that function itself, however, make sure
  // that you do not delete the #getStockQuote function.
  // @Override
  //   public List<AnnualizedReturn> calculateAnnualizedReturnParallel(
  //       List<PortfolioTrade> portfolioTrades, LocalDate endDate, int numThreads)
  //       throws InterruptedException, StockQuoteServiceException {
      
  //         ExecutorService executor = Executors.newFixedThreadPool(numThreads);

  //           List<Future<AnnualizedReturn>> futures = new ArrayList<>();
  //           //
  //           for (int i = 0; i < numThreads && i< portfolioTrades.size(); i++) {
  //               Callable<AnnualizedReturn> task = new TaskCallable(endDate,portfolioTrades.get(i));
  //               Future<AnnualizedReturn> future = executor.submit(task);
  //               futures.add(future);
  //           }

  //           // Collect the results from the futures
  //           List<AnnualizedReturn> results = new ArrayList<>();
  //           for (Future<AnnualizedReturn> future : futures) {
  //               try {
  //                   AnnualizedReturn annualizedReturn = future.get();
  //                   results.add(annualizedReturn);
  //               } catch (InterruptedException | ExecutionException e) {
  //                   throw new StockQuoteServiceException("Rate limit excided");
  //               }
  //           }
  //           executor.shutdown();
  //           Collections.sort(results,getComparator());
  //           return results;
  //       }

  //   class TaskCallable implements Callable<AnnualizedReturn> {
  //       PortfolioTrade trades;
  //       LocalDate endDate;


  //       public TaskCallable(LocalDate endDate, PortfolioTrade trades) {
  //           this.endDate = endDate;
  //           this.trades = trades;
  //       }

  //       @Override
  //       public AnnualizedReturn call() throws Exception {
  //         List<Candle> candles = stockQuotesService.getStockQuote(trades.getSymbol(), trades.getPurchaseDate(), endDate);
  //         // if(candles != null){
  //           Candle tiingoCandle = candles.get(0);
  //           Candle tiingoCandlelast = candles.get(candles.size()-1);
  //           AnnualizedReturn annualizedReturn = PortfolioManagerApplication.calculateAnnualizedReturns(tiingoCandlelast.getDate(), trades, tiingoCandle.getOpen(), tiingoCandlelast.getClose());
  //           return annualizedReturn;
  //       }
  //   }



}
