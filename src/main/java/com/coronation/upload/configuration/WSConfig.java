package com.coronation.upload.configuration;

import com.coronation.upload.ws.EntrustMultiFactorAuthImpl;
import com.coronation.upload.ws.EntrustMultiFactorAuthImplService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Toyin on 8/27/19.
 */
@Configuration
public class WSConfig {
    @Bean
    public EntrustMultiFactorAuthImpl entrustMultiFactorAuth() {
        EntrustMultiFactorAuthImplService entrustMultiFactorAuthImpl = new EntrustMultiFactorAuthImplService();
        return entrustMultiFactorAuthImpl.getEntrustMultiFactorAuthImplPort();
    }
}
