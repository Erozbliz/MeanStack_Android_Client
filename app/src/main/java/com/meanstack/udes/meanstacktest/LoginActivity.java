package com.meanstack.udes.meanstacktest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };


    /**
     * Log
     */
    private static final String LOG_TAG = "test";

    private static final String IPMEAN = "http://192.168.0.101:3000";



    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    //UI MEAN
    //Inscription
    private TextView mTextViewInfo;
    private AutoCompleteTextView mNameView;
    private AutoCompleteTextView mFavView;
    //Connexion
    private TextView mTextInfoConnectView;
    private AutoCompleteTextView mIdConnectView;
    //Edit
    private TextView mTextEditView;
    private AutoCompleteTextView mNameEditView;
    private AutoCompleteTextView mFavEditView;



    String PREFS_NAME = "LOCAL";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        //TEMPLATE
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //MEAN inscription
        mTextViewInfo = (TextView) findViewById(R.id.textViewInfo);
        mNameView = (AutoCompleteTextView) findViewById(R.id.name_mean);
        mFavView = (AutoCompleteTextView) findViewById(R.id.fav_mean);
        Button mMeanSignInButton = (Button) findViewById(R.id.mean_inscription_button);
        mMeanSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Bouton inscription");
                Toast.makeText(getApplicationContext(), "Inscription", Toast.LENGTH_SHORT).show();

                String name = mNameView.getText().toString();
                String fav = mFavView.getText().toString();

                JsonPostRequest jsonPostRequest = new JsonPostRequest(name,fav);
                jsonPostRequest.execute();
            }
        });

        //MEAN Connexion
        mTextInfoConnectView = (TextView) findViewById(R.id.textViewInfoConnect);
        mIdConnectView = (AutoCompleteTextView) findViewById(R.id.id_mean_connect);
        Button mMeanConnectButton = (Button) findViewById(R.id.mean_connect_button);
        mMeanConnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Bouton Connexion");
                Toast.makeText(getApplicationContext(), "Connexion", Toast.LENGTH_SHORT).show();

                String myId = mIdConnectView.getText().toString();

                JsonConnectRequest jsonConnectRequest = new JsonConnectRequest(myId);
                jsonConnectRequest.execute();
            }
        });


        //MEAN edit
        mTextEditView = (TextView) findViewById(R.id.textViewInfoEdit);
        mNameEditView = (AutoCompleteTextView) findViewById(R.id.name_edit_mean);
        mFavEditView = (AutoCompleteTextView) findViewById(R.id.fav_edit_mean);
        Button mMeanEditButton = (Button) findViewById(R.id.mean_edit_button);
        mMeanEditButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Bouton Edit");
                Toast.makeText(getApplicationContext(), "Edit", Toast.LENGTH_SHORT).show();

                String myId = getIdSharedPreference(); //Provient de sharedPreference
                String name = mNameEditView.getText().toString();
                String fav = mFavEditView.getText().toString();


                //A TERMINER FAIRE UNE METHODE PUT <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                //JsonConnectRequest jsonConnectRequest = new JsonConnectRequest(myId);
                //jsonConnectRequest.execute();
                JsonEditRequest jsonEditRequest = new JsonEditRequest(myId,name,fav);
                jsonEditRequest.execute();
            }
        });

        //affiche au lancement les données local de sharedPreference
        printFromSharedPreference();

        //Déconnexion (vide le sharePreference)
        Button mMeanLogoutButton = (Button) findViewById(R.id.mean_logout_button);
        mMeanLogoutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Bouton Logout");
                Toast.makeText(getApplicationContext(), "Déconnexion", Toast.LENGTH_SHORT).show();
                flushSharedPreference();
            }
        });








        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    /**
     * TEMPLATE
     * Inscription mean stack appel sur le bouton inscription
     */
    private class LoginRequest extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String address = "http://server/login";
            HttpURLConnection urlConnection;
            String requestBody;
            Uri.Builder builder = new Uri.Builder();
            Map<String, String> params = new HashMap<>();
            params.put("username", "bnk");
            params.put("password", "bnk123");


            // encode parameters
            Iterator entries = params.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                builder.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
                entries.remove();
            }
            requestBody = builder.build().getEncodedQuery();

            try {
                URL url = new URL(address);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                writer.write(requestBody);
                writer.flush();
                writer.close();
                outputStream.close();

                JSONObject jsonObject = new JSONObject();
                InputStream inputStream;
                // get stream
                if (urlConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                    inputStream = urlConnection.getInputStream();
                } else {
                    inputStream = urlConnection.getErrorStream();
                }
                // parse stream
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp, response = "";
                while ((temp = bufferedReader.readLine()) != null) {
                    response += temp;
                }
                // put into JSONObject
                jsonObject.put("Content", response);
                jsonObject.put("Message", urlConnection.getResponseMessage());
                jsonObject.put("Length", urlConnection.getContentLength());
                jsonObject.put("Type", urlConnection.getContentType());

                return jsonObject.toString();
            } catch (IOException | JSONException e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i(LOG_TAG, "POST\n" + result);
        }
    }

    /**
     * MEAN STACK POST
     * Asynctask pour l'inscription (bien vérifier l'adresse ip)
     */
    private class JsonPostRequest extends AsyncTask<Void, Void, String> {


        private final String mName;
        private final String mFav;

        public static final int READ_TIMEOUT = 3000;
        public static final int CONNECTION_TIMEOUT = 3000;

        //Contructeur par defaut
        JsonPostRequest(String name, String fav) {
            mName = name;
            mFav = fav;
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                String address = IPMEAN+"/api/users";
                JSONObject json = new JSONObject();
                json.put("name", mName);
                json.put("fav", mFav);
                String requestBody = json.toString();
                URL url = new URL(address);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(READ_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                writer.write(requestBody);
                writer.flush();
                writer.close();
                outputStream.close();

                InputStream inputStream;
                // get stream
                if (urlConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                    inputStream = urlConnection.getInputStream();
                } else {
                    inputStream = urlConnection.getErrorStream();
                }
                // parse stream
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp, response = "";
                while ((temp = bufferedReader.readLine()) != null) {
                    response += temp;
                }
                // put into JSONObject
                JSONObject jsonObject = new JSONObject();
                //don't delete
                //jsonObject.put("Content", response);
                //jsonObject.put("Message", urlConnection.getResponseMessage());
                //jsonObject.put("Length", urlConnection.getContentLength());
                //jsonObject.put("Type", urlConnection.getContentType());
                //return jsonObject.toString();
                Log.i(LOG_TAG, "MESSAGE RESPONSE: " + urlConnection.getResponseMessage());


                return response;
            } catch (IOException | JSONException e) {
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(String result) {
            mTextViewInfo.setText(result);
            super.onPostExecute(result);
            parseStringToJson(result);
            Log.i(LOG_TAG, "POST RESPONSE: " + result);
            Toast.makeText(getApplicationContext(), "Détail Inscription"+ result, Toast.LENGTH_LONG).show();
            //mTextViewInfo.setText(result);

        }
    }



    /**
     * MEAN STACK GET
     * https://medium.com/@JasonCromer/android-asynctask-http-request-tutorial-6b429d833e28#.tmg8z8c1t
     * Asynctask pour la connexion (bien vérifier l'adresse ip)
     * Void, void , string
     */
    private class JsonConnectRequest extends AsyncTask<Void, Void, String> {


        private final String mId;

        //Contructeur par defaut
        JsonConnectRequest(String id) {
            mId = id;
        }




        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 3000;
        public static final int CONNECTION_TIMEOUT = 3000;

        @Override
        protected String doInBackground(Void... voids){

            //Nécessaire car si on envoie une url sans l'id le serveur nous renvoie tout les utilisateurs
            String idFinal = mId;
            if(mId.isEmpty()){
                idFinal="vide";
            }
            String stringUrl = IPMEAN+"/api/users/"+idFinal;
            Log.d("-------------", stringUrl);

            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                result = e.toString();
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result){
            mTextInfoConnectView.setText(result);
            super.onPostExecute(result);
            parseStringToJson(result); //parse et sauvegarde localement les données
            printFromSharedPreference(); //affiche les donées de SharedPreferences
        }
    }


    /**
     * MEAN STACK PUT
     * Asynctask pour la modification d'un profil (bien vérifier l'adresse ip)
     * Void, void , string
     */
    private class JsonEditRequest extends AsyncTask<Void, Void, String> {


        private final String mId; //Doit provenir de sharedPreference
        private final String mName;
        private final String mFav;

        //Contructeur par defaut
        JsonEditRequest(String id, String name, String fav) {
            mId = id;
            mName = name;
            mFav = fav;
        }


        public static final String REQUEST_METHOD = "PUT";
        public static final int READ_TIMEOUT = 3000;
        public static final int CONNECTION_TIMEOUT = 3000;

        @Override
        protected String doInBackground(Void... voids){
            String stringUrl = IPMEAN+"/api/users/"+mId;


            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                JSONObject json = new JSONObject();
                json.put("name", mName);
                json.put("fav", mFav);
                String requestBody = json.toString();
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                connection.setRequestProperty("Content-Type", "application/json");
                OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                writer.write(requestBody);
                writer.flush();
                writer.close();
                outputStream.close();

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());

                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();


                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            }
            catch(IOException | JSONException e){
                e.printStackTrace();
                result = e.toString();
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result){
            //mTextEditView.setText(result); //contient les données avant la modification
            super.onPostExecute(result);
            JsonConnectRequest jsonConnectRequest = new JsonConnectRequest(getIdSharedPreference()); //on met à jour apres l'update qu'on a fait
            jsonConnectRequest.execute();


            //parseStringToJson(result); //parse et sauvegarde localement les données
            //printFromSharedPreference(); //affiche les donées de SharedPreferences
        }
    }


    /**
     * --------------------------------------------------------------------
     *                              TOOLS
     *  --------------------------------------------------------------------
     */

    /**
     * TOOL : parse my String to JSON + change mTextViewInfo + sharedPreference
     * @param myJsonString contient simplement le contenu (pas de header etc)
     */
    public void parseStringToJson(String myJsonString){
        try {

            Log.d("------------------> ", myJsonString);
            JSONObject json = new JSONObject(myJsonString); // convert String to JSONObject
            Log.d("-------------", "Taille "+json.length());
            Log.d("-------------", json.getString("name"));
            Log.d("-------------", json.getString("_id"));
            Log.d("-------------", json.getString("fav"));
            Toast.makeText(getApplicationContext(), "ID : "+json.getString("_id")+", Nom :"+ json.getString("name") +", fav :"+ json.getString("fav"), Toast.LENGTH_LONG).show();

            mTextViewInfo.setText("ID : "+json.getString("_id")+", Nom :"+ json.getString("name") +", fav :"+ json.getString("fav"));


            SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE); //MODE_PRIVATE
            SharedPreferences.Editor editor = settings.edit();

            //sauvegarde local
            editor.putString("save_name", json.getString("name"));
            editor.putString("save_id", json.getString("_id"));
            editor.putString("save_fav", json.getString("fav"));
            editor.commit();



        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + myJsonString + "\"");
        }
    }


    /**
     *
     * Cherche si on a sauvegardé des données + affiche
     * Utilisé au lancement de l'application
     */
    public void printFromSharedPreference(){

        SharedPreferences settings;

        settings = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE); //1
        String save_name = settings.getString("save_name", null);
        String save_id = settings.getString("save_id", null);
        String save_fav = settings.getString("save_fav", null);
        if(save_id!=null){
            mTextEditView.setText("id:"+save_id+" nom:"+save_name+" ville:"+save_fav);
            Toast.makeText(getApplicationContext(), "Donnée Local trouvé. Bonjour "+save_name, Toast.LENGTH_SHORT).show();
        }else{
            mTextEditView.setText("Aucune données dans SharedPreferences");

        }
    }

    /**
     * Permet de retourner l'id sauvegardé par sharedPreference
     * Utilisé pour la modification d'un profil
     * @return l'id sauvegardé localement
     */
    public String getIdSharedPreference(){

        SharedPreferences settings;
        settings = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE); //1
        String save_id = settings.getString("save_id", null);

        return save_id;
    }

    /**
     * Déconnexion
     * Méthode qui vide simplement le SharedPreference
     */
    public void flushSharedPreference(){

        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.commit();
        Toast.makeText(getApplicationContext(), "Données Locales nettoyé", Toast.LENGTH_SHORT).show();
        mTextEditView.setText("VIDE");
    }



}

