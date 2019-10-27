package org.wildcat.camada.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableEncryptableProperties
@PropertySource("encrypted.properties")
public class AppConfigForJasyptStarter {
}
