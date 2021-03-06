package sourceit.com.myspannablestring;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static sourceit.com.myspannablestring.utils_spannable_string.UtilsDates.DATE_REGEXPS;
import static sourceit.com.myspannablestring.utils_spannable_string.UtilsDates.DAYS_OF_THE_WEEK;
import static sourceit.com.myspannablestring.utils_spannable_string.UtilsDates.TIME_WORDS;
import static sourceit.com.myspannablestring.utils_spannable_string.UtilsDates.getCurrentDay;
import static sourceit.com.myspannablestring.utils_spannable_string.UtilsDates.getCurrentHour;
import static sourceit.com.myspannablestring.utils_spannable_string.UtilsDates.getCurrentMinute;
import static sourceit.com.myspannablestring.utils_spannable_string.UtilsDates.getCurrentMonth;
import static sourceit.com.myspannablestring.utils_spannable_string.UtilsDates.getCurrentYear;
import static sourceit.com.myspannablestring.utils_spannable_string.UtilsDates.getDifferenceBetweenPlannedDayAndToday;
import static sourceit.com.myspannablestring.utils_spannable_string.UtilsWords.getIntMonthFromString;

public class MainActivity extends AppCompatActivity {

    public static SpannableString bufferSpannableString = null;

    String initialWord;
    String strRegExp;

    @BindView(R.id.et)
    EditText et;


