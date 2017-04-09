package test4.payworks_final_fmz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.EnumSet;

import io.mpos.accessories.AccessoryFamily;
import io.mpos.accessories.parameters.AccessoryParameters;
import io.mpos.provider.ProviderMode;
import io.mpos.transactions.Transaction;
import io.mpos.transactions.parameters.TransactionParameters;
import io.mpos.ui.shared.MposUi;
import io.mpos.ui.shared.model.MposUiConfiguration;

public class MainActivity extends AppCompatActivity
{
    private final static String MERCHANT_ID = "cfec59ae-a059-4ae7-b9e9-4c897836038c";
    private final static String MERCHANT_SECRET = "oTOLU6Zqa5Op4Ajl9uss9UFYnvjarOGy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Button payButton = (Button) findViewById(R.id.pay);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentButtonClicked();
            }
        });

        Button refundButton = (Button) findViewById(R.id.refund);
        refundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refundButtonClicked();
            }
        });
    }

    void paymentButtonClicked() {
        MposUi ui = MposUi.initialize(this, ProviderMode.MOCK, MainActivity.MERCHANT_ID, MainActivity.MERCHANT_SECRET);

        // Start with a mocked card reader:
        AccessoryParameters accessoryParameters = new AccessoryParameters.Builder(AccessoryFamily.MOCK)
                .mocked()
                .build();
        ui.getConfiguration().setTerminalParameters(accessoryParameters);

        TransactionParameters transactionParameters = new TransactionParameters.Builder()
                .charge(
                        new BigDecimal(
                                ((EditText) (findViewById(R.id.money))).getText().toString()
                        ),
                        io.mpos.transactions.Currency.EUR
                )
                .subject("Bouquet of Flowers")
                .customIdentifier("abc-123")
                .build();

        Intent intent = ui.createTransactionIntent(transactionParameters);
        startActivityForResult(intent, MposUi.REQUEST_CODE_PAYMENT);
    }

    void refundButtonClicked() {
        TransactionParameters parameters = new TransactionParameters.Builder()
                .refund("abc-123")
                .subject("Bouquet of Flowers")
                .amountAndCurrency(
                        new BigDecimal(
                                ((EditText) (findViewById(R.id.money))).getText().toString()
                        ),
                        io.mpos.transactions.Currency.EUR)
                .build();
        Intent intent = MposUi.getInitializedInstance().createTransactionIntent(parameters);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MposUi.REQUEST_CODE_PAYMENT) {
            if (resultCode == MposUi.RESULT_CODE_APPROVED) {
                findViewById(R.id.refund).setVisibility(View.VISIBLE);
                findViewById(R.id.pay).setVisibility(View.GONE);
                findViewById(R.id.money).setVisibility(View.GONE);
                findViewById(R.id.AMOUNT).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.refund).setVisibility(View.GONE);
            findViewById(R.id.pay).setVisibility(View.VISIBLE);
            findViewById(R.id.money).setVisibility(View.VISIBLE);
            findViewById(R.id.AMOUNT).setVisibility(View.VISIBLE);
        }
    }
}