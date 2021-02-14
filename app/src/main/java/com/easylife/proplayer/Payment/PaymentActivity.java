package com.easylife.proplayer.Payment;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.easylife.proplayer.BasicFunctions;
import com.easylife.proplayer.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    String customerId = "";
    String orderId = "";
    String mid = "ohqCBM01981723810929";
    String amount = "";
    DatabaseReference reference;
    BasicFunctions basicFunctions ;
    Dialog dialogLoading;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        basicFunctions=new BasicFunctions(this);

        dialogLoading=new Dialog(this);
        dialogLoading.setContentView(R.layout.loading_view);
        dialogLoading.setCancelable(false);
        ImageView imageView1 =dialogLoading.findViewById(R.id.loading_img);
        Glide.with(this).load(R.drawable.loading).into(imageView1);


        customerId = generateString();
        orderId = generateString();
        amount = getIntent().getStringExtra("amount");

        getCheckSum cs = new getCheckSum();
        cs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }



    public class getCheckSum extends AsyncTask<ArrayList<String>, Void, String> {

        JSONObject jsonObject;



        String url ="https://slushier-skin.000webhostapp.com/generateChecksum.php";
        //TODO your server's url here (www.xyz/checksumGenerate.php)
        String varifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
        String CHECKSUMHASH ="";

        @Override
        protected void onPreExecute() {

            dialogLoading.show();
        }

        protected String doInBackground(ArrayList<String>... alldata) {
            JsonParse jsonParser = new JsonParse(PaymentActivity.this);
            String param=
                    "MID="+mid+
                            "&ORDER_ID=" + orderId+
                            "&CUST_ID="+customerId+
                            "&CHANNEL_ID=WAP&TXN_AMOUNT="+amount+"&WEBSITE=DEFAULT"+
                            "&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";

            Log.e("PostData",param);

            jsonObject= jsonParser.makeHttpRequest(url,"POST",param);
            Log.e("CheckSum result >>",jsonObject.toString());
            if(jsonObject != null){
                Log.e("CheckSum result >>",jsonObject.toString());
                try {

                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.e("CheckSum result >>",CHECKSUMHASH);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }

        @Override
        protected void onPostExecute(String result) {





            Log.e(" setup acc ","  signup result  " + result);


            //  PaytmPGService Service = PaytmPGService.getStagingService();
            PaytmPGService Service = PaytmPGService.getProductionService();
            HashMap<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("MID", mid);
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", customerId);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", amount);
            paramMap.put("WEBSITE", "DEFAULT");
            paramMap.put("CALLBACK_URL" ,varifyurl);
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,null);
            Service.startPaymentTransaction(PaymentActivity.this, true,
                    true,
                    PaymentActivity.this  );

            if (dialogLoading.isShowing()) {

                dialogLoading.dismiss();

            }
        }
    }





    @Override
    public void onTransactionResponse(Bundle inResponse) {



        if(inResponse.getString("STATUS").equals("TXN_SUCCESS")){
            Toast.makeText(this, "Payment successful", Toast.LENGTH_LONG).show();
            dialogLoading.show();
            reference= FirebaseDatabase.getInstance().getReference("accounts").child(basicFunctions.read("id")).child("balance");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String b= dataSnapshot.getValue(String.class);
                    int ba= Integer.parseInt(b);
                    ba= ba+Integer.parseInt(amount);
                    String bal= String.valueOf(ba);
                    reference.setValue(bal).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (dialogLoading.isShowing()) {

                                dialogLoading.dismiss();

                            }
                            finish();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


        Log.e("xxxxxxxxxxxxxx",inResponse.toString());

    }

    @Override
    public void networkNotAvailable() {
        Log.e("Trans ", "Network Not Available" );
        Toast.makeText(this, "Network Not Available", Toast.LENGTH_LONG).show();
        finish();

    }

    @Override
    public void clientAuthenticationFailed(String s) {
        Log.e("Trans ", " Authentication Failed  "+ s );
        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("Trans ", " ui fail respon  "+ s );
        Toast.makeText(this, "UI Error Occurred", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("Trans ", " error loading pagerespon true "+ s + "  s1 " + s1);
        Toast.makeText(this, "onErrorLoadingWebPage", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(this, "Transaction Cancel", Toast.LENGTH_LONG).show();
        Log.e("Trans ", " cancel call back respon  " );
        finish();
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Toast.makeText(this, "Transaction Cancel", Toast.LENGTH_LONG).show();
        Log.e("Trans ", "  transaction cancel " );
        finish();

    }

    private String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

}
