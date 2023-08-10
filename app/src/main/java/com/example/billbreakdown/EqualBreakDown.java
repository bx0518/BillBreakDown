package com.example.billbreakdown;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EqualBreakDown extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equal_break_down);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Break-Down Equally");

        //get data from MainActivity
        double amount = getIntent().getDoubleExtra("amount", 0);
        int pax = getIntent().getIntExtra("pax", 0);

        double amountPerPerson = amount / pax;

        TextView infoTV = findViewById(R.id.infoTV);
        infoTV.setText("Total Bill Amount: " + amount + "  Pax: " + pax);

        //create table
        TableLayout tableLayout = findViewById(R.id.table_layout);
        //header row
        TableRow headerRow = new TableRow(this);
        TextView headerUser = new TextView(this);
        headerUser.setText("User");
        TextView headerAmount = new TextView(this);
        headerAmount.setText("Amount");
        headerRow.addView(headerUser);
        headerRow.addView(headerAmount);
        tableLayout.addView(headerRow);

        //generate pax number rows
        for (int i = 1; i <= pax; i++) {
            TableRow row = new TableRow(this);
            EditText userEditText = new EditText(this);
            userEditText.setText("user" + i);
            userEditText.setWidth(500);
            TextView amountTextView = new TextView(this);
            amountTextView.setText(String.format("%.2f", amountPerPerson));
            row.addView(userEditText);
            row.addView(amountTextView);
            tableLayout.addView(row);
        }

        Button btnShare = findViewById(R.id.btnShareEqual);
        btnShare.setOnClickListener(v -> shareResults());
        btnShare.setVisibility(View.VISIBLE);
    }

    private void shareResults(){
        StringBuilder sharedContent = new StringBuilder();

        double amount = getIntent().getDoubleExtra("amount", 0);
        int pax = getIntent().getIntExtra("pax", 0);
        double amountPerPerson = amount / pax;


        // Add date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mma", Locale.getDefault());
        String dateTime = dateFormat.format(new Date());
        sharedContent.append("Date & Time: ").append(dateTime).append("\n");

        // Add total amount and pax
        sharedContent.append("Total Amount: RM").append(String.format("%.2f", amount)).append("\n");
        sharedContent.append("Pax: ").append(pax).append("\n");

        // Add breakdown type
        sharedContent.append("BreakDown: by Ratio\n\n");

        // Add breakdown details
            sharedContent.append("Each pax RM" + String.format("%.2f", amountPerPerson));

        // Create a sharing Intent
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Bill Breakdown");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharedContent.toString());

        // Start the sharing activity
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle back button click
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}