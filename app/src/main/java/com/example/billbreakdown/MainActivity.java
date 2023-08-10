package com.example.billbreakdown;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private EditText amountInput, paxInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setTitle("Bill Break-Down");

        amountInput = findViewById(R.id.amount);
        paxInput = findViewById(R.id.pax);

        Button btnEqual = findViewById(R.id.btnEqual);
        Button btnPercentage = findViewById(R.id.btnPercentage);
        Button btnRatio = findViewById(R.id.btnRatio);
        Button btnAmount = findViewById(R.id.btnAmount);

        btnEqual.setOnClickListener(v -> {
            if (validateInput())
            {
                double amount = Double.parseDouble(amountInput.getText().toString());
                int pax = Integer.parseInt(paxInput.getText().toString());

                Intent intent = new Intent(MainActivity.this, EqualBreakDown.class);
                intent.putExtra("amount", amount);
                intent.putExtra("pax", pax);
                startActivity(intent);
            }
        });

        btnPercentage.setOnClickListener(v -> {
            if (validateInput())
            {
                double amount = Double.parseDouble(amountInput.getText().toString());
                int pax = Integer.parseInt(paxInput.getText().toString());

                Intent intent = new Intent(MainActivity.this, PercentageBreakDown.class);
                intent.putExtra("amount", amount);
                intent.putExtra("pax", pax);
                startActivity(intent);
            }
        });

        btnRatio.setOnClickListener(v -> {
            if (validateInput())
            {
                double amount = Double.parseDouble(amountInput.getText().toString());
                int pax = Integer.parseInt(paxInput.getText().toString());

                Intent intent = new Intent(MainActivity.this, RatioBreakDown.class);
                intent.putExtra("amount", amount);
                intent.putExtra("pax", pax);
                startActivity(intent);
            }
        });

        btnAmount.setOnClickListener(v -> {
            if (validateInput())
            {
                double amount = Double.parseDouble(amountInput.getText().toString());
                int pax = Integer.parseInt(paxInput.getText().toString());

                Intent intent = new Intent(MainActivity.this, AmountBreakDown.class);
                intent.putExtra("amount", amount);
                intent.putExtra("pax", pax);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private boolean validateInput()
    {
        String amountStr = amountInput.getText().toString();
        String paxStr = paxInput.getText().toString();

        if (amountStr.isEmpty() || paxStr.isEmpty())
        {
            Toast.makeText(this,"Please enter the value", Toast.LENGTH_SHORT).show();
            return false;
        }

        double amount = Double.parseDouble(amountStr);
        int pax = Integer.parseInt(paxStr);

        if(!String.format("%.2f", amount).equals(amountStr))
        {
            Toast.makeText(this,"Please enter amount with 2 decimal places (0.xx)", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(amount <= 0 || pax < 2)
        {
            Toast.makeText(this, "Please enter amount and 2 or above pax number", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