    @BindView(R.id.btn_clear)
    Button btnClear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        et.addTextChangedListener(onTextChangedListener());
        btnClear.setOnClickListener(onButtonClickListener());
    }


    private View.OnClickListener onButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setText("");
            }
        };
    }

    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                et.removeTextChangedListener(this);

                try {
                    String initialString = s.toString();
                    SpannableString spannableString = new SpannableString(initialString);
                    bufferSpannableString = new SpannableString(spannableString);


                    for (String days : DAYS_OF_THE_WEEK.keySet()) {
                        initialWord = days;
                        spannableString = convertString(bufferSpannableString, initialWord);
                        bufferSpannableString = spannableString;
                    }


                    for (String timeWords : TIME_WORDS.keySet()) {
                        initialWord = timeWords;
                        spannableString = convertString(bufferSpannableString, initialWord);
                        bufferSpannableString = spannableString;
                    }


                    for (String dateRegExp : DATE_REGEXPS) {
                        strRegExp = dateRegExp;
                        spannableString = convertDateRegExps(bufferSpannableString, strRegExp);
                        bufferSpannableString = spannableString;
                    }


                    et.setText(bufferSpannableString);

                    et.setLinksClickable(true);
                    et.setMovementMethod(LinkMovementMethod.getInstance());
                    et.setSelection(et.getText().length());


                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
                et.addTextChangedListener(this);
            }
        };
    }


    public SpannableString convertDateRegExps(SpannableString initialSpannableString, String strRegExp) {

        Pattern p = Pattern.compile(strRegExp);
        Matcher m = p.matcher(initialSpannableString.toString().replaceAll("[\\p{Cf}]", ""));

        SpannableString newSpannableString = new SpannableString(initialSpannableString);
        while (m.find()) {

            final int day = Integer.parseInt(m.group(1));

            final int month;
            if (m.group(3) != null) {
                month = getIntMonthFromString(m.group(3));
            } else {
                month = getCurrentMonth();
            }


            int year1 = getCurrentYear();
            if (m.group(5) != null) {
                year1 = Integer.parseInt(m.group(5));
                if (year1 < 100) {
                    if (year1 < 50) {
                        year1 += 2000;
                    } else {
                        year1 += 1900;
                    }
                }
            }

            final int year = year1;

            final int hour = getCurrentHour();
            final int minute = getCurrentMinute();


//String regExp = "(\\d{1,2})(\\s*)(января|янв|февраля|фев|марта|мар|апреля|апр|май|мая|июня|июн|июля|июл|августа|авг|сентября|сен|октября|окт|ноября|ноя|декабря|дек)(\\s*)(\\d{2,4}){0,1}";


            if (m.group(5) != null) {

                newSpannableString.setSpan(new ClickableSpan() {

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    public void onClick(View widget) {

                        Intent calIntent = createCalendarIntent(year, month, day, hour, minute);
                        startActivity(calIntent);

                    }
                }, m.start(1), m.end(5), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            } else if (m.group(3) != null) {

                newSpannableString.setSpan(new ClickableSpan() {

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    public void onClick(View widget) {

                        Intent calIntent = createCalendarIntent(year, month, day, hour, minute);
                        startActivity(calIntent);


                    }
                }, m.start(1), m.end(3), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {

                newSpannableString.setSpan(new ClickableSpan() {

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    public void onClick(View widget) {

                        Intent calIntent = createCalendarIntent(year, month, day, hour, minute);
                        startActivity(calIntent);

                    }
                }, m.start(1), m.end(1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return newSpannableString;
    }

    @NonNull
    private Intent createCalendarIntent(int year, int month, int day, int hour, int minute) {
        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setType("vnd.android.cursor.item/event");

        GregorianCalendar calDateBegin = new GregorianCalendar(
                year,
                month,
                day
        );
        GregorianCalendar calDateEnd = new GregorianCalendar(
                year,
                month,
                day
        );

        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                calDateBegin.getTimeInMillis() + hour * 60 * 60 * 1000 + minute * 60 * 1000);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                calDateEnd.getTimeInMillis() + hour * 60 * 60 * 1000 + minute * 60 * 1000);
        return calIntent;
    }


    public SpannableString convertString(SpannableString initialSpannableString, final String initialWord) {

        List<Integer> indexesOfFirstLetters = getIndexesOfFirstLetters(initialSpannableString, initialWord);

        SpannableString newSpannableString = new SpannableString(initialSpannableString);

        for (Integer indexesOfFirstLetter : indexesOfFirstLetters) {
            newSpannableString.setSpan(new ClickableSpan() {

                @RequiresApi(api = Build.VERSION_CODES.N)
                public void onClick(View widget) {

                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                    calIntent.setType("vnd.android.cursor.item/event");


                    int beginDay = getCurrentDay();
                    int beginMonth = getCurrentMonth();
                    int beginYear = getCurrentYear();

                    int beginHour = getCurrentHour();
                    int beginMinute = getCurrentMinute();

                    int endDay = getCurrentDay();
                    int endMonth = getCurrentMonth();
                    int endYear = getCurrentYear();

                    int endHour = getCurrentHour();
                    int endMinute = getCurrentMinute();

                    GregorianCalendar calDateBegin = new GregorianCalendar(
                            beginYear,
                            beginMonth,
                            beginDay + getDifferenceBetweenPlannedDayAndToday(initialWord)
                    );
                    GregorianCalendar calDateEnd = new GregorianCalendar(
                            endYear,
                            endMonth,
                            endDay + getDifferenceBetweenPlannedDayAndToday(initialWord)
                    );

                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                            calDateBegin.getTimeInMillis() + beginHour * 60 * 60 * 1000 + beginMinute * 60 * 1000);
                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                            calDateEnd.getTimeInMillis() + endHour * 60 * 60 * 1000 + endMinute * 60 * 1000);

                    startActivity(calIntent);

                }
            }, indexesOfFirstLetter, indexesOfFirstLetter + initialWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return newSpannableString;
    }


    public List<Integer> getIndexesOfFirstLetters(SpannableString initialSpannableString, String initialWord) {
        List<Integer> indexesOfFirstLetters = new ArrayList<Integer>();

        StringBuilder text = new StringBuilder(initialSpannableString);
        StringBuilder newString = new StringBuilder("");
        int intIndex;
        while (text.length() > initialWord.length()) {
            intIndex = text.indexOf(initialWord);
            if (intIndex == -1) {
                newString.append(text);
                break;
            } else {
                indexesOfFirstLetters.add(newString.length() + intIndex);
                newString.append(text.substring(0, intIndex)).append(initialWord);
                text = new StringBuilder(text.substring(intIndex + initialWord.length()));
            }
        }
        return indexesOfFirstLetters;
    }
}
