package org.example;

/**
 * Hello world!
 *
 */
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@MapperScan("org.example.mapper")
public class App 
{
    public static void main( String[] args )
    {



        SpringApplication.run(App.class, args);

    }
}
