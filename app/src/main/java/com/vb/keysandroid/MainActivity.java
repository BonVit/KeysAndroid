package com.vb.keysandroid;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private List<Client> mClients;
    private List<Pair<String, BigInteger>> mKeys;

    private EditText mEditTextA;
    private EditText mEditTextP;
    private Button mButton;
    private TextView mTextViewNumberUsers;
    private SeekBar mSeekBarNumberUsers;
    private ProgressBar mProgressBarLoading;
    private RecyclerView mRecyclerViewKeys;
    private KeyAdapter mRecyclerViewKeysAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClients = new ArrayList<Client>();
        mKeys = new ArrayList<Pair<String, BigInteger>>();

        mEditTextA = (EditText) findViewById(R.id.editTextA);
        mEditTextP = (EditText) findViewById(R.id.editTextP);

        mSeekBarNumberUsers = (SeekBar) findViewById(R.id.seekBarNumberUsers);
        mSeekBarNumberUsers.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mTextViewNumberUsers.setText(String.format(getResources().getString(R.string.NumberUsers),
                        mSeekBarNumberUsers.getProgress() + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mTextViewNumberUsers = (TextView) findViewById(R.id.textViewNumberUsers);
        mTextViewNumberUsers.setText(String.format(getResources().getString(R.string.NumberUsers),
                mSeekBarNumberUsers.getProgress() + 1));

        mButton = (Button) findViewById(R.id.buttonFindKeys);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                loading(true);
                (new FindKeysAsyncTask()).execute(mEditTextA.getText().toString(),
                        mEditTextP.getText().toString(),
                        Integer.toString(mSeekBarNumberUsers.getProgress() + 1));
            }
        });

        mProgressBarLoading = (ProgressBar) findViewById(R.id.loadingBar);

        mRecyclerViewKeys = (RecyclerView) findViewById(R.id.recyclerViewKeys);
        mRecyclerViewKeys.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewKeysAdapter = new KeyAdapter(mKeys);
        mRecyclerViewKeys.setAdapter(mRecyclerViewKeysAdapter);
        mRecyclerViewKeys.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mButton.setEnabled(true);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy == 0)
                    return;
                mButton.setEnabled(false);
            }
        });

        loading(false);
    }

    private void loading(boolean isLoading)
    {
        if(isLoading)
        {
            mProgressBarLoading.setVisibility(View.VISIBLE);
            mRecyclerViewKeys.setVisibility(View.GONE);
        }
        else
        {
            mProgressBarLoading.setVisibility(View.GONE);
            mRecyclerViewKeys.setVisibility(View.VISIBLE);
        }

        mButton.setEnabled(!isLoading);
    }

    private void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mButton.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private class KeyHolder extends RecyclerView.ViewHolder {
        public TextView mNameTextView;
        private TextView mKeyTextView;

        private Pair<String, BigInteger> mKey;

        public KeyHolder(View itemView) {
            super(itemView);

            mNameTextView = (TextView) itemView.findViewById(R.id.itemKeyTextViewName);
            mKeyTextView = (TextView) itemView.findViewById(R.id.itemKeyTextViewKey);
        }

        public void bindKey(Pair<String, BigInteger> key) {
            this.mKey = key;
            mNameTextView.setText(mKey.first);
            mKeyTextView.setText(mKey.second.toString());
        }
    }

    private class KeyAdapter extends RecyclerView.Adapter<KeyHolder> {
        private List<Pair<String, BigInteger>> mKeys;

        public KeyAdapter(List<Pair<String, BigInteger>> keys) {
            mKeys = keys;
        }

        public void setKeys(List<Pair<String, BigInteger>> keys) {
            mKeys = keys;
        }

        @Override
        public KeyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            View view = layoutInflater.inflate(R.layout.item_key, parent, false);
            return new KeyHolder(view);
        }

        @Override
        public void onBindViewHolder(KeyHolder holder, int position) {
            Pair<String, BigInteger> key = mKeys.get(position);
            holder.bindKey(key);
        }
        @Override
        public int getItemCount() {
            return mKeys.size();
        }
    }


    private class FindKeysAsyncTask extends AsyncTask<String, Object, Pair<Boolean, String>>
    {
        @Override
        protected Pair<Boolean, String> doInBackground(String... args) throws IllegalArgumentException {
            if(args.length != 3)
                return new Pair<Boolean, String>(false, "Must be 3 args!");

            if(args[0].length() == 0 || args[1].length() == 0 || args[2].length() == 0)
                return new Pair<Boolean, String>(false, "Empty values!");

            mKeys.clear();

            int numberClients = Integer.parseInt(args[2]);
            mClients.clear();

            for(int i = 0; i < numberClients; i++)
            {
                mClients.add(new Client(args[0], args[1]));
                mKeys.add(new Pair<String, BigInteger>("Public key ("  + (i + 1) + "): "
                        , mClients.get(i).getPublicKey()));
            }

            for(int i = 0; i < numberClients; i++)
            {
                for(int j = i + 1; j < numberClients; j++)
                {
                    mKeys.add(new Pair<String, BigInteger>("Shared key ("  + (i + 1) + "-" + (j + 1) + "): "
                        , mClients.get(i).getSharedKey(mClients.get(j).getPublicKey())));
                    mKeys.add(new Pair<String, BigInteger>("Shared key ("  + (j + 1) + "-" + (i + 1) + "): "
                            , mClients.get(j).getSharedKey(mClients.get(i).getPublicKey())));
                }
            }

            return new Pair<Boolean, String>(true, "Okay!");
        }

        @Override
        protected void onPostExecute(Pair<Boolean, String> result) {
            loading(false);
            if(!result.first)
            {
                Toast.makeText(getApplicationContext(), result.second, Toast.LENGTH_SHORT).show();
                return;
            }

            mRecyclerViewKeysAdapter.notifyDataSetChanged();
        }
    }
}


