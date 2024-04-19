
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

public static final String TOKEN="5725a601162d3aae842ae372c49be1c71a97208f";

private RestTemplate restTemplate;
 
@Autowired
  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException , StockQuoteServiceException {
    // TODO Auto-generated method stub

    if(from.compareTo(to)>=0){
         throw new RuntimeException();
    }
  
  String url = builduriTemplate(symbol,from,to);
  try{
      String response = restTemplate.getForObject(url, String.class);
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      Candle[] obj = objectMapper.readValue(response, TiingoCandle[].class);

      if(response == null || obj == null)
          throw new StockQuoteServiceException("");
      // if(obj==null) return new ArrayList<>();
      // else return Arrays.asList(obj);
      if(obj != null) return Arrays.asList(obj);
  }catch(StockQuoteServiceException e)
  {
      throw new StockQuoteServiceException("");
  }
  catch(RuntimeException e)
  {
      throw new RuntimeException("");
  }

  return new ArrayList<>();
}



  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.
  protected String builduriTemplate(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate=String.format("https://api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
    + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY");
    

   // String token = TiingoService.getToken();
    String url = uriTemplate.replace("$APIKEY", TOKEN).replace("$SYMBOL", symbol)
                            .replace("$STARTDATE", startDate.toString())
                            .replace("$ENDDATE", endDate.toString());
    return url; 
    
  }





  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  1. Update the method signature to match the signature change in the interface.
  //     Start throwing new StockQuoteServiceException when you get some invalid response from
  //     Tiingo, or if Tiingo returns empty results for whatever reason, or you encounter
  //     a runtime exception during Json parsing.
  //  2. Make sure that the exception propagates all the way from
  //     PortfolioManager#calculateAnnualisedReturns so that the external user's of our API
  //     are able to explicitly handle this exception upfront.

  //CHECKSTYLE:OFF



}
