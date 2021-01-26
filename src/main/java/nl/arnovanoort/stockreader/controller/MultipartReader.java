package nl.arnovanoort.stockreader.controller;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MultipartReader {

  /*
   Will be used later on to read zipped http response
   */
  public Flux<String> getLines(FilePart filePart) {
    Flux<String> result =  filePart.content()
        .map(dataBuffer -> {
          byte[] bytes = new byte[dataBuffer.readableByteCount()];
          dataBuffer.read(bytes);
          DataBufferUtils.release(dataBuffer);

          return new String(bytes, StandardCharsets.UTF_8);
        })
        .map(this::processAndGetLinesAsList)
        .flatMapIterable(Function.identity());
    return result;
  }

  private List<String> processAndGetLinesAsList(String string) {

    Supplier<Stream<String>> streamSupplier = string::lines;
    return  streamSupplier.get().filter(s -> !s.isBlank()).collect(Collectors.toList()) ;
  }
}
