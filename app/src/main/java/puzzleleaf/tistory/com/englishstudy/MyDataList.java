package puzzleleaf.tistory.com.englishstudy;

/**
 * Created by cmtyx on 2017-02-04.
 */

public class MyDataList {

    private String Day;
    private String Value;

    MyDataList(String v1, String v2){
        Day = v1;
        Value = v2;
    }

    public String getDay()
    {
        return Day;
    }

    public String getValue() {
        return Value;
    }
}
