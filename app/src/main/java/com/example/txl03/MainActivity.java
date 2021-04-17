package com.example.txl03;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private List<Person> list;
    ArrayAdapter<String> adapter;

    List<String> contactsList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.contacts_view);
        //获取到listview并且设置适配器
        ListView contactsView= (ListView) findViewById(R.id.contacts_view);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,contactsList);
        contactsView.setAdapter(adapter);

        //判断是否开启读取通讯录的权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager
                .PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
        }else {
            readContacts();
        }
    }

    private void readContacts() {
        list = new ArrayList<>();
        Cursor cursor=null;
        try {
            //查询联系人数据,使用了getContentResolver().query方法来查询系统的联系人的数据
            //CONTENT_URI就是一个封装好的Uri，是已经解析过得常量
            cursor=getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            //对cursor进行遍历，取出姓名和电话号码
            if (cursor!=null){
                while (cursor.moveToNext()){
                    //获取联系人姓名
                    String displayName=cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    ));
                    //获取联系人手机号
                    String number=cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ));
//                    把取出的两类数据进行拼接，然后添加到listview中
//                    contactsList.add(displayName+"\n"+number);
//                    把取出的两类数据进行拼接，然后添加到list数组中
                    list.add(new Person(displayName,number));
                }
                //刷新listview
                adapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //记得关掉cursor
            if (cursor!=null){
                cursor.close();
            }
        }
//      使用自定义适配器
        final CustomAdapter adapter = new CustomAdapter(this,list);
        listView.setAdapter(adapter);
        //设置监听器，当点击联系人列表的每一行时，显示联系人的详细信息
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //自定义对话框，显示联系人详情
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view1 = View.inflate(MainActivity.this,R.layout.activity_contact_detail2,null);
                //为对话框设置自定义的视图
                builder.setView(view1);
                //绑定控件
                ImageView imageView = view1.findViewById(R.id.imageView);
                final EditText editText1 = view1.findViewById(R.id.editText1);
                final EditText editText2 = view1.findViewById(R.id.editText2);
                editText1 .setText((String) list.get(position).getName());
                editText2.setText((String) list.get(position).getPhone());
                //添加按钮
                builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list.get(position).setName(editText1.getText().toString());
                        list.get(position).setPhone(editText2.getText().toString());
                        //更新数据
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this,"修改信息成功！",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("返回",null);
                //显示对话框
                builder.create().show();
            }
        });
        //当长时间点击时，弹出对话框，提示是否要删除联系人
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MainActivity.this).setTitle("提示")
                        .setMessage("你确定要删除 "+list.get(position).getName()+" 吗！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //将要删除的联系人的信息从list中移除
                                list.remove(position);
                                //更新数据
                                adapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this,"删除成功！",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消",null).create().show();
                return true;
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    readContacts();
                }else {
                    Toast.makeText(this,"没有权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}