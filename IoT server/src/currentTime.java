import java.text.SimpleDateFormat;
import java.util.Calendar;

public abstract class currentTime {

    public static String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss'-'dd.MM.yyyy");
        System.out.println( sdf.format(cal.getTime()) );
        
        return sdf.format(cal.getTime());
    }

}