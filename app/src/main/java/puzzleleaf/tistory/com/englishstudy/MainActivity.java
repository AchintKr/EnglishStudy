package puzzleleaf.tistory.com.englishstudy;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    //drawer
    private DrawerLayout drawerLayout;
    private View drawerView;
    private Button openDrawer;
    private Button closeDrawer;
    private DrawerLayout.DrawerListener myDrawerListener;
    private CustomAdapter adapter;
    private Button saveEngData; //임시
    private Button addMenu;
    private ListView listView;
    ArrayList<MyDataList> engData;

    //viewPager
    private ViewPager pager;
    private PagerItem mPagerAdapter;
    ArrayList<String> word;
    ArrayList<String> mean;

    //popUp
    private PopupWindow mPopupWindow ;
    private EditText menuEdit;
    private EditText wordEdit;
    private EditText meanEdit;
    private Button menuAdd;
    private Button wordAdd;
    private Button addClose;
    private Button popupPasate;
    boolean manuNameFlag;
    View popupView;

    //붙여넣기 popup
    private PopupWindow pastePopup ;
    private View pasteView;
    private Button pasteAdd;
    private Button pasteCancel;
    private EditText pasteEdit;
    ClipboardManager clipBoard;
    ClipData.Item item ;

    //종료 popup
    private PopupWindow pop;
    //data
    String data ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //화면 계속 켜짐

        engData = new ArrayList<>();

        engData = MyValue.getEng(getApplicationContext());


        drawerInit(); //drawer 할당
        viewPagerInit(); // ViewPager 할당

    }

    @Override
    public void onBackPressed() {

        if(pop!=null && pop.isShowing())
        {
            pop.dismiss();
        }
        View popView = getLayoutInflater().inflate(R.layout.popup_finish, null);
        pop = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //popupView 에서 (LinearLayout 을 사용) 레이아웃이 둘러싸고 있는 컨텐츠의 크기 만큼 팝업 크기를 지정
        pop.showAtLocation(popView, Gravity.CENTER, 0, 0);

        Button cancel = (Button)popView.findViewById(R.id.popupCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });

        final Button fini = (Button)popView.findViewById(R.id.popupFinish);
        fini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

    //문자열 분해
    private void getWordText(String temp)
    {
        String t = "";
        for(int i=0;i<temp.length();i++)
        {
            if(!String.valueOf('|').equals(String.valueOf(temp.charAt(i)))
                    && !String.valueOf('/').equals(String.valueOf(temp.charAt(i))))
                t +=temp.charAt(i);

            else if(String.valueOf('|').equals(String.valueOf(temp.charAt(i))))
            {
                word.add(t);
                t="";
            }
            else if(String.valueOf('/').equals(String.valueOf(temp.charAt(i))))
            {
                mean.add(t);
                t="";
            }
        }
    }

    void popupInit()
    {
        manuNameFlag = false;
        popupView = getLayoutInflater().inflate(R.layout.popup_add, null);
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //popupView 에서 (LinearLayout 을 사용) 레이아웃이 둘러싸고 있는 컨텐츠의 크기 만큼 팝업 크기를 지정
        mPopupWindow.setFocusable(true);

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);



        menuAdd = (Button)popupView.findViewById(R.id.addMenuBtn);
        menuEdit = (EditText)popupView.findViewById(R.id.addMenuName);
        wordEdit = (EditText)popupView.findViewById(R.id.addWord);
        meanEdit = (EditText)popupView.findViewById(R.id.addMean);
        wordAdd = (Button)popupView.findViewById(R.id.addBtn);
        addClose = (Button)popupView.findViewById(R.id.addClose);
        popupPasate = (Button)popupView.findViewById(R.id.popupPaste);

        popupPasate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(manuNameFlag ==false)
                {
                    Toast.makeText(getApplicationContext(), "설정 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                    popupPasteInit();
            }
        });



        //메뉴 이름 지정하기
        menuAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!menuEdit.getText().toString().equals(""))
                {
                    MyValue.saveTempMenu=menuEdit.getText().toString();
                    manuNameFlag = true;
                    Toast.makeText(getApplicationContext(),MyValue.saveTempMenu,Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"값을 입력하세요",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //추가하기 버튼
        wordAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!wordEdit.getText().toString().equals("") && !meanEdit.getText().toString().equals(""))
                {
                    MyValue.saveTempContent+=wordEdit.getText().toString()+"|"+
                            meanEdit.getText().toString()+"/";
                    wordEdit.setText("");
                    meanEdit.setText("");
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"값을 모두 입력하세요",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //닫기버튼 -- 임시값 저장
        addClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(manuNameFlag ==false)
                {
                    Toast.makeText(getApplicationContext(), "설정 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(wordEdit.getText().toString().equals("") && meanEdit.getText().toString().equals("")) {
                    MyDataList temp = new MyDataList(MyValue.saveTempMenu, MyValue.saveTempContent);
                    adapterAddItem(temp);
                    MyValue.saveTempMenu = "";
                    MyValue.saveTempContent = "";

                    if (mPopupWindow.isShowing())
                        mPopupWindow.dismiss();
                    Toast.makeText(getApplicationContext(), "추가되었습니다.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "추가하기를 눌러주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void popupPasteInit()
    {
        mPopupWindow.dismiss();
        pasteView = getLayoutInflater().inflate(R.layout.popup_paste, null);
        pastePopup = new PopupWindow(pasteView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //popupView 에서 (LinearLayout 을 사용) 레이아웃이 둘러싸고 있는 컨텐츠의 크기 만큼 팝업 크기를 지정

        pastePopup.showAtLocation(pasteView, Gravity.CENTER, 0, 0);

        pasteAdd = (Button)pasteView.findViewById(R.id.pasteAdd);
        pasteCancel = (Button)pasteView.findViewById(R.id.pasteClose);
        pasteEdit = (EditText)pasteView.findViewById(R.id.pasteEdit);


        clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        item = clipBoard.getPrimaryClip().getItemAt(0);


        pasteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasteEdit.setText(item.getText().toString());
                MyValue.saveTempContent = pasteEdit.getText().toString();
            }
        });

        pasteCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyDataList temp = new MyDataList(MyValue.saveTempMenu, MyValue.saveTempContent);
                adapterAddItem(temp);
                MyValue.saveTempMenu = "";
                MyValue.saveTempContent = "";

                Toast.makeText(getApplicationContext(), "추가되었습니다.", Toast.LENGTH_SHORT).show();
                pastePopup.dismiss();
            }
        });




    }



    //뷰 페이저 초기화
    void viewPagerInit()
    {
        word = new ArrayList<>();
        mean = new ArrayList<>();
        getWordText(data);
        if(word.size() != mean.size())
        {
            word.clear();
            mean.clear();
        }
        pager = (ViewPager)findViewById(R.id.viewpager);
        mPagerAdapter = new PagerItem(getApplicationContext(),word,mean);
        pager.setAdapter(mPagerAdapter);
    }

    //drawer 할당 함수
    void drawerInit()
    {
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);
        listView = (ListView)findViewById(R.id.listView);
        adapter = new CustomAdapter(getApplicationContext(),engData,listView);
        listView.setAdapter(adapter);
        saveEngData = (Button)findViewById(R.id.engSave);
        addMenu = (Button)findViewById(R.id.addMenu);

        addMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupInit();
            }
        });


        saveEngData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyValue.saveEng(getApplicationContext(),engData);
                Toast.makeText(getApplicationContext(),"저장완료",Toast.LENGTH_SHORT).show();
            }
        });


        myDrawerListener = new DrawerLayout.DrawerListener() {
            public void onDrawerClosed(View drawerView) {
            }
            public void onDrawerOpened(View drawerView) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            }
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }
            public void onDrawerStateChanged(int newState) {
            }
        };

        openDrawer = (Button)findViewById(R.id.opendrawer);
        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        closeDrawer =(Button)findViewById(R.id.closedrawer);
        closeDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                drawerLayout.closeDrawers();
                data = MyValue.value;
                viewPagerInit();
                listView.setAdapter(adapter);
            }
        });

        drawerLayout.addDrawerListener(myDrawerListener);
    }

    void adapterAddItem(MyDataList temp)
    {
        adapter.add(temp);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

}
