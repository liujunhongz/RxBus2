package tv.lycam.rxbus.app;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hwangjr.rxbus.app.R;

import java.util.ArrayList;

import timber.log.Timber;
import tv.lycam.rxbus.RxBus;

/**
 * Activity to show the story.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Mam to birth Tom.
     */
    private CatMam catMam = new CatMam();
    /**
     * Mam to birth mouse.
     */
    private MouseMam mouseMam = new MouseMam();

    /**
     * Init view and bus provider.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mouse mouse = mouseMam.birth();
                mouse.squeak();
                Snackbar.make(view, "Birth a mouse and squeak ! " + mouse, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                Timber.e("Haha, i am " + mouse);
            }
        });

        RxBus.get().register(mouseMam);
        RxBus.get().register(catMam.birth());

        Timber.e("Moust Mam has registed? " + RxBus.get().hasRegistered(mouseMam));
    }

    /**
     * Unregister the register object.
     */
    @Override
    protected void onDestroy() {
        RxBus.get().unregister(mouseMam);
        ArrayList<Cat> cats = catMam.getCats();
        for (Cat cat : cats) {
            RxBus.get().unregister(cat);
        }
        super.onDestroy();
    }

}
