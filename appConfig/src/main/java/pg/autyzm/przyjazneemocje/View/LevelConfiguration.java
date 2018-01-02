package pg.autyzm.przyjazneemocje.View;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import pg.autyzm.przyjazneemocje.R;
import pg.autyzm.przyjazneemocje.lib.Level;
import pg.autyzm.przyjazneemocje.lib.SqlliteManager;

import static pg.autyzm.przyjazneemocje.lib.SqlliteManager.getInstance;

/**
 * Created by joagi on 26.12.2017.
 */

public class LevelConfiguration extends AppCompatActivity {

    private int emotionsNumber = 2;
    private String[] emotions = {"wesoły", "smutny"};
    ArrayList praiseList = new ArrayList();
    private Level level = new Level();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_view);

        createTabMaterial();
        createTabLearningWays();
        createTabConsolidation();
        createTabTest();
        createTabSave();
    }

    private void createTabSave() {
        createDefaultStepName();
        updateInfo();
    }

    private void createTabTest() {
        createGridViewActiveInTest();
        activateNumberTryTest();
        activateNumberTimeTest();
    }

    private void createTabConsolidation() {
        createGridPraise();
        activateAddPraiseButton();
        createGridPrize();
    }

    private void createTabLearningWays() {
        createSpinnerCommand();
        activateNumberPhotos();
        activateNumberTry();
        activateNumberTime();
    }

    private void createTabMaterial() {
        createTabs();
        activeNumberEmotionPlusMinus();
        createListOfSpinners();
    }

    private void updateInfo() {
        TextView text = (TextView) findViewById(R.id.level_info);
        Map info = level.getInfo();
        text.setText(info.toString());

    }

    private void createDefaultStepName() {
        EditText editText = (EditText) findViewById(R.id.step_name);
        String name = "";
        for (String emotion : emotions) {
            name += emotion + " ";
        }
        editText.setText(name);
    }

    private void createGridViewActiveInTest() {
        ArrayList emotionList = new ArrayList();
        for (String emotion : emotions) {
            emotionList.add(new CheckboxGridBean(emotion, true));
        }
        GridView gridView = (GridView) findViewById(R.id.gridViewActiveInTest);
        CheckboxGridAdapter adapter = new CheckboxGridAdapter(emotionList, getApplicationContext());
        gridView.setAdapter(adapter);
    }

    private void activateAddPraiseButton() {
        final String newItem = ((TextView) findViewById(R.id.newPraise)).getText().toString();

        Button button = (Button) findViewById(R.id.buttonAddPraise);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                praiseList.add(new CheckboxGridBean(newItem, true));
                updateGridPraise();
            }
        });
    }

    private void createGridPrize() {
        GridCheckboxImageBean[] tabPhotos;
        tabPhotos = new GridCheckboxImageBean[1];
        tabPhotos[0] = new GridCheckboxImageBean("butterfly.png", 1, true, getContentResolver(), 0);

        final GridView listView = (GridView) findViewById(R.id.gridPrize);
        CheckboxImageAdapter adapter = new CheckboxImageAdapter(this, R.layout.grid_element_checkbox_image, tabPhotos);
        listView.setAdapter(adapter);

    }

    private void createGridPraise() {
        String[] praises = getResources().getStringArray(R.array.praise_array);
        for (String prize : praises) {
            praiseList.add(new CheckboxGridBean(prize, true));
        }
        updateGridPraise();
    }

    private void updateGridPraise() {
        GridView gridView = (GridView) findViewById(R.id.gridWordPraise);
        CheckboxGridAdapter adapter = new CheckboxGridAdapter(praiseList, getApplicationContext());
        gridView.setAdapter(adapter);
    }

    private void activateNumberTryTest() {
        activePlusMinus((EditText) findViewById(R.id.number_try_test), (Button) findViewById(R.id.button_minus_try_test), (Button) findViewById(R.id.button_plus_try_test));
    }

    private void activateNumberTimeTest() {
        activePlusMinus((EditText) findViewById(R.id.number_time_test), (Button) findViewById(R.id.button_minus_time_test), (Button) findViewById(R.id.button_plus_time_test));
    }

    private void activateNumberTime() {
        activePlusMinus((EditText) findViewById(R.id.time), (Button) findViewById(R.id.button_minus_time), (Button) findViewById(R.id.button_plus_time));
    }

    private void activateNumberTry() {
        activePlusMinus((EditText) findViewById(R.id.number_try), (Button) findViewById(R.id.button_minus_try), (Button) findViewById(R.id.button_plus_try));
    }

    private void activateNumberPhotos() {
        activePlusMinus((EditText) findViewById(R.id.number_photos), (Button) findViewById(R.id.button_minus_photos), (Button) findViewById(R.id.button_plus_photos));
    }

    private void activePlusMinus(final EditText textLabel, final Button minusButton, final Button plusButton) {
        minusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textLabel.setText(Integer.toString(Integer.parseInt(textLabel.getText().toString()) - 1));
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textLabel.setText(Integer.toString(Integer.parseInt(textLabel.getText().toString()) + 1));
            }
        });
    }

    private void createSpinnerCommand() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_command);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_command, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void createListOfSpinners() {
        final ListView selectedEmotionsList = (ListView) findViewById(R.id.selected_emotions_list);

        CustomAdapter adapter = new CustomAdapter();
        selectedEmotionsList.setAdapter(adapter);
    }

    private GridCheckboxImageBean[] getEmotionPhotos(String choosenEmotion) {
        SqlliteManager sqlm = getInstance(this);
        Cursor cursor = sqlm.givePhotosWithEmotion(choosenEmotion);
        int n = cursor.getCount();
        GridCheckboxImageBean tabPhotos[] = new GridCheckboxImageBean[n];
        while (cursor.moveToNext()) {
            tabPhotos[--n] = (new GridCheckboxImageBean(cursor.getString(3), cursor.getInt(1), true, getContentResolver(), cursor.getInt(0)));
        }
        return tabPhotos;
    }

    private String getEmotionName(int emotionNumber) {
        Configuration config = new Configuration(getBaseContext().getResources().getConfiguration());
        config.setLocale(Locale.ENGLISH);
        return getBaseContext().createConfigurationContext(config).getResources().getStringArray(R.array.emotions_array)[emotionNumber];
    }

    private void updateEmotionsGrid(int emotionNumber) {
        String emotion = getEmotionName(emotionNumber);
        GridCheckboxImageBean[] tabPhotos = getEmotionPhotos(emotion);

        final GridView listView = (GridView) findViewById(R.id.grid_photos);
        CheckboxImageAdapter adapter = new CheckboxImageAdapter(this, R.layout.grid_element_checkbox_image, tabPhotos);
        listView.setAdapter(adapter);
    }

    private void activeNumberEmotionPlusMinus() {
        final EditText nrEmotions = (EditText) findViewById(R.id.nr_emotions);

        final Button minusButton = (Button) findViewById(R.id.button_minus);
        minusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                emotionsNumber = Integer.parseInt(nrEmotions.getText().toString());
                emotionsNumber--;
                nrEmotions.setText(Integer.toString(emotionsNumber));
            }
        });

        final Button plusButton = (Button) findViewById(R.id.button_plus);
        plusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                emotionsNumber = Integer.parseInt(nrEmotions.getText().toString());
                emotionsNumber++;
                nrEmotions.setText(Integer.toString(emotionsNumber));
            }
        });
    }

    private void createTabs() {
        TabHost tab = (TabHost) findViewById(R.id.tabHost);
        tab.setup();

        String[] tabsFiles = {"tab1_material", "tab2_learning_ways", "tab3_consolidation", "tab4_test", "tab5_save"};
        TabHost.TabSpec spec;
        for (String tabFile : tabsFiles) {
            spec = tab.newTabSpec(tabFile);
            spec.setIndicator(getResourceString(tabFile));
            spec.setContent(getResourceId(tabFile));
            tab.addTab(spec);
        }
    }

    private int getResource(String variableName, String resourceName) {
        return getResources().getIdentifier(variableName, resourceName, getPackageName());
    }

    private int getResourceId(String resourceName) {
        return getResource(resourceName, "id");
    }

    private String getResourceString(String resourceName) {
        return getString(getResource(resourceName, "string"));
    }


    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return emotionsNumber;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.row_spinner, parent, false);

            Spinner spinner = (Spinner) convertView.findViewById(R.id.spinner_emotions);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(LevelConfiguration.this,
                    R.array.emotions_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            AdapterView.OnItemSelectedListener emotionSelectedListener = new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> spinner, View container,
                                           int position, long id) {
                    updateEmotionsGrid(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            };
            spinner.setOnItemSelectedListener(emotionSelectedListener);

            return convertView;
        }
    }
}
