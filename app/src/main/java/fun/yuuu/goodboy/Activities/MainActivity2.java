package fun.yuuu.goodboy.Activities;

import androidx.appcompat.app.AppCompatActivity;
import fun.yuuu.goodboy.R;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import fun.yuuu.goodboy.Utils.DatabaseHelper;

public class MainActivity2 extends AppCompatActivity {
    private SQLiteDatabase mSQLiteDatabase = null;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private TextView mtextViewWorkTimeInfo = null;
    private int deleteFlag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mSQLiteDatabase = new DatabaseHelper(MainActivity2.this,"GoodBoy_db",null,1).getWritableDatabase();
        Button btnStartWork = findViewById(R.id.btn_start_work);
        Button btnEndWork = findViewById(R.id.btn_end_work);
        Button btnDeleteAll = findViewById(R.id.btn_delete_all);
        mtextViewWorkTimeInfo = findViewById(R.id.textview_work_time_info);
        mtextViewWorkTimeInfo.setText(Html.fromHtml(getAllInfo()));
        btnStartWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Cursor cursor = mSQLiteDatabase.rawQuery(
                        "select * from   clock_in_work  where   t_date=? ",
                        new String[] { mSimpleDateFormat.format(new Date()) });
                while (cursor.moveToNext()) {
                    cursor.close();
                    mtextViewWorkTimeInfo.setText(Html.fromHtml(getAllInfo()+"<br/><font color=\"#FF0000\">[START TIME ALREADY EXISTS!]</font>"));
                    return;
                }
                ContentValues values = new ContentValues();
                values.put("t_date",mSimpleDateFormat.format(new Date()));
                values.put("t_start_time",new SimpleDateFormat("HH:mm:ss").format(new Date()));
                values.put("t_end_time","");
                mSQLiteDatabase.insert("clock_in_work",null,values);
                mtextViewWorkTimeInfo.setText(Html.fromHtml(getAllInfo()+"<br/><font color=\"#33FF00\">[START TIME UPDATE SUCCESSFUL!]</font>"));

            }
        });
        btnEndWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                String currenTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
                values.put("t_end_time",currenTime);
                mSQLiteDatabase.update("clock_in_work",values,"t_date=?",new String[]{mSimpleDateFormat.format(new Date())});
                mtextViewWorkTimeInfo.setText(Html.fromHtml(getAllInfo()+"<br/><font color=\"#33FF00\">[END TIME UPDATE SUCCESSFUL!]</font>"));
            }
        });
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String notifyString = null;
                if(deleteFlag<9){
                    deleteFlag++;
                    mtextViewWorkTimeInfo.setText(Html.fromHtml(getAllInfo()+"<br/><font color=\"#FF0000\">[YOU WILL DELETE ALL DATA !!!]<br/>[IF YOU WANT DO IT , PLEASE CLICK </font><font color=\"#33FF00\">"+(10-deleteFlag)+"</font><font color=\"#FF0000\"> TIMES!!]</font>"));
                    return;
                }
                mSQLiteDatabase.execSQL("delete from clock_in_work");
                mtextViewWorkTimeInfo.setText(Html.fromHtml("<font color=\"#33FF00\">[DELETE ALL DATA SUCCESSFUL!]</font>"));
                deleteFlag = 0;
            }
        });

    }

    private String getAllInfo(){
        //创建游标对象
        Cursor cursor = mSQLiteDatabase.query("clock_in_work", new String[]{"t_date","t_start_time","t_end_time"}, null, null, null, null, null);
        //利用游标遍历所有数据对象
        //为了显示全部，把所有对象连接起来，放到TextView中
        StringBuffer sb = new StringBuffer("<font color=\"#00FFFF\">[START]</font><br/>");
        while(cursor.moveToNext()){
            String res_date = cursor.getString(cursor.getColumnIndex("t_date"));
            String res_start_time = cursor.getString(cursor.getColumnIndex("t_start_time"));
            String res_end_time = cursor.getString(cursor.getColumnIndex("t_end_time"));
            sb.append("["+res_date+"]: ["+res_start_time+"] -> ["+res_end_time+"]<br/>");
        }
        sb.append("<font color=\"#00FFFF\">[END]</font>");
        // 关闭游标，释放资源
        cursor.close();
        return sb.toString();
    }
}