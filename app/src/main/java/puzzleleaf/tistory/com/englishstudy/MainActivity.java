package puzzleleaf.tistory.com.englishstudy;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
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
    private Button itemAdd; //임시
    private Button addMenu;
    private EditText editText;
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
    View popupView;

    //data
    String data ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        engData = new ArrayList<>();

        MyDataList temp = new MyDataList("Hello","expressway|n.고속도로/" +
                "equip|v.(장비를)갖추어주다/" +
                "entire|a.전체의,전부의/" +
                "destination|n.행선의,도착의,목적지/" +
                "delay|n.지연,지체\nv.지연시키다/");
        engData.add(temp);

        temp = new MyDataList("Hello2","qeqweqweqweqweqweqw|n.고속도로/" +
                "equip|v.(장비를)갖추어주다/" +
                "entire|a.전체의,전부의/" +
                "destination|n.행선의,도착의,목적지/" +
                "delay|n.지연,지체\nv.지연시키다/");
        engData.add(temp);

        //팝업
        popupView = getLayoutInflater().inflate(R.layout.popup_add, null);
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        drawerInit(); //drawer 할당
        viewPagerInit(); // ViewPager 할당

        Button btn = (Button)findViewById(R.id.test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        //popupView 에서 (LinearLayout 을 사용) 레이아웃이 둘러싸고 있는 컨텐츠의 크기 만큼 팝업 크기를 지정
        mPopupWindow.setFocusable(true);
        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);


        menuAdd = (Button)popupView.findViewById(R.id.addMenuBtn);
        menuEdit = (EditText)popupView.findViewById(R.id.addMenuName);
        wordEdit = (EditText)popupView.findViewById(R.id.addWord);
        meanEdit = (EditText)popupView.findViewById(R.id.addMean);
        wordAdd = (Button)popupView.findViewById(R.id.addBtn);
        addClose = (Button)popupView.findViewById(R.id.addClose);



        //메뉴 이름 지정하기
        menuAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!menuEdit.getText().toString().equals(""))
                {
                    MyValue.saveTempMenu=menuEdit.getText().toString();
                    Toast.makeText(getApplicationContext(),MyValue.saveTempMenu,Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"값을 입력하세요",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(),MyValue.saveTempContent,Toast.LENGTH_LONG).show();
                    wordEdit.setText("");
                    meanEdit.setText("");
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"값을 모두 입력하세요",Toast.LENGTH_LONG).show();
                }
            }
        });

        //닫기버튼 -- 임시값 저장
        addClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyDataList temp = new MyDataList(MyValue.saveTempMenu,MyValue.saveTempContent);
                adapterAddItem(temp);
                MyValue.saveTempMenu ="";
                MyValue.saveTempContent="";
                if(mPopupWindow.isShowing())
                    mPopupWindow.dismiss();
                Toast.makeText(getApplicationContext(),"추가되었습니다.",Toast.LENGTH_LONG).show();
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

        addMenu = (Button)findViewById(R.id.addMenu);

        addMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupInit();
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
