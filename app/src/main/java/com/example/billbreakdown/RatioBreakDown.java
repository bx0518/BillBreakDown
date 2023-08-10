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

public class RatioBreakDown extends AppCompatActivity {

    int pax;
    double amount, totalRatio;
    double[] ratios;
    double[] amounts;
    EditText[] ratioInputs;
    EditText[] userInputs;
    Button btnCal;
    TableLayout ratioResultTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratio_break_down);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Custom Ratio");

        pax = getIntent().getIntExtra("pax", 0);
        amount = getIntent().getDoubleExtra("amount", 0.00);
        btnCal = findViewById(R.id.btnCalRatio);
        btnCal.setOnClickListener(v -> calRatio());
        ratioResultTable = findViewById(R.id.ratioResultTable);

        ratioInputs = new EditText[pax];
        userInputs = new EditText[pax];

        LinearLayout layout = findViewById(R.id.ratioLayout);
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

            // Create and add the editable ratio EditText
            EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editText.setHint("Ratio (1/2/0.xx/...)");
            ratioInputs[i] = editText;
            rowLayout.addView(editText);

            layout.addView(rowLayout);
        }
    }

    private void calRatio() {
        totalRatio = 0;
        double totalAmount = getIntent().getDoubleExtra("amount", 0.00);

        String[] userNames = new String[pax];
        ratios = new double[pax];
        amounts = new double[pax];
        boolean validateInput = true;

        for (int i = 0; i < pax; i++) {
            String ratioInput = ratioInputs[i].getText().toString().trim();
            String userInput = userInputs[i].getText().toString().trim();
            if (!ratioInput.isEmpty()) {
                double ratio = Double.parseDouble(ratioInput);
                ratios[i] = ratio;
                totalRatio += ratio;

                if(!userInput.isEmpty()){
                    userNames[i] = userInput;
                }

                double userAmount = (ratio / totalRatio) * totalAmount;
                amounts[i] = userAmount;
            }else {
                validateInput = false;
            }
        }
        
        if(validateInput){
            displayResultTable(userNames,ratios,totalRatio);
            Button btnShare = findViewById(R.id.btnShareRatio);
            btnShare.setOnClickListener(v -> shareResults());
            btnShare.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(this, "Please insert all the ratio", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayResultTable(String[] userNames, double[] ratios, double totalRatio){
        ratioResultTable.removeAllViews();

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
        ratioResultTable.addView(headerRow);

        for (int i = 0; i < userNames.length; i++) {
            TableRow tableRow = new TableRow(this);
            TextView userTV = new TextView(this);
            userTV.setText(userNames[i]);
            TextView ratioTV = new TextView(this);
            double[] percentages = new double[userNames.length];
            percentages[i] = ratios[i]/totalRatio*100;
            if (percentages[i] == (int) percentages[i]) {
                ratioTV.setText(String.format("%.0f%%", percentages[i]));
            } else {
                ratioTV.setText(String.format("%.2f%%", percentages[i]));
            }
            TextView amountTV = new TextView(this);
            amountTV.setText(String.format("%.2f", (ratios[i]/totalRatio *amount)));

            tableRow.addView(userTV);
            tableRow.addView(ratioTV);
            tableRow.addView(amountTV);

            ratioResultTable.addView(tableRow);
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
        sharedContent.append("BreakDown: by Ratio\n\n");

        // Add breakdown details
        for (int i = 0; i < userInputs.length; i++) {
            sharedContent.append(userInputs[i].getText().toString())
                    .append(" (")
                    .append(getFormattedRatio(ratios[i], totalRatio))
                    .append("): RM")
                    .append(String.format("%.2f", (ratios[i]/totalRatio*amount)))
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

    private String getFormattedRatio(double ratio, double totalRatio) {
        double percentage = (ratio / totalRatio) * 100;

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