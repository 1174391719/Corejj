import android.app.Application;

import com.maxi.corejj.infrastucture.utils.L;

public class MyApplication extends Application {
    private static final String TAG = "FilePathUtils";

    @Override
    public void onCreate() {
        super.onCreate();
        L.d(TAG, "onCreate. ");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        L.d(TAG, "onTerminate. ");
    }
}
