import data.repositories.SessionFactoryUtil;
import org.hibernate.SessionFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {

        // Initialize SessionFactory and ExecutorService
        SessionFactory sessionFactory = SessionFactoryUtil.getSessionFactory();
        ExecutorService executorService = Executors.newCachedThreadPool();


        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Shutdown ExecutorService
        executorService.shutdown();
    }
}