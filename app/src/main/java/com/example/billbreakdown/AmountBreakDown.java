package com.example.billbreakdown;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AmountBreakDown extends AppCompatActivity {

    int pax;
    double amount;
    double[] percentages;
    double[] amounts;
    EditText[] userInputs;
    EditText[] amountInputs;
    Button btnCal;
    TableLayout amountResultTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_break_down);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Custom Amount");

        pax = getIntent().getIntExtra("pax", 0);
        amount = getIntent().getDoubleExtra("amount", 0.00);

        btnCal = findViewById(R.id.btnCalAmount);
        btnCal.setOnClickListener(v -> calAmount());

        amountResultTable = findViewById(R.id.amountResultTable);

        //initialize the array to store percentage ET inputs
        amountInputs = new EditText[pax];
        userInputs = new EditText[pax];

        LinearLayout layout = findViewById(R.id.amountLayout);
        for (int i = 0; i < pax; i++){
            //generate row for each pax
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            // Create and add the "User X" label
            EditText userLabel = new EditText(this);
            userLabel.setText("User " + (i + 1));
            userLabel.setWidth(500);
            userInputs[i] = userLabel;
            rowLayout.addView(userLabel);

            // Create and add the editable percentage EditText
            EditText amountET = new EditText(this);
            amountET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            amountET.setHint("Amount (0.00)");
            amountInputs[i] = amountET;
            rowLayout.addView(amountET);

            layout.addView(rowLayout);
        }
    }

    private void calAmount(){
        double totalAmount = amount;
        double validateAmount = 0;
        String[] userNames = new String[pax];
        percentages = new double[pax];
        amounts = new double[pax];
        boolean validateInput = true;

        for (int i = 0; i < pax; i++){
            String amtInput = amountInputs[i].getText().toString().trim();
            String userInput = userInputs[i].getText().toString().trim();
            if (!amtInput.isEmpty()){
                double amount = Double.parseDouble(amtInput);
                amounts[i] = amount;
                validateAmount += amount;

                if(!userInput.isEmpty()){
                    userNames[i] = userInput;
                }else {
                    validateInput =false;
                }

                double percentage = (amounts[i]/totalAmount)*100;
                percentages[i] = percentage;
            }else {
                validateInput = false;
            }
        }

        if (!validateInput){
            Toast.makeText(this, "Please do not leave blank for any information", Toast.LENGTH_SHORT).show();
        } else if (validateAmount < totalAmount) {
            Toast.makeText(this, "The amount still remaining: " + (totalAmount-validateAmount), Toast.LENGTH_SHORT).show();
        } else if (validateAmount > totalAmount) {
            Toast.makeText(this, "The amount already exceed: " + (validateAmount-totalAmount), Toast.LENGTH_SHORT).show();
        } else{
            displayResultTable(userNames,percentages, amounts);
            Button btnShare = findViewById(R.id.btnShareAmount);
            btnShare.setOnClickListener(v -> shareResults());
            btnShare.setVisibility(View.VISIBLE);
        }

    }

    private void displayResultTable(String[] userNames, double[] percentages, double[] amounts){
        amountResultTable.removeAllViews();

        //header row
        TableRow headerRow = new TableRow(this);
        TextView headerUser = new TextView(this);
        headerUser.setText("User");
        TextView headerPct = new TextView(this);
        headerPct.setText("Percentage");
        TextView headerAmount = new TextView(this);
        headerAmount.setText("Amount");
        headerRow.addView(headerUser);
        headerRow.addView(headerPct);
        headerRow.addView(headerAmount);
        amountResultTable.addView(headerRow);

        for (int i = 0; i < userNames.length; i++) {
            TableRow tableRow = new TableRow(this);
            TextView userTV = new TextView(this);
            userTV.setText(userNames[i]);
            TextView percentageTV = new TextView(this);
            if (percentages[i] == (int) percentages[i]) {
                percentageTV.setText(String.format("%.0f%%", percentages[i]));
            } else {
                percentageTV.setText(String.format("%.2f%%", percentages[i]));
            }
            TextView amountTV = new TextView(this);
            amountTV.setText(String.format("%.2f", amounts[i]));

            tableRow.addView(userTV);
            tableRow.addView(percentageTV);
            tableRow.addView(amountTV);

            amountResultTable.addView(tableRow);
        }
    }

    private void shareResults(){
        StringBuilder sharedContent = new StringBuilder();

        // Add date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mma", Locale.getDefault());
        String dateTime = dateFormat.format(new Date());
        sharedContent.append("Date & Time: ").append(dateTime).append("\n");

        // Add total amount and pax
        sharedContent.append("Total Amount: RM").append(String.format("%.2f", amount)).append("\n");
        sharedContent.append("Pax: ").append(pax).append("\n");

        // Add breakdown type
        sharedContent.append("BreakDown: by Custom Amount\n\n");

        // Add breakdown details
        for (int i = 0; i < userInputs.length; i++) {
            sharedContent.append(userInputs[i].getText().toString())
                    .append(" (")
                    .append(getFormattedRatio(percentages[i]))
                    .append("): RM")
                    .append(String.format("%.2f", amounts[i]))
                    .append("\n");
        }

        // Create a sharing Intent
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Bill Breakdown");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharedContent.toString());

        // Start the sharing activity
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private String getFormattedRatio(double percentage) {

        if (percentage == (int) percentage) {
            return String.format("%.0f%%", percentage);
        } else {
            return String.format("%.2f%%", percentage);
        }
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