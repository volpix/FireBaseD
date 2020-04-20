package mx.edu.tesoem.isc.karm.FireBase;

import com.google.firebase.database.FirebaseDatabase;

public class MyFirebase extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
