package guru.springframework.msscssm.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MsscSsmApplicationConfig {
  @Bean
  public Logger getLogger()
  {
    return LoggerFactory.getLogger(Slf4j.class);
  }
}
