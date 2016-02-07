package tipbox.com.tipbox;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import java.math.BigDecimal;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    RelativeLayout whole_layout;
    LinearLayout controls_layout, tip_percent_layout, tip_total_layout, persons_layout, per_person_layout;
    RelativeLayout amount_due_layout, total_layout;
    EditText amount_due_edit, total_edit, tip_edit, tip_total_edit, persons_edit, per_person_edit;
    Toolbar toolbar;
    int toolbarHeight, rootHeight, currentEdit;
    int lastHeight = 0;
    public InputMethodManager imm;
    int savedHeight, lastEdit = 0;
    SPHelper spHelper;
    Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        whole_layout = (RelativeLayout) findViewById(R.id.whole_layout);
        amount_due_layout = (RelativeLayout) findViewById(R.id.amount_due_layout);
        total_layout = (RelativeLayout) findViewById(R.id.total_layout);
        controls_layout = (LinearLayout) findViewById(R.id.controls_layout);
        tip_percent_layout = (LinearLayout) findViewById(R.id.tip_percent_layout);
        tip_total_layout = (LinearLayout) findViewById(R.id.tip_total_layout);
        persons_layout = (LinearLayout) findViewById(R.id.persons_layout);
        per_person_layout = (LinearLayout) findViewById(R.id.per_person_layout);

        amount_due_edit = (EditText) findViewById(R.id.amount_due_edit);
        total_edit = (EditText) findViewById(R.id.total_amount);
        tip_edit = (EditText) findViewById(R.id.tip_edit);
        tip_total_edit = (EditText) findViewById(R.id.tip_total_edit);
        persons_edit = (EditText) findViewById(R.id.persons_edit);
        per_person_edit = (EditText) findViewById(R.id.per_person_edit);

        currentEdit = 1;

        amount_due_edit.addTextChangedListener(textWatcher);
        amount_due_edit.requestFocus();

        amount_due_layout.setOnClickListener(amountDueOnClickListener);
        total_layout.setOnClickListener(totalAmountOnClickListener);
        tip_percent_layout.setOnClickListener(tipPercentOnClickListener);
        tip_total_layout.setOnClickListener(tipTotalOnClickListener);
        persons_layout.setOnClickListener(personsOnClickListener);
        per_person_layout.setOnClickListener(perPersonOnClickListener);

        spHelper = new SPHelper(this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        setUpToolbar();

        final ViewGroup.LayoutParams params = controls_layout.getLayoutParams();
        setTipLayoutHeight(params);

        result = new Result();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_default_tip:

                return true;

            case R.id.action_currency:

                return true;

            case R.id.action_clear:
                result = new Result();
                clearEdits();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarHeight = toolbar.getLayoutParams().height;
    }

    public void setTipLayoutAboveKeyboard(final ViewGroup.LayoutParams params) {
        final ViewTreeObserver observer = whole_layout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Log.d(TAG, "Height: " + whole_layout.getHeight());
                        rootHeight = whole_layout.getHeight();

                        if (!(rootHeight > lastHeight)) {
                            params.height = rootHeight - toolbarHeight;
                            spHelper.setSavedHeight(params.height);
                        }
                        lastHeight = rootHeight;
                    }
                });
    }

    public void setTipLayoutHeight(final ViewGroup.LayoutParams params) {
        savedHeight = spHelper.getHeight();
        Log.d(TAG, "saved Height " + savedHeight);
        if(savedHeight == 0 ) {
            setTipLayoutAboveKeyboard(params);
        } else {
            params.height = savedHeight;
        }
    }

    //<editor-fold desc="OnClickListeners">

    private View.OnClickListener amountDueOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            lastEdit = currentEdit;
            removeTextWatcherFor(lastEdit);
            currentEdit = 1;
            amount_due_edit.addTextChangedListener(textWatcher);
            setUpEditText(amount_due_edit, 0);
        }
    };

    private View.OnClickListener totalAmountOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            lastEdit = currentEdit;
            removeTextWatcherFor(lastEdit);
            currentEdit = 4;
            total_edit.addTextChangedListener(textWatcher);
            setUpEditText(total_edit, 0);
        }
    };

    private View.OnClickListener tipPercentOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            lastEdit = currentEdit;
            removeTextWatcherFor(lastEdit);
            currentEdit = 2;
            tip_edit.addTextChangedListener(textWatcher);
            setUpEditText(tip_edit, 1);
        }
    };

    private View.OnClickListener tipTotalOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            lastEdit = currentEdit;
            removeTextWatcherFor(lastEdit);
            currentEdit = 3;
            tip_total_edit.addTextChangedListener(textWatcher);
            setUpEditText(tip_total_edit, 0);
        }
    };

    private View.OnClickListener personsOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            lastEdit = currentEdit;
            removeTextWatcherFor(lastEdit);
            currentEdit = 5;
            persons_edit.addTextChangedListener(textWatcher);
            setUpEditText(persons_edit, 0);
            if(persons_edit.getText().length() < 1){
                persons_edit.setHint("");
            }
        }
    };

    private View.OnClickListener perPersonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            lastEdit = currentEdit;
            removeTextWatcherFor(lastEdit);
            currentEdit = 6;
            per_person_edit.addTextChangedListener(textWatcher);
            setUpEditText(per_person_edit, 0);
        }
    };

    //</editor-fold>

    
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            onTextchangedFor(currentEdit);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //checks if the user deletes the currency or percentage sign and adds it back
    public void doSignBehavior(EditText editText, String sign, int offset) {
        if(editText.getText().length() < 1) {
            editText.setText(sign);
            editText.setSelection(editText.getText().length() - offset);
        }
        checkForDots(editText, offset);
    }

    public void checkForDots(EditText editText, int offset) {
        int numbersOfDots = 0;
        String input = String.valueOf(editText.getText());
        int index = input.indexOf(".");

        while (index >= 0) {
            index = input.indexOf(".", index + 1);
            numbersOfDots++;

        }

        if(numbersOfDots >= 1) {
            int centDigit = input.length() - input.lastIndexOf(".") - offset;
            if(centDigit >= 3) {
                editText.setKeyListener(DigitsKeyListener.getInstance(""));
            } else {
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            }
        } else {
            if(input.length() >= 5) {
                editText.setKeyListener(DigitsKeyListener.getInstance(".")); //allow only 5 digits
            } else {
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
            }
        }
    }

    //when an edit text is clicked right
    public void setUpEditText(EditText editText, int offset) {
        editText.requestFocus();
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        editText.setSelection(editText.getText().length() - offset);
        if(persons_edit.getText().length() < 1) {
            persons_edit.setHint("1");
        }
        checkForDots(editText, offset);
    }

    public BigDecimal inputToDecimal(String input) {
        String val;
        if(input.startsWith("$")) {
            val = realVal(input.replaceAll("\\$", ""));
        } else {
            val = realVal(input.replace("%", ""));
        }
        return new BigDecimal(val);
    }

    public String realVal(String val) {
        if(val.equals(".") || val.equals("") || val.equals("-")) {
            val = "0";
        }
        return val;
    }

    public BigDecimal inputToDecimalSplit(String input) {
        if(input.equals("")){
            input = "1";
        }
        return new BigDecimal(input);
    }

    public String getInputFor(EditText editText) {
        String input = String.valueOf(editText.getText());
        /*if(input.length() < 2){
            input = "0";
        }*/
        return input;
    }

    public void setTotals() {
        tip_total_edit.setText("$" + String.valueOf(result.tipTotal));
        total_edit.setText("$" + String.valueOf(result.amountTotal));
        per_person_edit.setText("$" + String.valueOf(result.amountPerPerson));
    }

    public void onTextchangedFor(int which) {
        String input;

        switch (which) {

            case 1:
                doSignBehavior(amount_due_edit, "$", 0);
                input = getInputFor(amount_due_edit);
                result.editAmountDue(inputToDecimal(input));
                setTotals();
                break;

            case 2:
                doSignBehavior(tip_edit, "%", 1);
                input = getInputFor(tip_edit);
                result.editTipPercent(inputToDecimal(input));
                setTotals();
                break;

            case 3:
                doSignBehavior(tip_total_edit, "$", 0);
                input = getInputFor(tip_total_edit);
                result.editTiptotal(inputToDecimal(input));
                tip_edit.setText(String.valueOf(result.tipPercent) + "%");
                total_edit.setText("$" + String.valueOf(result.amountTotal));
                per_person_edit.setText("$" + String.valueOf(result.amountPerPerson));
                break;

            case 4:
                doSignBehavior(total_edit, "$", 0);
                input = getInputFor(total_edit);
                result.editTotal(inputToDecimal(input));
                tip_edit.setText(String.valueOf(result.tipPercent) + "%");
                tip_total_edit.setText("$" + String.valueOf(result.tipTotal));
                per_person_edit.setText("$" + String.valueOf(result.amountPerPerson));
                break;

            case 5:
                if(persons_edit.getText().length() >= 2) {
                    persons_edit.setKeyListener(DigitsKeyListener.getInstance("")); //allow only 2
                } else {
                    persons_edit.setKeyListener(DigitsKeyListener.getInstance("123456789"));
                }
                input = String.valueOf(persons_edit.getText());
                result.editSplit(inputToDecimalSplit(input));
                setTotals();
                break;

            case 6:
                doSignBehavior(per_person_edit, "$", 0);
                input = getInputFor(per_person_edit);
                result.editPerPerson(inputToDecimal(input));
                tip_total_edit.setText("$" + String.valueOf(result.tipTotal));
                total_edit.setText("$" + String.valueOf(result.amountTotal));
                tip_edit.setText(String.valueOf(result.tipPercent) + "%");
                break;

            default:
                break;
        }
    }

    public void removeTextWatcherFor(int which){

        switch (which) {

            case 1:
                amount_due_edit.removeTextChangedListener(textWatcher);
                break;

            case 2:
                tip_edit.removeTextChangedListener(textWatcher);
            break;

            case 3:
                tip_total_edit.removeTextChangedListener(textWatcher);
                break;

            case 4:
                total_edit.removeTextChangedListener(textWatcher);
                break;

            case 5:
                persons_edit.removeTextChangedListener(textWatcher);
                break;

            case 6:
                per_person_edit.removeTextChangedListener(textWatcher);
                break;

            default:
                break;
        }
    }

    public void clearEdits() {
        tip_total_edit.setText("$" + String.valueOf(result.tipTotal));
        total_edit.setText("$" + String.valueOf(result.amountTotal));
        per_person_edit.setText("$" + String.valueOf(result.amountPerPerson));
        tip_edit.setText(String.valueOf(result.tipPercent) + "%");
        persons_edit.setText("1");
        amount_due_edit.setText("$");
        amount_due_layout.performClick();
        doSignBehavior(amount_due_edit, "$", 0);
    }

}
