package restaurantdb.aspect;

import java.sql.Connection;
import java.sql.Savepoint;
import javax.sql.DataSource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import restaurantdb.annotation.MyTransactional;

@Aspect
@Component
public class TransactionAspect {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DataSource dataSource;

    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private static final ThreadLocal<Savepoint> savepointHolder = new ThreadLocal<>();

    @Around("@annotation(myTransactional)")
    public Object around(ProceedingJoinPoint joinPoint, MyTransactional myTransactional) throws Throwable {
        if (myTransactional.propagation() == Propagation.NESTED) {
            return handleNestedTransaction(joinPoint);
        } else {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(getPropagationBehavior(myTransactional.propagation()));
            def.setReadOnly(myTransactional.readOnly());

            TransactionStatus status = transactionManager.getTransaction(def);
            try {
                Object result = joinPoint.proceed();
                if (!status.isCompleted()) {
                    transactionManager.commit(status);
                }
                return result;
            } catch (Throwable ex) {
                if (!status.isCompleted()) {
                    transactionManager.rollback(status);
                }
                throw ex;
            }
        }
    }

    private int getPropagationBehavior(Propagation propagation) {
        return switch (propagation) {
            case REQUIRED -> DefaultTransactionDefinition.PROPAGATION_REQUIRED;
            case REQUIRES_NEW -> DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW;
            case SUPPORTS -> DefaultTransactionDefinition.PROPAGATION_SUPPORTS;
            case NOT_SUPPORTED -> DefaultTransactionDefinition.PROPAGATION_NOT_SUPPORTED;
            case MANDATORY -> DefaultTransactionDefinition.PROPAGATION_MANDATORY;
            case NEVER -> DefaultTransactionDefinition.PROPAGATION_NEVER;
            default -> throw new IllegalArgumentException("Unsupported propagation behavior: " + propagation);
        };
    }

    private Object handleNestedTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        Connection connection = connectionHolder.get();
        Savepoint savepoint = null;

        if (connection == null) {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            connectionHolder.set(connection);
        }

        try {
            savepoint = connection.setSavepoint();
            savepointHolder.set(savepoint);
            Object result = joinPoint.proceed();
            connection.releaseSavepoint(savepoint);
            savepointHolder.remove();
            return result;
        } catch (Throwable ex) {
            if (connection != null && savepoint != null) {
                connection.rollback(savepoint);
                savepointHolder.remove();
            }
            throw ex;
        } finally {
            if (connection != null && savepointHolder.get() == null) {
                connection.setAutoCommit(true);
                connectionHolder.remove();
                connection.close();
            }
        }
    }
}
