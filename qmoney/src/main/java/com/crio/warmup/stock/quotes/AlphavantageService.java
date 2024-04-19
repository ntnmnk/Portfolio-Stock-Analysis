
package com.crio.warmup.stock.quotes;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;

import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;

import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AlphavantageService implements StockQuotesService {
  private RestTemplate restTemplate;

  public AlphavantageService(RestTemplate restTemplate)
  {
    this.restTemplate = restTemplate;
  }
  
  private static Boolean isValidDate(LocalDate currDate, LocalDate from, LocalDate to)
  {
      if((currDate.equals(from) || currDate.isAfter(from)) && (currDate.equals(to) || currDate.isBefore(to)))
      {
          return true;
      }

      return false;
  }

  private static List<Candle> getAlphaventageCandleInStartAndEndDate(Map<LocalDate, AlphavantageCandle> alphavantageCandlesMap, LocalDate from, LocalDate to)
  {
    if (Objects.isNull(alphavantageCandlesMap)) {
      return Collections.emptyList(); // Return an empty list if the map is null
  }

  return alphavantageCandlesMap.entrySet().stream()
  .filter(entry -> isValidDate(entry.getKey(), from, to))
  .map(entry -> {
      AlphavantageCandle currAlphavantageCandle = entry.getValue();
      if (Objects.nonNull(currAlphavantageCandle)) {
          currAlphavantageCandle.setDate(entry.getKey());
          return currAlphavantageCandle;
      } else {
          return null;
      }
  })
  .filter(Objects::nonNull) // Filter out null candles
  .collect(Collectors.toList());
    }
    
    private static ObjectMapper getObjectMapper() {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      return objectMapper;
    }
  
  
  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException,RuntimeException, StockQuoteServiceException {
    // TODO Auto-generated method stub

    List<Candle> alphavantageCandles;
    Map<LocalDate, AlphavantageCandle> dailyResponses;
    
    try{
      String url = buildAlphavantageUrl(symbol); 
      String apiResponse = restTemplate.getForObject(url, String.class);
      // if (apiResponse == null) {
      //     throw new StockQuoteServiceException("API response is null");
      // }
      ObjectMapper objectMapper = getObjectMapper();
      dailyResponses = objectMapper.readValue(apiResponse, AlphavantageDailyResponse.class).getCandles();
      // if (response == null || response.getCandles() == null) {
      //     throw new StockQuoteServiceException("Invalid response from API");
      // }
      
       alphavantageCandles = getAlphaventageCandleInStartAndEndDate(dailyResponses, from, to);

      Collections.reverse(alphavantageCandles);
      return alphavantageCandles;


    } catch(NullPointerException e){
           throw new StockQuoteServiceException("");
    }
    // catch (StockQuoteServiceException e)
    // {
    //     throw new StockQuoteServiceException("");
    // }catch (RuntimeException e)
    // {
    //     throw new RuntimeException("");
    // }
    catch (JsonProcessingException e) {

      throw new StockQuoteServiceException("Error processing JSON response", e);

    }

   
  } 




  

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
  //  to fetch daily adjusted data for last 20 years.
  //  Refer to documentation here: https://www.alphavantage.co/documentation/
  //  --
  //  The implementation of this functions will be doing following tasks:
  //    1. Build the appropriate url to communicate with third-party.
  //       The url should consider startDate and endDate if it is supported by the provider.
  //    2. Perform third-party communication with the url prepared in step#1
  //    3. Map the response and convert the same to List<Candle>
  //    4. If the provider does not support startDate and endDate, then the implementation
  //       should also filter the dates based on startDate and endDate. Make sure that
  //       result contains the records for for startDate and endDate after filtering.
  //    5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
  //  IMP: Do remember to write readable and maintainable code, There will be few functions like
  //    Checking if given date falls within provided date range, etc.
  //    Make sure that you write Unit tests for all such functions.
  //  Note:
  //  1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  //  2. Run the tests using command below and make sure it passes:
  //    ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  1. Write a method to create appropriate url to call Alphavantage service. The method should
  //     be using configurations provided in the {@link @application.properties}.
  //  2. Use this method in #getStockQuote.




  protected static String getAPIKey()
  {
      return "K5NXLX94U1QML672";
  }

  protected static String buildAlphavantageUrl(String symbol)
  {
      String urlTemplate = "https://www.alphavantage.co/query?"
      + "function=TIME_SERIES_DAILY&symbol=$SYMBOL&apikey=$APIKEY";

      String apiKey = AlphavantageService.getAPIKey();
      String url = urlTemplate.replace("$SYMBOL", symbol).replace("$APIKEY", apiKey);


      return url;

  }

}




  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //   1. Update the method signature to match the signature change in the interface.
  //   2. Start throwing new StockQuoteServiceException when you get some invalid response from
  //      Alphavantage, or you encounter a runtime exception during Json parsing.
  //   3. Make sure that the exception propagates all the way from PortfolioManager, so that the
  //      external user's of our API are able to explicitly handle this exception upfront.
  //CHECKSTYLE:OFF







