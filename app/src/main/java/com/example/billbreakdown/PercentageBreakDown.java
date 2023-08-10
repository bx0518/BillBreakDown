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

public class PercentageBreakDown extends AppCompatActivity {

    int pax;
    double amount;
    String[] userNames;
    double[] percentages;
    double[] amounts;
    EditText[] percentageInputs;
    EditText[] userInputs;
    Button btnCal;
    TableLayout pctResultTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percentage_break_down);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Custom Percentage");

        pax = getIntent().getIntExtra("pax", 0);
        amount = getIntent().getDoubleExtra("amount", 0.00);

        btnCal = findViewById(R.id.btnCalPct);
        btnCal.setOnClickListener(v -> calPercentages());

        pctResultTable = findViewById(R.id.pctResultTable);

        //initialize the array to store percentage ET inputs
        percentageInputs = new EditText[pax];
        userInputs = new EditText[pax];

        LinearLayout layout = findViewById(R.id.percentageLayout);
        for (int i = 0; i < pax; i++){
            //generate row for each pax
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            EditText userLabel = new EditText(this);
            userLabel.setText("User " + (i + 1));
            userLabel.setWidth(500);
            userInputs[i] = userLabel;
            rowLayout.addView(userLabel);

            EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editText.setHint("Percentage");
            percentageInputs[i] = editText;
            rowLayout.addView(editText);

            layout.addView(rowLayout);
        }
    }

    private void calPercentages() {
        double totalPercentage = 0;
        double totalAmount = getIntent().getDoubleExtra("amount", 0.00);

        userNames = new String[pax];
        percentages = new double[pax];
        amounts = new double[pax];

        for (int i = 0; i < pax; i++) {
            String pctInput = percentageInputs[i].getText().toString().trim();
            String userInput = userInputs[i].getText().toString().trim();
            if (!pctInput.isEmpty()) {
                double percentage = Double.parseDouble(pctInput);
                percentages[i] = percentage;
                totalPercentage += percentage;

                if(!userInput.isEmpty()){
                    userNames[i] = userInput;
                }

                double userAmount = (percentage / 100) * totalAmount;
                amounts[i] = userAmount;
            }
        }

        if (totalPercentage == 100) {
            displayResultTable(userNames, percentages, amounts);
            Button btnShare = findViewById(R.id.btnSharePct);
            btnShare.setOnClickListener(v -> shareResults());
            btnShare.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Total percentage must be 100%", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayResultTable(String[] userNames, double[] percentages, double[] amounts){
        pctResultTable.removeAllViews();

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
        pctResultTable.addView(headerRow);

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
            amountTV.setText(String.format("%.2f", (percentages[i]/100*amount)));

            tableRow.addView(userTV);
            tableRow.addView(percentageTV);
            tableRow.addView(amountTV);

            pctResultTable.addView(tableRow);
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
        sharedContent.append("BreakDown: by Percentage\n\n");

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