package nl.arnovanoort.stockreader.exception;

import java.util.Optional;

public class StockReaderException extends RuntimeException{
  String description;
  Optional<Throwable> t;

  public StockReaderException(String description){
    this.description = description;
    this.t = Optional.empty();
  }

  public StockReaderException(String description, Throwable t){
    this.description = description;
    this.t = Optional.of(t);
  }
}
