package restaurantdb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.aspectj.AnnotationTransactionAspect;
import org.springframework.transaction.support.TransactionTemplate;
import restaurantdb.aspect.TransactionAspect;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new org.springframework.jdbc.datasource.DataSourceTransactionManager(dataSource);
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    public AnnotationTransactionAspect annotationTransactionAspect(PlatformTransactionManager transactionManager) {
        AnnotationTransactionAspect aspect = AnnotationTransactionAspect.aspectOf();
        aspect.setTransactionManager(transactionManager);
        return aspect;
    }

    @Bean
    public TransactionAspect transactionAspect() {
        return new TransactionAspect();
    }
}