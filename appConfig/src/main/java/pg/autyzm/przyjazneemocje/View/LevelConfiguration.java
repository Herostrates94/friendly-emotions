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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import pg.autyzm.przyjazneemocje.R;
import pg.autyzm.przyjazneemocje.lib.entities.Level;
import pg.autyzm.przyjazneemocje.lib.SqlliteManager;

import static pg.autyzm.przyjazneemocje.lib.SqlliteManager.getInstance;

/**
 * Created by joagi on 26.12.2017.
 */

public class LevelConfiguration extends AppCompatActivity {

    ArrayList praiseList = new ArrayList();
    private Level level = new Level();
    String currentEmotionName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_view);

        initLevel();
        createTabMaterial();
        createTabLearningWays();
        createTabConsolidation();
        createTabTest();
        createTabSave();
        loadLevelIfItIsEditionMode();

    }


    private void loadLevelIfItIsEditionMode(){

        // Loaded level id retrieval

        Bundle b = getIntent().getExtras();
        int loadedLevelId = -1; // or other values
        if(b != null)
            loadedLevelId = b.getInt("key");


        if(loadedLevelId > 0){
            loadLevelFromDatabaseAndInjectDataToGUI(loadedLevelId);
        }

    }


    private void loadLevelFromDatabaseAndInjectDataToGUI(int loadedLevelId){

        SqlliteManager sqlm = getInstance(this);


        Cursor cur2 = sqlm.giveLevel(loadedLevelId);
        Cursor cur3 = sqlm.givePhotosInLevel(loadedLevelId);
        Cursor cur4 = sqlm.giveEmotionsInLevel(loadedLevelId);

        level = new Level(cur2, cur3, cur4);




        // 2 panel

        TextView pvPerLevel = (TextView) findViewById(R.id.number_photos);
        pvPerLevel.setText(level.getPhotosOrVideosShowedForOneQuestion() + "");

        TextView sublevels = (TextView) findViewById(R.id.number_try);
        sublevels.setText(level.getSublevelsPerEachEmotion() + "");

        // 3 panel



        // 4 panel

        EditText correctness = (EditText) findViewById(R.id.number_try_test);
        correctness.setText(level.getAmountOfAllowedTriesForEachEmotion() + "");

        EditText timeLimit = (EditText) findViewById(R.id.number_time_test);
        timeLimit.setText(level.getTimeLimit() + "");

        // panel 5

        EditText levelName = (EditText) findViewById(R.id.step_name);
        levelName.setText(level.getName());


    }




    private void initLevel(){
        level.addEmotion(0);
        level.addEmotion(1);
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
        for (int emotion : level.getEmotions()) {
            name += getEmotionNameInLocalLanguage(emotion) + " ";
        }
        editText.setText(name);
    }

    private void createGridViewActiveInTest() {
        ArrayList emotionList = new ArrayList();
        for (int emotion : level.getEmotions()) {
            String emotionName = getEmotionNameInLocalLanguage(emotion);
            emotionList.add(new CheckboxGridBean(emotionName, true));
        }
        GridView gridView = (GridView) findViewById(R.id.gridViewActiveInTest);
        CheckboxGridAdapter adapter = new CheckboxGridAdapter(emotionList, getApplicationContext());
        gridView.setAdapter(adapter);
    }

    private void activateAddPraiseButton() {

        Button button = (Button) findViewById(R.id.buttonAddPraise);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String newItem = ((TextView) findViewById(R.id.newPraise)).getText().toString();
                praiseList.add(new CheckboxGridBean(newItem, true));
                updateGridPraise();
            }
        });
    }

    private void createGridPrize() {
        GridCheckboxImageBean[] tabPhotos = getEmotionPhotos("prize");

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

    private String getEmotionNameInLocalLanguage(int emotionNumber) {
        Configuration config = new Configuration(getBaseContext().getResources().getConfiguration());
        return getBaseContext().createConfigurationContext(config).getResources().getStringArray(R.array.emotions_array)[emotionNumber];
    }

    private void updateEmotionsGrid(int emotionNumber) {
        String emotion = getEmotionName(emotionNumber);

        // Birgiel 09.01.17
        currentEmotionName = emotion;

        GridCheckboxImageBean[] tabPhotos = getEmotionPhotos(emotion);

        final GridView listView = (GridView) findViewById(R.id.grid_photos);
        CheckboxImageAdapter adapter = new CheckboxImageAdapter(this, R.layout.grid_element_checkbox_image, tabPhotos);
        listView.setAdapter(adapter);
    }

    private void updateSelectedEmotions(){
        createDefaultStepName();
        createGridViewActiveInTest();
        updateInfo();
    }

    private void activeNumberEmotionPlusMinus() {
        final EditText nrEmotions = (EditText) findViewById(R.id.nr_emotions);

        final Button minusButton = (Button) findViewById(R.id.button_minus);
        minusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                level.deleteEmotion(0);
                nrEmotions.setText(Integer.toString(level.getEmotions().size()));
                updateSelectedEmotions();
            }
        });

        final Button plusButton = (Button) findViewById(R.id.button_plus);
        plusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                level.addEmotion(Integer.parseInt(nrEmotions.getText().toString()));
                nrEmotions.setText(Integer.toString(level.getEmotions().size()));
                updateSelectedEmotions();
            }
        });
    }

    private void createTabs() {
        TabHost tab = (TabHost) findViewById(R.id.tabHost);
        tab.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tab) {
                if ("tab1_material".equals(tab)) {
                    findViewById(R.id.button_prev).setVisibility(View.INVISIBLE);
                } else {
                    findViewById(R.id.button_prev).setVisibility(View.VISIBLE);
                }
                ImageButton button = (ImageButton) findViewById(R.id.button_next);
                if ("tab5_save".equals(tab)) {
                    button.setImageResource(R.drawable.icon_save);
                } else {
                    button.setImageResource(R.drawable.icon_next);
                }
            }
        });
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

    public void nextTab(View view) {

        TabHost tabs = (TabHost) findViewById(R.id.tabHost);

        if(tabs.getCurrentTab() == 4){
            save();
        }else{
            tabs.setCurrentTab(tabs.getCurrentTab() + 1);
        }

    }

    void save(){

        gatherInfoFromGUI();
        saveLevelToDatabaseAndShowLevelSavedText();
        level.setId(0);

    }


    void saveLevelToDatabaseAndShowLevelSavedText(){

        SqlliteManager sqlm = getInstance(this);


        sqlm.saveLevelToDatabase(level);

        /* TODO: Null Pointer Exception for msg
        final TextView msg = (TextView) findViewById(R.id.saveMessage);
        msg.setVisibility(View.VISIBLE);
        msg.postDelayed(new Runnable() {
            public void run() {
                msg.setVisibility(View.INVISIBLE);
            }
        }, 2000);
        */
    }



    void gatherInfoFromGUI(){


        // 1 panel

        // save selected photos

        GridView listView = (GridView) findViewById(R.id.grid_photos);
        int size = listView.getChildCount();

        for(int i = 0; i < size; i++){

            listView.getChildAt(i);

        }


        // 2 panel

        TextView pvPerLevel = (TextView) findViewById(R.id.number_photos);
        level.setPhotosOrVideosShowedForOneQuestion(Integer.parseInt(pvPerLevel.getText() + ""));

        TextView sublevels = (TextView) findViewById(R.id.number_try);
        level.setSublevelsPerEachEmotion(Integer.parseInt(sublevels.getText() + ""));

        // question type

        Spinner spinner = (Spinner) findViewById(R.id.spinner_command);
        int spinnerPosition = spinner.getSelectedItemPosition();

        switch(spinnerPosition){
            case 0:
                level.setQuestionType(Level.Question.EMOTION_NAME);
                break;
            case 1:
                level.setQuestionType(Level.Question.SHOW_WHERE_IS_EMOTION_NAME);
                break;
            case 2:
                level.setQuestionType(Level.Question.SHOW_EMOTION_NAME);
                break;
        }

        // should questin be read aloud checkbox
        CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox);
        level.setShouldQuestionBeReadAloud(checkBox.isChecked());

        TextView secondsToHint = (TextView) findViewById(R.id.time);
        level.setSecondsToHint(Integer.parseInt(secondsToHint.getText() + ""));

        // hint types

        checkBox = (CheckBox)findViewById(R.id.checkBox3);
        if(checkBox.isChecked()){
            level.addHintType(Level.Hint.FRAME_CORRECT);
        }

        checkBox = (CheckBox)findViewById(R.id.checkBox5);
        if(checkBox.isChecked()){
            level.addHintType(Level.Hint.ENLARGE_CORRECT);
        }

        checkBox = (CheckBox)findViewById(R.id.checkBox6);
        if(checkBox.isChecked()){
            level.addHintType(Level.Hint.MOVE_CORRECT);
        }

        checkBox = (CheckBox)findViewById(R.id.checkBox4);
        if(checkBox.isChecked()){
            level.addHintType(Level.Hint.GREY_OUT_INCORRECT);
        }

        // 3 panel



        // 4 panel

        EditText correctness = (EditText) findViewById(R.id.number_try_test);
        level.setAmountOfAllowedTriesForEachEmotion(Integer.parseInt(correctness.getText() + ""));

        EditText timeLimit = (EditText) findViewById(R.id.number_time_test);
        level.setTimeLimit(Integer.parseInt(timeLimit.getText() + ""));

        // panel 5

        EditText levelName = (EditText) findViewById(R.id.step_name);
        level.setName(levelName.getText() + "");

    }

    public void prevTab(View view) {
        TabHost tabs = (TabHost) findViewById(R.id.tabHost);
        tabs.setCurrentTab(tabs.getCurrentTab() - 1);
    }

    private int getResource(String variableName, String resourceName) {
        return getResources().getIdentifier(variableName, resourceName, getPackageName());
    }

    private int getResourceId(String resourceName) {
        return getResource(resourceName, "id");
    }

    private String getResourceString(String resourceName) {
        return getResource(resourceName, "string") + "";
    }


    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return level.getEmotions().size();
        }

        @Override
        public Object getItem(int n) {
            return level.getEmotions().get(n);
    }

        @Override
        public long getItemId(int n) {
            return level.getEmotions().get(n);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.row_spinner, parent, false);

            Spinner spinner = (Spinner) convertView.findViewById(R.id.spinner_emotions);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(LevelConfiguration.this,
                    R.array.emotions_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(level.getEmotions().get(position));

            ImageButton button = (ImageButton) convertView.findViewById(R.id.button_edit);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateEmotionsGrid(level.getEmotions().get(position));
                }
            });

            AdapterView.OnItemSelectedListener emotionSelectedListener = new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> spinner, View container,
                                           int position, long id) {
                    updateEmotionsGrid(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            };
            spinner.setOnItemSelectedListener(emotionSelectedListener);

            return convertView;
        }
    }

    // klikniety checkbox przy zdjeciu emocji listener
    public void pictureClicked(View view){

        SqlliteManager sqlm = getInstance(this);

        //TODO: To slabo dziala. Jesli klikniesz w chechboxa ze zdjeciem (niewazne, czy zaznaczysz, czy odznaczysz,
        // to zostanie ono dodane do poziomu. Ale np. domyslnie zaznaczone nie zostana dodane, jesli ich nie klikniemy.
        /*


        GridView listView = (GridView) findViewById(R.id.grid_photos);
        int position = listView.getPositionForView(view);

        int photoId = sqlm.getPhotoIdByName(currentEmotionName + "" + (position + 1) + ".jpg");
        level.addPhoto(photoId);
        */

        // rozwiazanie tymczasowe: dodaj wszystkie zdjecia z danej emocji do poziomu

        Cursor cursor = sqlm.givePhotosWithEmotion(currentEmotionName);

        while (cursor.moveToNext()) {
            level.addPhoto(cursor.getInt(0));
        }


    }


}