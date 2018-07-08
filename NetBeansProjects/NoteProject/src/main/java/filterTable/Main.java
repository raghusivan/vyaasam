package filterTable;

import au.com.notes.dao.impl.CoreDAO;
import com.note.beans.Note;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
//import org.apache.commons.lang3.StringUtils;

/**
 * Main class to start the application.
 *
 * @author Marco Jakob
 */

//public class Main extends Application {
public class Main{
    
   private final static Logger logger = Logger.getLogger(Main.class.getName());

    @Inject
    private CoreDAO coreDAO;

    public static void main(String[] args) {
        try {
//    		System.out.println(Paths.get(".").toAbsolutePath());
            //Main m = new Main();
            //System.out.println(m.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
//    		System.out.println(GetClassContainer(Main.class));
//        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
//        ctx.register(AppConfig.class);
//        ctx.refresh();
/*
            Weld weld = new Weld();
            WeldContainer container = weld.initialize();
            Application application = container.instance().select(Application.class).get();
            application.start(null);
             */
            CoreDAO coreDAO1 = new BootLoader().coreDAO;
            logger.info("ccc:"+coreDAO1);
            new Main().testDB();
//                weld.shutdown();
//            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testDB() {
        final String PERSISTENCE_UNIT_NAME = "notes";
        EntityManagerFactory factory;

        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

        EntityManager em = factory.createEntityManager();
// read the existing entries and write to console
        logger.info("coreDAO::::"+getCoreDAO());
        List<Note> notes = getCoreDAO().findWithNamedQuery("FROM Note");
        logger.info("CoreDAO working::::"+notes.toString());
        Query q = (Query) em.createQuery("FROM Note");
        System.out.println("RAGHU::::::" + q);
        List<Note> todoList = q.getResultList();
        System.out.println(todoList);
        for (Note todo : todoList) {
            System.out.println(todo);
        }
        System.out.println("Size: " + todoList.size());

        /*
                // create new todo
        List<Integer> ints = Stream.of(10, 20, 40, 30, 50).collect(Collectors.toList());
        for (int i : ints) {
                    em.getTransaction().begin();
            Note todo = new Note();
            todo.setContent("Content textsssssss"+i);
            todo.setType("general.txt-from "+i);
            em.persist(todo);
            em.getTransaction().commit();
        }
        ints.forEach((i) -> System.out.print(i + " ")  );
        */
        em.close();
    }

//    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sorting and Filtering");

        try {
            System.out.println("RAGHU TEST:::" + getClass().getResource("/PersonTable.fxml"));

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/PersonTable.fxml"));

//            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/PersonTable.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    public static String GetClassContainer(Class c) {
    if (c == null) {
        throw new NullPointerException("The Class passed to this method may not be null");
    }
    try {
        while(c.isMemberClass() || c.isAnonymousClass()){
            c = c.getEnclosingClass(); //Get the actual enclosing file
        }
        if (c.getProtectionDomain().getCodeSource() == null) {
            //This is a proxy or other dynamically generated class, and has no physical container,
            //so just return null.
            return null;
        }
        String packageRoot;
        try {
            //This is the full path to THIS file, but we need to get the package root.
            String thisClass = c.getResource(c.getSimpleName() + ".class").toString();
            packageRoot = StringUtils.replaceLast(thisClass, Pattern.quote(c.getName().replaceAll("\\.", "/") + ".class"), "");
            if(packageRoot.endsWith("!/")){
                packageRoot = StringUtils.replaceLast(packageRoot, "!/", "");
            }
        } catch (Exception e) {
            //Hmm, ok, try this then
            packageRoot = c.getProtectionDomain().getCodeSource().getLocation().toString();
        }
        packageRoot = URLDecoder.decode(packageRoot, "UTF-8");
        return packageRoot;
    } catch (Exception e) {
        throw new RuntimeException("While interrogating " + c.getName() + ", an unexpected exception was thrown.", e);
    }
}
     */

    /**
     * @return the coreDAO
     */
    public CoreDAO getCoreDAO() {
        return coreDAO;
    }

    /**
     * @param coreDAO the coreDAO to set
     */
    public void setCoreDAO(CoreDAO coreDAO) {
        this.coreDAO = coreDAO;
    }


    
    

}
