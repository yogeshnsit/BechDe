package com.olx.bechde;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Sushil on 26-09-2015.
 */
public class DetailsActivity extends Activity {

    TextView tv1,tv2,tv3;
    EditText et1, et2, et3;
    Button b1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_details);

        et1=(EditText)findViewById(R.id.etName);
        et2=(EditText)findViewById(R.id.etphone);
        et3=(EditText)findViewById(R.id.etemail);

        b1=(Button)findViewById(R.id.buttonValidate);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=(String)et1.getText().toString();
                String phone=(String)et2.getText().toString();
                String email=(String)et3.getText().toString();

                if(name==null || phone==null || email==null)
                    Toast.makeText(DetailsActivity.this,"Please enter all details",Toast.LENGTH_SHORT).show();
                else{
                    Intent intent=new Intent();
                    intent.putExtra("Name",name);
                    intent.putExtra("Phone",phone);
                    intent.putExtra("Email",email);

                    setResult(Activity.RESULT_OK,intent);
                }



            }
        });




    }
}
