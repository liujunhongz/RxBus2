package tv.lycam.rxbus.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hwangjr.rxbus.app.R;

import java.util.ArrayList;

import timber.log.Timber;
import tv.lycam.rxbus.RxBus;
import tv.lycam.rxbus.annotation.Subscribe;
import tv.lycam.rxbus.annotation.Tag;
import tv.lycam.rxbus.thread.EventThread;

/**
 * Activity to show the story.
 */
public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postByTag();
            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Main2Activity.this, Main3Activity.class));
            }
        });

        RxBus.get().register(this);

    }

    /**
     * Unregister the register object.
     */
    @Override
    protected void onDestroy() {
        RxBus.get().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void eat(String food) {
        // purpose
        Timber.e("Main2::copy that!" + food);
    }

    @Subscribe(
            thread = EventThread.IO,
            tags = {
                    @Tag(BusAction.EAT_MORE)
            }
    )
    public void eatMore(ArrayList<String> foods) {
        // purpose
        Timber.e("Main2::copy that!" + foods.toString());
    }

//    @Produce
//    public String produceFood() {
//        return "This is bread!";
//    }
//
//    @Produce(
//            thread = EventThread.IO,
//            tags = {
//                    @Tag(BusAction.EAT_MORE)
//            }
//    )
//    public ArrayList<String> produceMoreFood() {
//        ArrayList<String> list = new ArrayList<>();
//        list.add("This is breads!");
//        return list;
//    }

    public void post() {
        RxBus.get().post(this);
    }

    public void postByTag() {
        ArrayList<String> list = new ArrayList<>();
        list.add("hello world");
        RxBus.get().post(BusAction.EAT_MORE, list);
        RxBus.get().post("hello");
    }


    interface BusAction {
        String EAT_MORE = "EAT_MORE";
    }
}
