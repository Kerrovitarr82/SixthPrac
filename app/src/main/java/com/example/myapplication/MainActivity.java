package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.data.UserContract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final String APP_REFERENCES = "mysettings";
    public static final String APP_PREFERENCES_NAME = "Nickname";
    public final static String FILE_NAME = "context.txt";
    public final static String FILE_NAME_EXTERNAL = "document.txt";
    SharedPreferences mSettings;

    ListView userList;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button saveBtn = findViewById(R.id.saveBtn);
        EditText nickname = findViewById(R.id.nickname);
        Button saveBtn2 = findViewById(R.id.saveBtn2);
        Button openBtn = findViewById(R.id.openBtn);
        Button saveBtn4 = findViewById(R.id.saveBtn4);
        Button openBtn2 = findViewById(R.id.openBtn2);
        Button deleteBtn = findViewById(R.id.deleteBtn);

        userList = findViewById(R.id.list);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        databaseHelper = new DatabaseHelper(getApplicationContext());
        mSettings = getSharedPreferences(APP_REFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_PREFERENCES_NAME)) {
            nickname.setText(mSettings.getString(APP_PREFERENCES_NAME, ""));
        }
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strNickname = nickname.getText().toString();
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(APP_PREFERENCES_NAME, strNickname);
                editor.apply();
            }
        });
        saveBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveText(view);
            }
        });
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openText(view);
            }
        });
        saveBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveText2(view);
            }
        });
        openBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openText2(view);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        db = databaseHelper.getReadableDatabase();
        userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE, null);
        String[] headers = new String[]{UserContract.UserEntry.COLUMN_NAME, UserContract.UserEntry.COLUMN_YEAR};
        userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        userList.setAdapter(userAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
        userCursor.close();
    }

    public void add(View view) {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    public void saveText(View view) {
        try (FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), FILE_NAME))) {
            EditText textBox = findViewById(R.id.nickname2);
            String text = textBox.getText().toString();
            fos.write(text.getBytes());
            File file = new File(getFilesDir(), FILE_NAME);
            Toast.makeText(this, "Файл сохранен " + Math.round(file.getFreeSpace() / 1024.0 / 1024.0) + " Gb", Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void openText(View view) {
        TextView textView = findViewById(R.id.text);
        try (FileInputStream fin = new FileInputStream(new File(getFilesDir(), FILE_NAME))) {
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String(bytes);
            textView.setText(text);
        } catch (IOException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void saveText2(View view) {
        try (FileOutputStream fos = new FileOutputStream(getExternalPath())) {
            EditText textBox = findViewById(R.id.nickname4);
            String text = textBox.getText().toString();
            fos.write(text.getBytes());
            Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void openText2(View view) {
        TextView textView = findViewById(R.id.text3);
        File file = getExternalPath();
        if (!file.exists()) {
            return;
        }
        try (FileInputStream fin = new FileInputStream(file)) {
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String(bytes);
            textView.setText(text);
        } catch (IOException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteFile() throws IOException {
        File file = new File(getFilesDir(), FILE_NAME);
        boolean deleted = file.delete();
        Toast.makeText(this, "Файл удален " + deleted, Toast.LENGTH_SHORT).show();
    }

    private File getExternalPath() {
        return new File(getExternalFilesDir(null), FILE_NAME_EXTERNAL);
    }
}