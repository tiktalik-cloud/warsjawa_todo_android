package com.tiktalik.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends Activity {
    Button addButton;
    ListView listView;
    EditText editText;
    TodoAdapter todoAdapter;
    LocalBroadcastManager localBroadcastManager;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BackendService.GET_ITEMS.equals(action)) {
                ArrayList<Item> items = intent.getParcelableArrayListExtra("items");
                if (items != null && items.size() > 0) {
                    todoAdapter.addAll(items);
                    todoAdapter.notifyDataSetChanged();
                }
            } else if (BackendService.ERROR.equals(action)) {
                Toast toast = Toast.makeText(context, intent.getStringExtra("error"), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    };

    private class TodoAdapter extends ArrayAdapter<Item> {
        private Context mContext;

        ArrayList<Item> itemsCopy = new ArrayList<Item>();

        public TodoAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
            mContext = context;
        }

        @Override
        public void addAll(Collection<? extends Item> collection) {
            super.addAll(collection);
            itemsCopy.addAll(collection);
        }

        @Override
        public void add(Item object) {
            super.add(object);
            itemsCopy.add(object);
        }

        public ArrayList<Item> getItems() {
            return itemsCopy;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.list_item, null);
            }

            Item item = getItem(position);

            TextView textView;

            textView = (TextView) convertView.findViewById(R.id.position);
            textView.setText(String.valueOf(position));

            textView = (TextView) convertView.findViewById(R.id.todo);
            textView.setText(item.getTodo());

            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        todoAdapter = new TodoAdapter(this);

        editText = (EditText) findViewById(R.id.editText);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(todoAdapter);

        final Context ctx = this;
        addButton = (Button) findViewById(R.id.button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getEditableText().toString().length() == 0)
                    return;

                Item item = new Item();
                item.setTodo(editText.getEditableText().toString());
                todoAdapter.add(item);
                editText.setText("");

                Intent in = new Intent(ctx, BackendService.class);
                in.setAction(BackendService.ADD_ITEM);
                in.putExtra("item", item);
                startService(in);
            }
        });

        if (savedInstanceState == null) {
            Intent in = new Intent(ctx, BackendService.class);
            in.setAction(BackendService.GET_ITEMS);
            startService(in);
        } else {
            ArrayList<Item> items = savedInstanceState.getParcelableArrayList("items");
            todoAdapter.addAll(items);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("items", todoAdapter.getItems());
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(BackendService.GET_ITEMS);
        intentFilter.addAction(BackendService.ERROR);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
