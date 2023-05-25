package lk.ijse.dep10.pos;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebRootConfig {
    @Bean
    public BasicDataSource basicDataSource(){

        BasicDataSource bds =  new BasicDataSource();
        bds.setUsername("root");
        bds.setPassword("rasiya");
        bds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        bds.setUrl("jdbc:mysql://localhost:3306/dep10_pos");
        bds.setMaxTotal(50);
        bds.setInitialSize(20);
        return bds;
    }
}
