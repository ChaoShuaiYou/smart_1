package com.henshin.smart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class ShowInf extends AppCompatActivity
{
    private Intent intent;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showoldinf);
        SqlHelper sqlHelper = new SqlHelper(this);
        final List<InfModel> modellest = sqlHelper.getAll("","");
        ListView listView = findViewById(R.id.show);
        InfAdapter infAdapter = new InfAdapter(ShowInf.this,R.layout.item,modellest);
        intent = new Intent(this,ShowSingleInf.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InfModel infModel = modellest.get(position);
                //Toast.makeText(ShowInf.this, infModel.getId(),Toast.LENGTH_SHORT).show();
                intent.putExtra("id",infModel.getId()+"");
                startActivity(intent);
            }
        });
        listView.setAdapter(infAdapter);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent myIntent = new Intent(ShowInf.this, MainActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
