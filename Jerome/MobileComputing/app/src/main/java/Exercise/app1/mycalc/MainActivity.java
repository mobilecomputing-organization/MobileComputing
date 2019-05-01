package Exercise.app1.mycalc;

import android.nfc.Tag;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    final String TAG = MainActivity.class.getCanonicalName();

    enum Operations{
        PLUS,MINUS,DIV,MUL,NONE;
    }

    double operand1, operand2 = 0;

    Operations operator = Operations.NONE;

    Boolean InOperator,DBZ = false;

    double CalcResult = 0.0;

    public void Calculate (String Num,String op)
    {
        if(op.indexOf("+") >= 0)
            CalcResult = CalcResult + Double.parseDouble(Num);
        else if (op.indexOf("-") >= 0)
            CalcResult = CalcResult - Double.parseDouble(Num);
        else if (op.indexOf("x") >= 0)
            CalcResult = CalcResult * Double.parseDouble(Num);
        else if (op.indexOf("/") >= 0)
        {
            if(Double.parseDouble(Num) == 0)
                DBZ = true;
            else
                CalcResult = CalcResult / Double.parseDouble(Num);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* For numerical Buttons*/
        Button onebut = (Button) findViewById(R.id.onebut);
        Button twobut = (Button) findViewById(R.id.twobut);
        Button threebut = (Button) findViewById(R.id.threebut);
        Button fourbut = (Button) findViewById(R.id.fourbut);
        Button fivebut = (Button) findViewById(R.id.fivebut);
        Button sixbut = (Button) findViewById(R.id.sixbut);
        Button sevenbut = (Button) findViewById(R.id.sevenbut);
        Button eightbut = (Button) findViewById(R.id.eightbut);
        Button ninebut = (Button) findViewById(R.id.ninebut);
        Button zerobut = (Button) findViewById(R.id.zerobut);
        /* For Special Buttons*/
        Button dotbut = (Button) findViewById(R.id.dotbut);
        Button plusbut = (Button) findViewById(R.id.plusbut);
        Button minusbut = (Button) findViewById(R.id.minusbut);
        Button divbut = (Button) findViewById(R.id.divbut);
        Button mulbut = (Button) findViewById(R.id.mulbut);
        Button clearbut = (Button) findViewById(R.id.clearbut);

        Button equalsbut = (Button) findViewById(R.id.equalsbut);

        final TextView result = (TextView) findViewById(R.id.result);


        onebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevText = result.getText().toString();
                result.setText(prevText + "1");
                InOperator = false;
            }
        });
        twobut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevText = result.getText().toString();
                result.setText(prevText + "2");
                InOperator = false;
            }
        });
        threebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevText = result.getText().toString();
                result.setText(prevText + "3");
                InOperator = false;
            }
        });
        fourbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevText = result.getText().toString();
                result.setText(prevText + "4");
                InOperator = false;
            }
        });
        fivebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevText = result.getText().toString();
                result.setText(prevText + "5");
                InOperator = false;
            }
        });
        sixbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevText = result.getText().toString();
                result.setText(prevText + "6");
                InOperator = false;
            }
        });
        sevenbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevText = result.getText().toString();
                result.setText(prevText + "7");
                InOperator = false;
            }
        });
        eightbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevText = result.getText().toString();
                result.setText(prevText + "8");
                InOperator = false;
            }
        });
        ninebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevText = result.getText().toString();
                result.setText(prevText + "9");
                InOperator = false;
            }
        });
        zerobut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevText = result.getText().toString();
                result.setText(prevText + "0");
                InOperator = false;
            }
        });
        dotbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevText = result.getText().toString();

                //int dotpresent = 0;
                //dotpresent = prevText.indexOf(".");

                //if (dotpresent == -1)
                //{
                    if(prevText == "")
                        result.setText(prevText + "0.");
                    else
                        result.setText(prevText + ".");
                //}
                //else
                //{
                //    Toast toast = Toast.makeText(v.getContext(),"You cannot use Two decimal pointers",Toast.LENGTH_SHORT);
                //    toast.show();
                //}
                InOperator = false;
            }
        });

        clearbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText("");
                operand1 = 0;
                operand2 = 0;
                operator = Operations.NONE;
                InOperator = false;
                // reset the flags here
            }
        });

        plusbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevText = result.getText().toString();
                if(prevText == "")
                {
                    // do nothing
                }
                else if(InOperator == false)
                    result.setText(prevText + " + ");
                else
                {
                    String SS = prevText.substring(0,prevText.length()-3);
                    result.setText(SS + " + ");
                }
                InOperator = true;

            }
        });
        minusbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevText = result.getText().toString();
                if(prevText == "")
                    result.setText(prevText + "0 - ");
                else if(InOperator == false)
                    result.setText(prevText + " - ");
                else
                {
                    String SS = prevText.substring(0,prevText.length()-3);
                    result.setText(SS + " - ");
                }
                InOperator = true;
            }
        });
        divbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevText = result.getText().toString();
                if(prevText == "")
                {
                    Toast toast = Toast.makeText(v.getContext(),"Please type a Number",Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(InOperator == false)
                    result.setText(prevText + " / ");
                else
                {
                    String SS = prevText.substring(0,prevText.length()-3);
                    result.setText(SS + " / ");
                }
                InOperator = true;
            }
        });
        mulbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevText = result.getText().toString();
                if(prevText == "")
                {
                    Toast toast = Toast.makeText(v.getContext(),"Please type a Number",Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(InOperator == false)
                    result.setText(prevText + " x ");
                else
                {
                    String SS = prevText.substring(0,prevText.length()-3);
                    result.setText(SS + " x ");
                }
                InOperator = true;
            }
        });

        equalsbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalcResult =0.0;
                String prevText = result.getText().toString();
                if(prevText == "")
                {
                    Toast toast = Toast.makeText(v.getContext(),"Please type a Number",Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    try{
                        int length = prevText.length();
                        int Firstspace = prevText.indexOf(" ",0);// index of the first space
                        String Firstopout = prevText.substring(0,Firstspace);

                        Calculate(Firstopout,"+");

                        for (int i=Firstspace;i < length;)
                        {
                            String operation = prevText.substring(i+1,i+2);

                            int Firstspaceindex = prevText.indexOf(" ",i+3);// index of the first space
                            if(Firstspaceindex != -1)
                            {
                                String Firstop = prevText.substring(i+3,Firstspaceindex);
                                //String operation = prevText.substring(Firstspaceindex+1,Firstspaceindex+2);
                                Log.i(TAG,Firstop + " " + operation);
                                i=Firstspaceindex;
                                Calculate(Firstop,operation);
                            }
                            else
                            {
                                String Lastoperator = prevText.substring(i+3);
                                Log.i(TAG,Lastoperator);
                                i=(i+3)+Lastoperator.length();
                                Calculate(Lastoperator,operation);
                            }
                            if(DBZ == true)
                            {
                                CalcResult = 0;
                                Toast toast = Toast.makeText(v.getContext(),"Divide By Zero Error",Toast.LENGTH_SHORT);
                                toast.show();
                                DBZ = false;
                                break;
                            }

                        }

                        result.setText(Double.toString(CalcResult));


                    }
                    catch (Exception ex)
                    {
                        Toast toast = Toast.makeText(v.getContext(),"Input not valid. Please Clear and try again",Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }


            }






        });









    }
}
