package ua.shapoval.configure;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.shapoval.CryptoTool;

@Configuration
public class CryptoToolConfiguration {

    @Value("${hashValue}")
    private String hashValue;

    @Bean
    public CryptoTool getCryptoTool(){
        return new CryptoTool(hashValue);
    }

}
