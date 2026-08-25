package com.example.smartpantry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;

import java.util.Calendar;

/**
 * AddEditIngredientActivity
 * Form to add a new pantry item or edit an existing one.
 * Validates all inputs before saving to SQLite.
 * Receives pantry_id via Intent extras when editing.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etExpiry;
    private Spinner spinnerUnit;
    private Button btnSave, btnPickDate;
    private DatabaseHelper dbHelper;
    private int pantryId = -1; // -1 means ADD mode

    private final String[] UNITS = {"piece", "g", "kg", "ml", "l", "cup", "tbsp", "tsp", "slice", "clove", "can"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dbHelper = new DatabaseHelper(this);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        etExpiry = findViewById(R.id.etExpiry);
        btnSave = findViewById(R.id.btnSave);
        btnPickDate = findViewById(R.id.btnPickDate);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, UNITS);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        // Check if editing existing item
        if (getIntent().hasExtra("pantry_id")) {
            pantryId = getIntent().getIntExtra("pantry_id", -1);
            setTitle("Edit Ingredient");
            loadExistingItem(pantryId);
        } else {
            setTitle("Add Ingredient");
        }

        btnPickDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveItem();
            }
        });
    }

    private void loadExistingItem(int id) {
        PantryItem item = dbHelper.getPantryItem(id);
        if (item != null) {
            etName.setText(item.getName());
            etQuantity.setText(String.valueOf(item.getQuantity()));
            etExpiry.setText(item.getExpiryDate());
            // Set spinner selection
            for (int i = 0; i < UNITS.length; i++) {
                if (UNITS[i].equalsIgnoreCase(item.getUnit())) {
                    spinnerUnit.setSelection(i);
                    break;
                }
            }
        }
    }

    private boolean validateInput() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Ingredient name is required");
            return false;
        }
        if (qtyStr.isEmpty()) {
            etQuantity.setError("Quantity is required");
            return false;
        }
        try {
            double qty = Double.parseDouble(qtyStr);
            if (qty <= 0) {
                etQuantity.setError("Quantity must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid number");
            return false;
        }
        return true;
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        double quantity = Double.parseDouble(etQuantity.getText().toString().trim());
        String unit = spinnerUnit.getSelectedItem().toString();
        String expiry = etExpiry.getText().toString().trim();

        PantryItem item = new PantryItem();
        item.setName(name);
        item.setQuantity(quantity);
        item.setUnit(unit);
        item.setExpiryDate(expiry);

        if (pantryId == -1) {
            dbHelper.addPantryItem(item);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            item.setId(pantryId);
            dbHelper.updatePantryItem(item);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish(); // Return to PantryListActivity
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etExpiry.setText(date);
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
}
